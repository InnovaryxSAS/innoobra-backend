package com.lambdas.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.lambdas.model.Company;
import com.lambdas.model.CompanyStatus;

public interface CompanyService {

    Company createCompany(Company company);

    List<Company> getAllCompanies();

    Optional<Company> getCompanyById(UUID id);

    List<Company> getCompaniesByStatus(CompanyStatus status);

    Company updateCompany(Company company);

    boolean deleteCompany(UUID id);

    boolean existsById(UUID id);

    String getConnectionPoolStats();

    boolean isHealthy();
}