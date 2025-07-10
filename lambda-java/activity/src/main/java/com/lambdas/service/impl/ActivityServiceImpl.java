package com.lambdas.service.impl;

import java.util.List;
import java.util.Optional;

import com.lambdas.model.Activity;
import com.lambdas.model.ActivityStatus;
import com.lambdas.repository.ActivityRepository;
import com.lambdas.repository.impl.ActivityRepositoryImpl;
import com.lambdas.service.ActivityService;
import com.lambdas.util.ValidationHelper;

public class ActivityServiceImpl implements ActivityService {

    private final ActivityRepository repository;

    public ActivityServiceImpl() {
        this.repository = new ActivityRepositoryImpl();
    }

    public ActivityServiceImpl(ActivityRepository repository) {
        this.repository = repository;
    }

    @Override
    public Activity createActivity(Activity activity) {
        ValidationHelper.validateAndThrow(activity);
        return repository.save(activity);
    }

    @Override
    public List<Activity> getAllActivities() {
        return repository.findAll();
    }

    @Override
    public Optional<Activity> getActivityById(String id) {
        return repository.findById(id);
    }

    @Override
    public Optional<Activity> getActivityByCode(String code) {
        return repository.findByCode(code);
    }

    @Override
    public List<Activity> getActivitiesByChapter(String idChapter) {
        return repository.findByChapter(idChapter);
    }

    @Override
    public List<Activity> getActivitiesByStatus(ActivityStatus status) {
        return repository.findByStatus(status);
    }

    @Override
    public Activity updateActivity(Activity activity) {
        ValidationHelper.validateAndThrow(activity);
        return repository.update(activity);
    }

    @Override
    public boolean deactivateActivity(String idActivity) {
        return repository.deactivate(idActivity);
    }

    @Override
    public boolean existsById(String idActivity) {
        return repository.existsById(idActivity);
    }

    @Override
    public boolean existsByCode(String code) {
        return repository.existsByCode(code);
    }

    @Override
    public boolean updateQuantity(String idActivity, Double quantity) {
        return repository.updateQuantity(idActivity, quantity);
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