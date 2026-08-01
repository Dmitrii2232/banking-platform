package com.bank.auth.repository;

import com.bank.auth.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
    Optional<Role> findByName(String name);
    Set<Role> findByNameIn(Set<String> names);
    boolean existsByName(String name);
}