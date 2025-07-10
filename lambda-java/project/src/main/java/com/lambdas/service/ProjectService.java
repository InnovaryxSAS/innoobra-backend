package com.lambdas.service;

import java.util.List;
import java.util.Optional;

import com.lambdas.model.Project;
import com.lambdas.model.ProjectStatus;

public interface ProjectService {

    Project createProject(Project project);

    List<Project> getAllProjects();

    Optional<Project> getProjectById(String id);

    Project updateProject(Project project);

    boolean deleteProject(String id);

    List<Project> getProjectsByStatus(ProjectStatus status);
    
    List<Project> getProjectsByCompany(String companyId);

    List<Project> getProjectsByResponsibleUser(String userId);
}