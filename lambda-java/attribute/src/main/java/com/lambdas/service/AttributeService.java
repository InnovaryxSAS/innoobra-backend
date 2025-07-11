package com.lambdas.service;

import java.util.List;
import java.util.Optional;

import com.lambdas.model.Attribute;
import com.lambdas.model.AttributeStatus;

public interface AttributeService {

    Attribute createAttribute(Attribute attribute);

    List<Attribute> getAllAttributes();

    Optional<Attribute> getAttributeById(String id);

    Optional<Attribute> getAttributeByCode(String code);

    List<Attribute> getAttributesByCompanyId(String idCompany);

    List<Attribute> getAttributesByStatus(AttributeStatus status);

    Attribute updateAttribute(Attribute attribute);

    boolean deactivateAttribute(String idAttribute);

    boolean existsById(String idAttribute);

    boolean existsByCode(String code);

    String getConnectionPoolStats();

    boolean isHealthy();
}