package com.lambdas.service;

import java.util.List;
import java.util.Optional;

import com.lambdas.model.Chapter;
import com.lambdas.model.ChapterStatus;

public interface ChapterService {

    Chapter createChapter(Chapter chapter);

    List<Chapter> getAllChapters();

    Optional<Chapter> getChapterById(String idChapter);

    Optional<Chapter> getChapterByCode(String code);

    List<Chapter> getChaptersByBudgetId(String idBudget);

    List<Chapter> getChaptersByStatus(ChapterStatus status);

    Chapter updateChapter(Chapter chapter);

    boolean deactivateChapter(String idChapter);

    boolean existsById(String idChapter);

    boolean existsByCode(String code);

    boolean existsByCodeAndBudgetId(String code, String idBudget);

    String getConnectionPoolStats();

    boolean isHealthy();
}