package com.lambdas.service;

import java.util.List;
import java.util.Optional;

import com.lambdas.model.Company;

public interface CompanyService {

    Company createCompany(Company company);

    List<Company> getAllCompanies();

    Optional<Company> getCompanyById(String id);

    Company updateCompany(Company company);

    boolean deleteCompany(String id);
}