package com.lambdas.service.impl;

import java.util.List;
import java.util.Optional;

import com.lambdas.model.Project;
import com.lambdas.model.ProjectStatus;
import com.lambdas.repository.ProjectRepository;
import com.lambdas.repository.impl.ProjectRepositoryImpl;
import com.lambdas.util.ValidationHelper;
import com.lambdas.service.ProjectService;

public class ProjectServiceImpl implements ProjectService{

    private final ProjectRepository repository;

    public ProjectServiceImpl() {
        this.repository = new ProjectRepositoryImpl();
    }

    public ProjectServiceImpl(ProjectRepository repository) {
        this.repository = repository;
    }

    @Override
    public Project createProject(Project project) {
        ValidationHelper.validateAndThrow(project);
        return repository.save(project);
    }

    @Override
    public List<Project> getAllProjects() {
        return repository.findAll();
    }

    @Override
    public Optional<Project> getProjectById(String id) {
        return repository.findById(id);
    }

    @Override
    public Project updateProject(Project project) {
        ValidationHelper.validateAndThrow(project);
        return repository.update(project);
    }

    @Override
    public boolean deleteProject(String id) {
        return repository.deactivate(id);
    }

    @Override
    public List<Project> getProjectsByStatus(ProjectStatus status) {
        return repository.findByStatus(status);
    }

    @Override
    public List<Project> getProjectsByCompany(String companyId) {
        return repository.findByCompany(companyId);
    }

    @Override
    public List<Project> getProjectsByResponsibleUser(String userId) {
        return repository.findByResponsibleUser(userId);
    }
}