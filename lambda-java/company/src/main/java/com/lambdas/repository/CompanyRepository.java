package com.lambdas.repository;

import java.util.List;
import java.util.Optional;

import com.lambdas.model.Company;
import com.lambdas.model.CompanyStatus;

public interface CompanyRepository {

    Company save(Company company);

    Optional<Company> findById(String id);

    List<Company> findAll();

    List<Company> findByStatus(CompanyStatus status);

    Company update(Company company);

    boolean deactivate(String id);

    boolean existsById(String id);

    String getConnectionPoolStats();

    boolean isHealthy();
}