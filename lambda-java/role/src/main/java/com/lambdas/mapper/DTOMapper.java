package com.lambdas.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.lambdas.dto.request.CreateRoleRequestDTO;
import com.lambdas.dto.request.UpdateRoleRequestDTO;
import com.lambdas.dto.response.RoleResponseDTO;
import com.lambdas.model.Role;
import com.lambdas.model.RoleStatus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DTOMapper {

    public static Role toRole(CreateRoleRequestDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("DTO cannot be null");
        }

        return Role.builder()
                .idRole(dto.getIdRole())
                .name(dto.getName())
                .description(dto.getDescription())
                .status(dto.getStatus() != null ? dto.getStatus() : RoleStatus.ACTIVE)
                .build();
    }

    public static Role updateRoleFromDTO(Role existingRole, UpdateRoleRequestDTO dto) {
        if (existingRole == null || dto == null) {
            return existingRole;
        }

        return Role.builder()
                .idRole(existingRole.getIdRole())
                .createdAt(existingRole.getCreatedAt())
                .name(dto.getName() != null ? dto.getName() : existingRole.getName())
                .description(dto.getDescription() != null ? dto.getDescription() : existingRole.getDescription())
                .status(dto.getStatus() != null ? dto.getStatus() : existingRole.getStatus())
                .fromDatabase()
                .build();
    }

    public static Role updateRoleUsingSetters(Role existingRole, UpdateRoleRequestDTO dto) {
        if (existingRole == null || dto == null) {
            return existingRole;
        }

        if (dto.getName() != null) {
            existingRole.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            existingRole.setDescription(dto.getDescription());
        }
        if (dto.getStatus() != null) {
            existingRole.setStatus(dto.getStatus());
        }

        return existingRole;
    }

    public static RoleResponseDTO toRoleResponseDTO(Role role) {
        if (role == null) {
            return null;
        }

        return RoleResponseDTO.builder()
                .idRole(role.getIdRole())
                .name(role.getName())
                .description(role.getDescription())
                .createdAt(role.getCreatedAt())
                .updatedAt(role.getUpdatedAt())
                .status(role.getStatus())
                .build();
    }

    public static List<RoleResponseDTO> toRoleResponseDTOList(List<Role> roles) {
        if (roles == null) {
            return null;
        }

        return roles.stream()
                .map(DTOMapper::toRoleResponseDTO)
                .collect(Collectors.toList());
    }
}