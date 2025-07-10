package com.lambdas.service;

import java.util.List;
import java.util.Optional;

import com.lambdas.model.User;
import com.lambdas.model.UserStatus;

public interface UserService {

    User createUser(User user);

    List<User> getAllUsers();

    Optional<User> getUserById(String id);

    Optional<User> getUserByEmail(String email);

    User updateUser(User user);

    boolean deactivateUser(String idUser);

    boolean existsById(String idUser);

    boolean existsByEmail(String email);

    boolean updateLastAccess(String idUser);

    String getConnectionPoolStats();

    boolean isHealthy();
}