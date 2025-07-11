package com.lambdas.service.impl;

import java.util.List;
import java.util.Optional;

import com.lambdas.model.Attribute;
import com.lambdas.model.AttributeStatus;
import com.lambdas.repository.AttributeRepository;
import com.lambdas.repository.impl.AttributeRepositoryImpl;
import com.lambdas.service.AttributeService;
import com.lambdas.util.ValidationHelper;

public class AttributeServiceImpl implements AttributeService {

    private final AttributeRepository repository;

    public AttributeServiceImpl() {
        this.repository = new AttributeRepositoryImpl();
    }

    public AttributeServiceImpl(AttributeRepository repository) {
        this.repository = repository;
    }

    @Override
    public Attribute createAttribute(Attribute attribute) {
        ValidationHelper.validateAndThrow(attribute);
        return repository.save(attribute);
    }

    @Override
    public List<Attribute> getAllAttributes() {
        return repository.findAll();
    }

    @Override
    public Optional<Attribute> getAttributeById(String id) {
        return repository.findById(id);
    }

    @Override
    public Optional<Attribute> getAttributeByCode(String code) {
        return repository.findByCode(code);
    }

    @Override
    public List<Attribute> getAttributesByCompanyId(String idCompany) {
        return repository.findByCompanyId(idCompany);
    }

    @Override
    public List<Attribute> getAttributesByStatus(AttributeStatus status) {
        return repository.findByStatus(status);
    }

    @Override
    public Attribute updateAttribute(Attribute attribute) {
        ValidationHelper.validateAndThrow(attribute);
        return repository.update(attribute);
    }

    @Override
    public boolean deactivateAttribute(String idAttribute) {
        return repository.deactivate(idAttribute);
    }

    @Override
    public boolean existsById(String idAttribute) {
        return repository.existsById(idAttribute);
    }

    @Override
    public boolean existsByCode(String code) {
        return repository.existsByCode(code);
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