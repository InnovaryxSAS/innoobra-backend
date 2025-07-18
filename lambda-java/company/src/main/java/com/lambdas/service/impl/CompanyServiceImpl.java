package com.lambdas.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.lambdas.model.Company;
import com.lambdas.model.CompanyStatus;
import com.lambdas.repository.CompanyRepository;
import com.lambdas.repository.impl.CompanyRepositoryImpl;
import com.lambdas.service.CompanyService;
import com.lambdas.util.ValidationHelper;

public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository repository;

    public CompanyServiceImpl() {
        this.repository = new CompanyRepositoryImpl();
    }

    public CompanyServiceImpl(CompanyRepository repository) {
        this.repository = repository;
    }

    @Override
    public Company createCompany(Company company) {
        ValidationHelper.validateAndThrow(company);
        return repository.save(company);
    }

    @Override
    public List<Company> getAllCompanies() {
        return repository.findAll();
    }

    @Override
    public Optional<Company> getCompanyById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<Company> getCompaniesByStatus(CompanyStatus status) {
        return repository.findByStatus(status);
    }

    @Override
    public Company updateCompany(Company company) {
        ValidationHelper.validateAndThrow(company);
        return repository.update(company);
    }

    @Override
    public boolean deleteCompany(UUID id) {
        return repository.deactivate(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return repository.existsById(id);
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