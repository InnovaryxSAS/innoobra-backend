package com.lambdas.service;

import java.util.List;
import java.util.Optional;

import com.lambdas.model.Role;
import com.lambdas.model.RoleStatus;
import com.lambdas.repository.RoleRepository;
import com.lambdas.util.ValidationHelper;

public class RoleService {
    
    private final RoleRepository repository;
    
    public RoleService() {
        this.repository = new RoleRepository();
    }
    
    public RoleService(RoleRepository repository) {
        this.repository = repository;
    }
    
    public Role createRole(Role role) {
        ValidationHelper.validateAndThrow(role);
        return repository.save(role);
    }
    
    public List<Role> getAllRoles() {
        return repository.findAll();
    }
    
    public Optional<Role> getRoleById(String id) {
        return repository.findById(id);
    }
    
    public Role updateRole(Role role) {
        ValidationHelper.validateAndThrow(role);
        return repository.update(role);
    }
    
    public boolean deleteRole(String id) {
        return repository.deactivate(id);
    }
    
    public List<Role> getRolesByStatus(RoleStatus status) {
        return repository.findByStatus(status);
    }
    
    public List<Role> getRolesByName(String name) {
        return repository.findByName(name);
    }
    
    public boolean deactivateRole(String id) {
        return repository.deactivate(id);
    }

    public long countRoles() {
        return repository.count();
    }
    
    public long countRolesByStatus(RoleStatus status) {
        return repository.countByStatus(status);
    }
    
    public boolean existsRoleById(String id) {
        return repository.existsById(id);
    }
    
    public boolean existsRoleByName(String name) {
        return repository.existsByName(name);
    }
    
    public String getConnectionPoolStats() {
        return repository.getConnectionPoolStats();
    }
    
    public boolean isHealthy() {
        return repository.isHealthy();
    }
}