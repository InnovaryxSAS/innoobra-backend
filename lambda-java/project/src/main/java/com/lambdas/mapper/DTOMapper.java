package com.lambdas.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.lambdas.dto.request.CreateProjectRequestDTO;
import com.lambdas.dto.request.UpdateProjectRequestDTO;
import com.lambdas.dto.response.ProjectResponseDTO;
import com.lambdas.model.Project;
import com.lambdas.model.ProjectStatus;

public class DTOMapper {

    private DTOMapper() {
    }

    public static Project toProject(CreateProjectRequestDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("DTO cannot be null");
        }

        return new Project.Builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .address(dto.getAddress())
                .city(dto.getCity())
                .state(dto.getState())
                .country(dto.getCountry())
                .responsibleUser(dto.getResponsibleUser())
                .dataSourceId(dto.getDataSourceId())
                .companyId(dto.getCompanyId())
                .createdBy(dto.getCreatedBy())
                .budgetAmount(dto.getBudgetAmount())
                .status(dto.getStatus() != null ? ProjectStatus.fromValue(dto.getStatus()) : ProjectStatus.ACTIVE)
                .build();
    }

    public static Project updateProjectFromDTO(Project existingProject, UpdateProjectRequestDTO dto) {
        if (existingProject == null || dto == null) {
            return existingProject;
        }

        Project.Builder builder = new Project.Builder()
                .id(existingProject.getId())
                .createdAt(existingProject.getCreatedAt())
                .fromDatabase();

        builder.name(dto.getName() != null ? dto.getName() : existingProject.getName());
        builder.description(dto.getDescription() != null ? dto.getDescription() : existingProject.getDescription());
        builder.address(dto.getAddress() != null ? dto.getAddress() : existingProject.getAddress());
        builder.city(dto.getCity() != null ? dto.getCity() : existingProject.getCity());
        builder.state(dto.getState() != null ? dto.getState() : existingProject.getState());
        builder.country(dto.getCountry() != null ? dto.getCountry() : existingProject.getCountry());
        builder.responsibleUser(dto.getResponsibleUser() != null ? dto.getResponsibleUser() : existingProject.getResponsibleUser());
        builder.dataSourceId(dto.getDataSourceId() != null ? dto.getDataSourceId() : existingProject.getDataSourceId());
        builder.companyId(dto.getCompanyId() != null ? dto.getCompanyId() : existingProject.getCompanyId());
        builder.createdBy(dto.getCreatedBy() != null ? dto.getCreatedBy() : existingProject.getCreatedBy());
        builder.budgetAmount(dto.getBudgetAmount() != null ? dto.getBudgetAmount() : existingProject.getBudgetAmount());

        if (dto.getStatus() != null) {
            try {
                ProjectStatus status = ProjectStatus.fromValue(dto.getStatus());
                builder.status(status);
            } catch (IllegalArgumentException e) {
                builder.status(existingProject.getStatus());
            }
        } else {
            builder.status(existingProject.getStatus());
        }

        return builder.build();
    }

    public static ProjectResponseDTO toProjectResponseDTO(Project project) {
        if (project == null) {
            return null;
        }

        return new ProjectResponseDTO.Builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .address(project.getAddress())
                .city(project.getCity())
                .state(project.getState())
                .country(project.getCountry())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .status(project.getStatus() != null ? project.getStatus().getValue() : null)
                .responsibleUser(project.getResponsibleUser())
                .dataSourceId(project.getDataSourceId())
                .companyId(project.getCompanyId())
                .createdBy(project.getCreatedBy())
                .budgetAmount(project.getBudgetAmount())
                .build();
    }

    public static List<ProjectResponseDTO> toProjectResponseDTOList(List<Project> projects) {
        if (projects == null) {
            return null;
        }

        return projects.stream()
                .map(DTOMapper::toProjectResponseDTO)
                .collect(Collectors.toList());
    }
}