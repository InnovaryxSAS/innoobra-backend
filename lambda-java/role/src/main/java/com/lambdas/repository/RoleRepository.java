package com.lambdas.repository;

import com.lambdas.model.Role;
import com.lambdas.model.RoleStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoleRepository {

    Role save(Role role);

    Optional<Role> findById(UUID id);

    List<Role> findAll();

    List<Role> findByStatus(RoleStatus status);

    List<Role> findByName(String name);

    Role update(Role role);

    boolean deactivate(UUID id);

    boolean existsById(UUID id);

    boolean existsByName(String name);

    long count();

    long countByStatus(RoleStatus status);

    String getConnectionPoolStats();

    boolean isHealthy();
}