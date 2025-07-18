package com.lambdas.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.lambdas.model.Company;
import com.lambdas.model.CompanyStatus;

public interface CompanyRepository {

    Company save(Company company);

    Optional<Company> findById(UUID id);

    List<Company> findAll();

    List<Company> findByStatus(CompanyStatus status);

    Company update(Company company);

    boolean deactivate(UUID id);

    boolean existsById(UUID id);

    String getConnectionPoolStats();

    boolean isHealthy();
}