package com.lambdas.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.lambdas.model.User;
import com.lambdas.model.UserStatus;

public interface UserService {

    User createUser(User user);

    List<User> getAllUsers();

    Optional<User> getUserById(UUID id);

    Optional<User> getUserByEmail(String email);

    Optional<User> getUserByDocumentNumber(String documentNumber);

    List<User> getUsersByCompanyId(UUID companyId);

    List<User> getUsersByStatus(UserStatus status);

    User updateUser(User user);

    boolean deactivateUser(UUID id);

    boolean existsById(UUID id);

    boolean existsByEmail(String email);

    boolean existsByDocumentNumber(String documentNumber);

    boolean updateLastAccess(UUID id);

    String getConnectionPoolStats();

    boolean isHealthy();
}