package com.lambdas.repository;

import java.util.List;
import java.util.Optional;

import com.lambdas.model.ApuDetail;
import com.lambdas.model.ApuDetailStatus;

public interface ApuDetailRepository {

    ApuDetail save(ApuDetail apuDetail);

    Optional<ApuDetail> findById(String idApuDetail);

    List<ApuDetail> findAll();

    List<ApuDetail> findByActivityId(String idActivity);

    List<ApuDetail> findByAttributeId(String idAttribute);

    List<ApuDetail> findByStatus(ApuDetailStatus status);

    ApuDetail update(ApuDetail apuDetail);

    boolean deactivate(String idApuDetail);

    boolean delete(String idApuDetail);

    boolean existsById(String idApuDetail);

    String getConnectionPoolStats();

    boolean isHealthy();
}