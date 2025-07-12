package com.lambdas.service;

import java.util.List;
import java.util.Optional;

import com.lambdas.model.ApuDetail;
import com.lambdas.model.ApuDetailStatus;

public interface ApuDetailService {

    ApuDetail createApuDetail(ApuDetail apuDetail);

    List<ApuDetail> getAllApuDetails();

    Optional<ApuDetail> getApuDetailById(String id);

    List<ApuDetail> getApuDetailsByActivityId(String idActivity);

    List<ApuDetail> getApuDetailsByAttributeId(String idAttribute);

    List<ApuDetail> getApuDetailsByStatus(ApuDetailStatus status);

    ApuDetail updateApuDetail(ApuDetail apuDetail);

    boolean deactivateApuDetail(String idApuDetail);

    boolean deleteApuDetail(String idApuDetail);

    boolean existsById(String idApuDetail);

    String getConnectionPoolStats();

    boolean isHealthy();
}