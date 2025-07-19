package com.lambdas.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.lambdas.model.User;
import com.lambdas.model.UserStatus;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(UUID id);

    Optional<User> findByEmail(String email);

    Optional<User> findByDocumentNumber(String documentNumber);

    List<User> findAll();

    List<User> findByCompanyId(UUID companyId);

    List<User> findByStatus(UserStatus status);

    User update(User user);

    boolean deactivate(UUID id);

    boolean existsById(UUID id);

    boolean existsByEmail(String email);

    boolean existsByDocumentNumber(String documentNumber);

    boolean updateLastAccess(UUID id);

    String getConnectionPoolStats();

    boolean isHealthy();
}