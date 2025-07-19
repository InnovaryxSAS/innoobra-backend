package com.lambdas.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.lambdas.model.Role;
import com.lambdas.model.RoleStatus;
import com.lambdas.repository.RoleRepository;
import com.lambdas.repository.impl.RoleRepositoryImpl;
import com.lambdas.util.ValidationHelper;
import com.lambdas.service.RoleService;

public class RoleServiceImpl implements RoleService {
    
    private final RoleRepository repository;
    
    public RoleServiceImpl() {
        this.repository = new RoleRepositoryImpl();
    }
    
    public RoleServiceImpl(RoleRepository repository) {
        this.repository = repository;
    }

    @Override
    public Role createRole(Role role) {
        ValidationHelper.validateAndThrow(role);
        return repository.save(role);
    }

    @Override
    public List<Role> getAllRoles() {
        return repository.findAll();
    }

    @Override
    public Optional<Role> getRoleById(UUID id) { 
        return repository.findById(id);
    }
    
    @Override
    public Optional<Role> getRoleById(String id) {
        try {
            UUID uuid = UUID.fromString(id);
            return repository.findById(uuid);
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    @Override
    public Role updateRole(Role role) {
        ValidationHelper.validateAndThrow(role);
        return repository.update(role);
    }

    @Override
    public boolean deleteRole(UUID id) {
        return repository.deactivate(id);
    }

    @Override
    public List<Role> getRolesByStatus(RoleStatus status) {
        return repository.findByStatus(status);
    }

    @Override
    public List<Role> getRolesByName(String name) {
        return repository.findByName(name);
    }

    @Override
    public boolean deactivateRole(UUID id) {  
        return repository.deactivate(id);
    }
    
    @Override
    public boolean deactivateRole(String id) {
        try {
            UUID uuid = UUID.fromString(id);
            return repository.deactivate(uuid);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public long countRoles() {
        return repository.count();
    }

    @Override
    public long countRolesByStatus(RoleStatus status) {
        return repository.countByStatus(status);
    }

    @Override
    public boolean existsRoleById(UUID id) {  
        return repository.existsById(id);
    }

    @Override
    public boolean existsRoleByName(String name) {
        return repository.existsByName(name);
    }

    @Override
    public String getConnectionPoolStats() {
        return repository.getConnectionPoolStats();
    }
    
    @Override
    public boolean isHealthy() {
        return repository.isHealthy();
    }
}