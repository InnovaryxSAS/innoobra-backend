package com.lambdas.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.lambdas.model.Project;
import com.lambdas.model.ProjectStatus;

public interface ProjectRepository {

    Project save(Project project);

    Optional<Project> findById(UUID id);

    List<Project> findAll();

    List<Project> findByStatus(ProjectStatus status);

    List<Project> findByCompany(UUID companyId);

    List<Project> findByResponsibleUser(UUID userId);

    List<Project> findByCompanyAndStatus(UUID companyId, ProjectStatus status);

    Project update(Project project);

    boolean deactivate(UUID id);

    boolean existsById(UUID id);

    String getConnectionPoolStats();

    boolean isHealthy();
}