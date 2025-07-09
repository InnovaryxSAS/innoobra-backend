package com.lambdas.repository;

import java.util.List;
import java.util.Optional;

import com.lambdas.model.Project;
import com.lambdas.model.ProjectStatus;

public interface ProjectRepository {

    Project save(Project project);

    Optional<Project> findById(String id);

    List<Project> findAll();

    List<Project> findByStatus(ProjectStatus status);

    List<Project> findByCompany(String companyId);

    List<Project> findByResponsibleUser(String userId);

    List<Project> findByCompanyAndStatus(String companyId, ProjectStatus status);

    Project update(Project project);

    boolean deactivate(String id);

    boolean existsById(String id);

    String getConnectionPoolStats();

    boolean isHealthy();
}