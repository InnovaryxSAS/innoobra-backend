package com.lambdas.repository;

import java.util.List;
import java.util.Optional;

import com.lambdas.model.Chapter;
import com.lambdas.model.ChapterStatus;

public interface ChapterRepository {

    Chapter save(Chapter chapter);

    Optional<Chapter> findById(String idChapter);

    Optional<Chapter> findByCode(String code);

    List<Chapter> findAll();

    List<Chapter> findByBudgetId(String idBudget);

    List<Chapter> findByStatus(ChapterStatus status);

    Chapter update(Chapter chapter);

    boolean deactivate(String idChapter);

    boolean existsById(String idChapter);

    boolean existsByCode(String code);

    boolean existsByCodeAndBudgetId(String code, String idBudget);

    String getConnectionPoolStats();

    boolean isHealthy();
}