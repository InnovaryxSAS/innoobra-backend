package com.lambdas.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.lambdas.model.Project;
import com.lambdas.model.ProjectStatus;

public interface ProjectService {

    Project createProject(Project project);

    List<Project> getAllProjects();

    Optional<Project> getProjectById(UUID id);

    Project updateProject(Project project);

    boolean deleteProject(UUID id);

    List<Project> getProjectsByStatus(ProjectStatus status);
    
    List<Project> getProjectsByCompany(UUID companyId);

    List<Project> getProjectsByResponsibleUser(UUID userId);

    List<Project> getProjectsByCompanyAndStatus(UUID companyId, ProjectStatus status);

    boolean existsById(UUID id);

    String getConnectionPoolStats();

    boolean isHealthy();
}