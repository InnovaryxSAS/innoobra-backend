package com.lambdas.service;

import java.util.List;
import java.util.Optional;

import com.lambdas.model.Activity;
import com.lambdas.model.ActivityStatus;

public interface ActivityService {

    Activity createActivity(Activity activity);

    List<Activity> getAllActivities();

    Optional<Activity> getActivityById(String id);

    Optional<Activity> getActivityByCode(String code);

    List<Activity> getActivitiesByChapter(String idChapter);

    List<Activity> getActivitiesByStatus(ActivityStatus status);

    Activity updateActivity(Activity activity);

    boolean deactivateActivity(String idActivity);

    boolean existsById(String idActivity);

    boolean existsByCode(String code);

    boolean updateQuantity(String idActivity, Double quantity);

    String getConnectionPoolStats();

    boolean isHealthy();
}