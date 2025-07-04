package com.lambdas.service;

import java.util.List;
import java.util.Optional;

import com.lambdas.model.Project;
import com.lambdas.model.ProjectStatus;
import com.lambdas.repository.ProjectRepository;
import com.lambdas.util.ValidationUtil;

public class ProjectService {
    
    private final ProjectRepository repository;
    
    public ProjectService() {
        this.repository = new ProjectRepository();
    }
    
    public ProjectService(ProjectRepository repository) {
        this.repository = repository;
    }
    
    public Project createProject(Project project) {
        ValidationUtil.validateProjectForCreation(project);
        return repository.save(project);
    }
    
    public List<Project> getAllProjects() {
        return repository.findAll();
    }
    
    public Optional<Project> getProjectById(String id) {
        return repository.findById(id);
    }
    
    public Project updateProject(Project project) {
        ValidationUtil.validateProjectForUpdate(project);
        return repository.update(project);
    }
    
    public boolean deleteProject(String id) {
        return repository.deactivate(id);
    }
    
    public List<Project> getProjectsByStatus(ProjectStatus status) {
        return repository.findByStatus(status);
    }
    
    public List<Project> getProjectsByCompany(String companyId) {
        return repository.findByCompany(companyId);
    }
    
    public List<Project> getProjectsByResponsibleUser(String userId) {
        return repository.findByResponsibleUser(userId);
    }
    
    public boolean completeProject(String id) {
        return repository.complete(id);
    }
    
    public boolean cancelProject(String id) {
        return repository.cancel(id);
    }
}