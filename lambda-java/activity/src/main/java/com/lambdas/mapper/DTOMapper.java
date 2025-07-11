package com.lambdas.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.lambdas.dto.request.CreateActivityRequestDTO;
import com.lambdas.dto.request.UpdateActivityRequestDTO;
import com.lambdas.dto.response.ActivityResponseDTO;
import com.lambdas.model.Activity;
import com.lambdas.model.ActivityStatus;

public class DTOMapper {

    private DTOMapper() {
    }

    public static Activity toActivity(CreateActivityRequestDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("DTO cannot be null");
        }

        return new Activity.Builder()
                .idActivity(dto.getIdActivity())
                .idChapter(dto.getIdChapter())
                .code(dto.getCode())
                .name(dto.getName())
                .description(dto.getDescription())
                .unit(dto.getUnit())
                .quantity(dto.getQuantity())
                .status(ActivityStatus.fromValue(dto.getStatus() != null ? dto.getStatus() : "active"))
                .build();
    }

    public static Activity updateActivityFromDTO(Activity existingActivity, UpdateActivityRequestDTO dto) {
        if (existingActivity == null || dto == null) {
            return existingActivity;
        }

        Activity.Builder builder = new Activity.Builder()
                .idActivity(existingActivity.getIdActivity())
                .idChapter(existingActivity.getIdChapter())
                .createdAt(existingActivity.getCreatedAt())
                .fromDatabase();

        builder.code(dto.getCode() != null ? dto.getCode() : existingActivity.getCode());
        builder.name(dto.getName() != null ? dto.getName() : existingActivity.getName());
        builder.description(dto.getDescription() != null ? dto.getDescription() : existingActivity.getDescription());
        builder.unit(dto.getUnit() != null ? dto.getUnit() : existingActivity.getUnit());
        builder.quantity(dto.getQuantity() != null ? dto.getQuantity() : existingActivity.getQuantity());

        if (dto.getStatus() != null) {
            try {
                ActivityStatus status = ActivityStatus.fromValue(dto.getStatus());
                builder.status(status);
            } catch (IllegalArgumentException e) {
                builder.status(existingActivity.getStatus());
            }
        } else {
            builder.status(existingActivity.getStatus());
        }

        return builder.build();
    }

    public static ActivityResponseDTO toResponseDTO(Activity activity) {
        if (activity == null) {
            return null;
        }

        return new ActivityResponseDTO.Builder()
                .idActivity(activity.getIdActivity())
                .idChapter(activity.getIdChapter())
                .code(activity.getCode())
                .name(activity.getName())
                .description(activity.getDescription())
                .unit(activity.getUnit())
                .quantity(activity.getQuantity())
                .createdAt(activity.getCreatedAt())
                .updatedAt(activity.getUpdatedAt())
                .status(activity.getStatus() != null ? activity.getStatus().getValue() : null)
                .build();
    }

    public static List<ActivityResponseDTO> toResponseDTOList(List<Activity> activities) {
        if (activities == null) {
            return null;
        }

        return activities.stream()
                .map(DTOMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}