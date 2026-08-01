package com.bank.auth.service;

import com.bank.auth.config.RateLimitConfig;
import com.bank.auth.dto.LoginRequest;
import com.bank.auth.dto.RegisterRequest;
import com.bank.auth.dto.TokenResponse;
import com.bank.auth.dto.UserResponse;
import com.bank.auth.model.RefreshToken;
import com.bank.auth.model.Role;
import com.bank.auth.model.User;
import com.bank.auth.repository.RefreshTokenRepository;
import com.bank.auth.repository.RoleRepository;
import com.bank.auth.repository.UserRepository;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String BLACKLIST_PREFIX = "token:blacklist:";

    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Пользователь уже существует");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email уже используется");
        }

        Role userRole = roleRepository.findByName("ROLE_USER")
            .orElseThrow(() -> new RuntimeException("Роль не найдена"));

        User user = User.builder()
            .username(request.getUsername())
            .password(passwordEncoder.encode(request.getPassword()))
            .email(request.getEmail())
            .phone(request.getPhone())
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .clientId(UUID.randomUUID().toString())
            .roles(Set.of(userRole))
            .build();

        user = userRepository.save(user);
        log.info("User registered: {}", user.getUsername());
        return mapToUserResponse(user);
    }

    @Transactional
    public TokenResponse login(LoginRequest request) {
        // Rate limiting на логин
        if (!RateLimitConfig.checkLoginAttempt(request.getUsername(), redisTemplate)) {
            throw new BadCredentialsException("Слишком много попыток. Попробуйте позже.");
        }

        User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new BadCredentialsException("Неверный логин или пароль"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            user.setFailedAttempts(user.getFailedAttempts() + 1);
            if (user.getFailedAttempts() >= 5) {
                user.setAccountLocked(true);
            }
            userRepository.save(user);
            throw new BadCredentialsException("Неверный логин или пароль");
        }

        if (!user.isEnabled() || user.isAccountLocked()) {
            throw new BadCredentialsException("Аккаунт заблокирован");
        }

        // Сброс счётчиков
        user.setFailedAttempts(0);
        userRepository.save(user);
        RateLimitConfig.resetLoginAttempt(request.getUsername(), redisTemplate);

        Collection<String> roles = user.getRoles().stream()
            .map(Role::getName)
            .collect(Collectors.toList());

        String accessToken = jwtService.generateAccessToken(
            user.getId(), user.getUsername(), user.getClientId(), roles);
        String refreshToken = jwtService.generateRefreshToken(user.getId(), user.getUsername());

        saveRefreshToken(user, refreshToken);

        return TokenResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .tokenType("Bearer")
            .expiresIn(jwtService.getAccessTokenExpiration())
            .user(mapToUserResponse(user))
            .build();
    }

    @Transactional
    public TokenResponse refreshToken(String refreshTokenValue) {
        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshTokenValue)
            .orElseThrow(() -> new BadCredentialsException("Недействительный refresh токен"));

        if (storedToken.isRevoked() || storedToken.getExpiryDate().isBefore(Instant.now())) {
            throw new BadCredentialsException("Refresh токен отозван или истёк");
        }

        if (jwtService.isTokenExpired(refreshTokenValue)) {
            throw new BadCredentialsException("Refresh токен истёк");
        }

        User user = storedToken.getUser();
        storedToken.setRevoked(true);
        refreshTokenRepository.save(storedToken);

        Collection<String> roles = user.getRoles().stream()
            .map(Role::getName)
            .collect(Collectors.toList());

        String newAccessToken = jwtService.generateAccessToken(
            user.getId(), user.getUsername(), user.getClientId(), roles);
        String newRefreshToken = jwtService.generateRefreshToken(user.getId(), user.getUsername());

        saveRefreshToken(user, newRefreshToken);

        return TokenResponse.builder()
            .accessToken(newAccessToken)
            .refreshToken(newRefreshToken)
            .tokenType("Bearer")
            .expiresIn(jwtService.getAccessTokenExpiration())
            .user(mapToUserResponse(user))
            .build();
    }

    @Transactional
    public void logout(String accessToken, String refreshToken) {
        if (accessToken != null && !jwtService.isTokenExpired(accessToken)) {
            String username = jwtService.getUsernameFromToken(accessToken);
            Claims claims = jwtService.validateToken(accessToken);
            long ttl = claims.getExpiration().getTime() - System.currentTimeMillis();
            if (ttl > 0) {
                redisTemplate.opsForValue().set(
                    BLACKLIST_PREFIX + accessToken, username, Duration.ofMillis(ttl));
            }
        }

        if (refreshToken != null) {
            refreshTokenRepository.findByToken(refreshToken)
                .ifPresent(token -> {
                    token.setRevoked(true);
                    refreshTokenRepository.save(token);
                });
        }
    }

    public boolean isTokenBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + token));
    }

    private void saveRefreshToken(User user, String tokenValue) {
        RefreshToken token = RefreshToken.builder()
            .token(tokenValue)
            .user(user)
            .expiryDate(Instant.now().plusMillis(2592000000L))
            .revoked(false)
            .build();
        refreshTokenRepository.save(token);
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
            .id(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .clientId(user.getClientId())
            .emailVerified(user.isEmailVerified())
            .build();
    }
}