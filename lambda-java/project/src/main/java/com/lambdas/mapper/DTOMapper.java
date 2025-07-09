package com.lambdas.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.lambdas.dto.request.CreateProjectRequestDTO;
import com.lambdas.dto.request.UpdateProjectRequestDTO;
import com.lambdas.dto.response.ProjectResponseDTO;
import com.lambdas.model.Project;
import com.lambdas.model.ProjectStatus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DTOMapper {

    public static Project toProject(CreateProjectRequestDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("DTO cannot be null");
        }

        return Project.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .address(dto.getAddress())
                .city(dto.getCity())
                .state(dto.getState())
                .country(dto.getCountry())
                .responsibleUser(dto.getResponsibleUser())
                .dataSource(dto.getDataSource())
                .company(dto.getCompany())
                .createdBy(dto.getCreatedBy())
                .budget(dto.getBudget())
                .inventory(dto.getInventory())
                .status(ProjectStatus.ACTIVE)
                .build();
    }

    public static Project updateProjectFromDTO(Project existingProject, UpdateProjectRequestDTO dto) {
        if (existingProject == null || dto == null) {
            return existingProject;
        }

        return Project.builder()
                .id(existingProject.getId())
                .createdAt(existingProject.getCreatedAt())
                .name(dto.getName() != null ? dto.getName() : existingProject.getName())
                .description(dto.getDescription() != null ? dto.getDescription() : existingProject.getDescription())
                .address(dto.getAddress() != null ? dto.getAddress() : existingProject.getAddress())
                .city(dto.getCity() != null ? dto.getCity() : existingProject.getCity())
                .state(dto.getState() != null ? dto.getState() : existingProject.getState())
                .country(dto.getCountry() != null ? dto.getCountry() : existingProject.getCountry())
                .responsibleUser(dto.getResponsibleUser() != null ? dto.getResponsibleUser() : existingProject.getResponsibleUser())
                .dataSource(dto.getDataSource() != null ? dto.getDataSource() : existingProject.getDataSource())
                .company(dto.getCompany() != null ? dto.getCompany() : existingProject.getCompany())
                .createdBy(dto.getCreatedBy() != null ? dto.getCreatedBy() : existingProject.getCreatedBy())
                .budget(dto.getBudget() != null ? dto.getBudget() : existingProject.getBudget())
                .inventory(dto.getInventory() != null ? dto.getInventory() : existingProject.getInventory())
                .status(getStatusFromDTO(dto.getStatus(), existingProject.getStatus()))
                .build();
    }

    public static ProjectResponseDTO toProjectResponseDTO(Project project) {
        if (project == null) {
            return null;
        }

        return ProjectResponseDTO.builder()
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
                .dataSource(project.getDataSource())
                .company(project.getCompany())
                .createdBy(project.getCreatedBy())
                .budget(project.getBudget())
                .inventory(project.getInventory())
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

    private static ProjectStatus getStatusFromDTO(String statusValue, ProjectStatus currentStatus) {
        if (statusValue != null) {
            try {
                return ProjectStatus.fromValue(statusValue);
            } catch (IllegalArgumentException e) {
                return currentStatus;
            }
        }
        return currentStatus;
    }
}