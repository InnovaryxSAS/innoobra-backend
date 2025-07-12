package com.lambdas.service.impl;

import java.util.List;
import java.util.Optional;

import com.lambdas.model.ApuDetail;
import com.lambdas.model.ApuDetailStatus;
import com.lambdas.repository.ApuDetailRepository;
import com.lambdas.repository.impl.ApuDetailRepositoryImpl;
import com.lambdas.service.ApuDetailService;
import com.lambdas.util.ValidationHelper;

public class ApuDetailServiceImpl implements ApuDetailService {

    private final ApuDetailRepository repository;

    public ApuDetailServiceImpl() {
        this.repository = new ApuDetailRepositoryImpl();
    }

    public ApuDetailServiceImpl(ApuDetailRepository repository) {
        this.repository = repository;
    }

    @Override
    public ApuDetail createApuDetail(ApuDetail apuDetail) {
        ValidationHelper.validateAndThrow(apuDetail);
        return repository.save(apuDetail);
    }

    @Override
    public List<ApuDetail> getAllApuDetails() {
        return repository.findAll();
    }

    @Override
    public Optional<ApuDetail> getApuDetailById(String id) {
        return repository.findById(id);
    }

    @Override
    public List<ApuDetail> getApuDetailsByActivityId(String idActivity) {
        return repository.findByActivityId(idActivity);
    }

    @Override
    public List<ApuDetail> getApuDetailsByAttributeId(String idAttribute) {
        return repository.findByAttributeId(idAttribute);
    }

    @Override
    public List<ApuDetail> getApuDetailsByStatus(ApuDetailStatus status) {
        return repository.findByStatus(status);
    }

    @Override
    public ApuDetail updateApuDetail(ApuDetail apuDetail) {
        ValidationHelper.validateAndThrow(apuDetail);
        return repository.update(apuDetail);
    }

    @Override
    public boolean deactivateApuDetail(String idApuDetail) {
        return repository.deactivate(idApuDetail);
    }

    @Override
    public boolean deleteApuDetail(String idApuDetail) {
        return repository.delete(idApuDetail);
    }

    @Override
    public boolean existsById(String idApuDetail) {
        return repository.existsById(idApuDetail);
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