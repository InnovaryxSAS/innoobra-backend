package com.lambdas.service.impl;

import java.util.List;
import java.util.Optional;

import com.lambdas.model.Chapter;
import com.lambdas.model.ChapterStatus;
import com.lambdas.repository.ChapterRepository;
import com.lambdas.repository.impl.ChapterRepositoryImpl;
import com.lambdas.service.ChapterService;
import com.lambdas.util.ValidationHelper;

public class ChapterServiceImpl implements ChapterService {

    private final ChapterRepository repository;

    public ChapterServiceImpl() {
        this.repository = new ChapterRepositoryImpl();
    }

    public ChapterServiceImpl(ChapterRepository repository) {
        this.repository = repository;
    }

    @Override
    public Chapter createChapter(Chapter chapter) {
        ValidationHelper.validateAndThrow(chapter);
        return repository.save(chapter);
    }

    @Override
    public List<Chapter> getAllChapters() {
        return repository.findAll();
    }

    @Override
    public Optional<Chapter> getChapterById(String idChapter) {
        return repository.findById(idChapter);
    }

    @Override
    public Optional<Chapter> getChapterByCode(String code) {
        return repository.findByCode(code);
    }

    @Override
    public List<Chapter> getChaptersByBudgetId(String idBudget) {
        return repository.findByBudgetId(idBudget);
    }

    @Override
    public List<Chapter> getChaptersByStatus(ChapterStatus status) {
        return repository.findByStatus(status);
    }

    @Override
    public Chapter updateChapter(Chapter chapter) {
        ValidationHelper.validateAndThrow(chapter);
        return repository.update(chapter);
    }

    @Override
    public boolean deactivateChapter(String idChapter) {
        return repository.deactivate(idChapter);
    }

    @Override
    public boolean existsById(String idChapter) {
        return repository.existsById(idChapter);
    }

    @Override
    public boolean existsByCode(String code) {
        return repository.existsByCode(code);
    }

    @Override
    public boolean existsByCodeAndBudgetId(String code, String idBudget) {
        return repository.existsByCodeAndBudgetId(code, idBudget);
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