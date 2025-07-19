package com.lambdas.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.lambdas.model.User;
import com.lambdas.model.UserStatus;
import com.lambdas.repository.UserRepository;
import com.lambdas.repository.impl.UserRepositoryImpl;
import com.lambdas.service.UserService;
import com.lambdas.util.ValidationHelper;

public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    public UserServiceImpl() {
        this.repository = new UserRepositoryImpl();
    }

    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public User createUser(User user) {
        ValidationHelper.validateAndThrow(user);
        return repository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        return repository.findAll();
    }

    @Override
    public Optional<User> getUserById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return repository.findByEmail(email);
    }

    @Override
    public Optional<User> getUserByDocumentNumber(String documentNumber) {
        return repository.findByDocumentNumber(documentNumber);
    }

    @Override
    public List<User> getUsersByCompanyId(UUID companyId) {
        return repository.findByCompanyId(companyId);
    }

    @Override
    public List<User> getUsersByStatus(UserStatus status) {
        return repository.findByStatus(status);
    }

    @Override
    public User updateUser(User user) {
        ValidationHelper.validateAndThrow(user);
        return repository.update(user);
    }

    @Override
    public boolean deactivateUser(UUID id) {
        return repository.deactivate(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return repository.existsById(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public boolean existsByDocumentNumber(String documentNumber) {
        return repository.existsByDocumentNumber(documentNumber);
    }

    @Override
    public boolean updateLastAccess(UUID id) {
        return repository.updateLastAccess(id);
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