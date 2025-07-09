package com.lambdas.service.impl;

import java.util.List;
import java.util.Optional;

import com.lambdas.model.Company;
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
    public Optional<Company> getCompanyById(String id) {
        return repository.findById(id);
    }

    @Override
    public Company updateCompany(Company company) {
        ValidationHelper.validateAndThrow(company);
        return repository.update(company);
    }

    @Override
    public boolean deleteCompany(String id) {
        return repository.deactivate(id);
    }
}