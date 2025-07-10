package com.lambdas.repository;

import java.util.List;
import java.util.Optional;

import com.lambdas.model.Activity;
import com.lambdas.model.ActivityStatus;

public interface ActivityRepository {

    Activity save(Activity activity);

    Optional<Activity> findById(String idActivity);

    Optional<Activity> findByCode(String code);

    List<Activity> findAll();

    List<Activity> findByChapter(String idChapter);

    List<Activity> findByStatus(ActivityStatus status);

    Activity update(Activity activity);

    boolean deactivate(String idActivity);

    boolean existsById(String idActivity);

    boolean existsByCode(String code);

    boolean updateQuantity(String idActivity, Double quantity);

    String getConnectionPoolStats();

    boolean isHealthy();
}