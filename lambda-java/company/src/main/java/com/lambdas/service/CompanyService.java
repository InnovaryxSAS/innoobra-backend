package com.lambdas.service;

import java.util.List;
import java.util.Optional;

import com.lambdas.model.Company;
import com.lambdas.repository.CompanyRepository;
import com.lambdas.util.ValidationUtil;

public class CompanyService {
    
    private final CompanyRepository repository;
    
    public CompanyService() {
        this.repository = new CompanyRepository();
    }
    
    public CompanyService(CompanyRepository repository) {
        this.repository = repository;
    }
    
    public Company createCompany(Company company) {
        ValidationUtil.validateCompanyForCreation(company);
        return repository.save(company);
    }
    
    public List<Company> getAllCompanies() {
        return repository.findAll();
    }
    
    public Optional<Company> getCompanyById(String id) {
        return repository.findById(id);
    }
    
    public Company updateCompany(Company company) {
        ValidationUtil.validateCompanyForUpdate(company);
        return repository.update(company);
    }
    
    public boolean deleteCompany(String id) {
        return repository.deactivate(id);
    }
}