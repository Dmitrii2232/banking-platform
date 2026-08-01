package com.bank.query.projection;

import com.bank.domain.common.Money;
import com.bank.domain.common.ProductId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class BalanceProjection {

    private final RedisTemplate<String, Object> redisTemplate;
    private final JdbcTemplate jdbc;

    private static final String BALANCE_KEY_PREFIX = "balance:";

    public Optional<Money> getBalance(ProductId productId) {
        String cacheKey = BALANCE_KEY_PREFIX + productId;
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached instanceof Money money) return Optional.of(money);

        String sql = "SELECT balance_amount, balance_currency FROM products WHERE id = ?";
        List<Map<String, Object>> rows = jdbc.queryForList(sql, productId.getUuid());
        if (!rows.isEmpty()) {
            Map<String, Object> result = rows.get(0);
            BigDecimal amount = (BigDecimal) result.get("balance_amount");
            String currency = (String) result.get("balance_currency");
            Money balance = new Money(amount, currency);
            redisTemplate.opsForValue().set(cacheKey, balance, Duration.ofMinutes(1));
            return Optional.of(balance);
        }
        return Optional.empty();
    }

    public void updateBalance(ProductId productId, Money newBalance) {
        String sql = "UPDATE products SET balance_amount = ?, balance_currency = ?, updated_at = NOW() WHERE id = ?";
        jdbc.update(sql, newBalance.getAmount(), newBalance.getCurrency(), productId.getUuid());
        redisTemplate.delete(BALANCE_KEY_PREFIX + productId);
    }
}