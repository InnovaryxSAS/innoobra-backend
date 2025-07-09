package com.lambdas.repository;

import com.lambdas.model.Role;
import com.lambdas.model.RoleStatus;

import java.util.List;
import java.util.Optional;

public interface RoleRepository {

    Role save(Role role);

    Optional<Role> findById(String idRole);

    List<Role> findAll();

    List<Role> findByStatus(RoleStatus status);

    List<Role> findByName(String name);

    Role update(Role role);

    boolean deactivate(String idRole);

    long count();

    long countByStatus(RoleStatus status);

    boolean existsById(String idRole);

    boolean existsByName(String name);

    String getConnectionPoolStats();

    boolean isHealthy();
}
