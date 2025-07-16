package com.lambdas.service.impl;

import java.util.List;
import java.util.Optional;

import com.lambdas.model.User;
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
    public Optional<User> getUserById(String id) {
        return repository.findById(id);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return repository.findByEmail(email);
    }

    @Override
    public User updateUser(User user) {
        ValidationHelper.validateAndThrow(user);
        return repository.update(user);
    }

    @Override
    public boolean deactivateUser(String idUser) {
        return repository.deactivate(idUser);
    }

    @Override
    public boolean existsById(String idUser) {
        return repository.existsById(idUser);
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public boolean updateLastAccess(String idUser) {
        return repository.updateLastAccess(idUser);
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