package com.lambdas.repository;

import java.util.List;
import java.util.Optional;

import com.lambdas.model.User;
import com.lambdas.model.UserStatus;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(String idUser);

    Optional<User> findByEmail(String email);

    List<User> findAll();

    User update(User user);

    boolean deactivate(String idUser);

    boolean existsById(String idUser);

    boolean existsByEmail(String email);

    boolean updateLastAccess(String idUser);

    String getConnectionPoolStats();

    boolean isHealthy();
}