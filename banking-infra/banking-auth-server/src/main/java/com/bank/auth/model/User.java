package com.bank.auth.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.JoinColumn;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(unique = true, nullable = false, length = 50)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(length = 20)
    private String phone;
    
    @Column(name = "first_name", nullable = false)
    private String firstName;
    
    @Column(name = "last_name", nullable = false)
    private String lastName;
    
    @Column(name = "client_id", unique = true, nullable = false)
    @Builder.Default  // ← Важно! Сохраняет значение по умолчанию
    private String clientId = UUID.randomUUID().toString();
    
    @Column(name = "email_verified")
    @Builder.Default  // ← Важно!
    private boolean emailVerified = false;
    
    @Column(name = "phone_verified")
    @Builder.Default  // ← Важно!
    private boolean phoneVerified = false;
    
    @Builder.Default  // ← Важно!
    private boolean enabled = true;
    
    @Column(name = "account_locked")
    @Builder.Default  // ← Важно!
    private boolean accountLocked = false;
    
    @Column(name = "failed_attempts")
    @Builder.Default  // ← Важно!
    private int failedAttempts = 0;
    
    @Column(name = "created_at")
    @Builder.Default  // ← Важно!
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    @Builder.Default  // ← Важно!
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Builder.Default  // ← ВАЖНО: исправляет предупреждение!
    private Set<Role> roles = new HashSet<>();
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
        if (clientId == null) {
            clientId = UUID.randomUUID().toString();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}