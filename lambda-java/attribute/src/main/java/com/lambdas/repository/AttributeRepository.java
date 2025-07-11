package com.lambdas.repository;

import java.util.List;
import java.util.Optional;

import com.lambdas.model.Attribute;
import com.lambdas.model.AttributeStatus;

public interface AttributeRepository {

    Attribute save(Attribute attribute);

    Optional<Attribute> findById(String idAttribute);

    Optional<Attribute> findByCode(String code);

    List<Attribute> findAll();

    List<Attribute> findByCompanyId(String idCompany);

    List<Attribute> findByStatus(AttributeStatus status);

    Attribute update(Attribute attribute);

    boolean deactivate(String idAttribute);

    boolean existsById(String idAttribute);

    boolean existsByCode(String code);

    String getConnectionPoolStats();

    boolean isHealthy();
}