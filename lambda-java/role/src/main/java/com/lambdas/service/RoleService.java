package com.lambdas.service;

import com.lambdas.model.Role;
import com.lambdas.model.RoleStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoleService {

    Role createRole(Role role);

    List<Role> getAllRoles();

    Optional<Role> getRoleById(UUID id);
    
    Optional<Role> getRoleById(String id);

    Role updateRole(Role role);

    boolean deleteRole(UUID id);

    List<Role> getRolesByStatus(RoleStatus status);

    List<Role> getRolesByName(String name);

    boolean deactivateRole(UUID id);
    
    boolean deactivateRole(String id);

    long countRoles();

    long countRolesByStatus(RoleStatus status);

    boolean existsRoleById(UUID id);

    boolean existsRoleByName(String name);

    String getConnectionPoolStats();

    boolean isHealthy();
}