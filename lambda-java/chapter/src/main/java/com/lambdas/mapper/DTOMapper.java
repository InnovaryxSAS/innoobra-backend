package com.lambdas.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.lambdas.dto.request.CreateChapterRequestDTO;
import com.lambdas.dto.request.UpdateChapterRequestDTO;
import com.lambdas.dto.response.ChapterResponseDTO;
import com.lambdas.model.Chapter;
import com.lambdas.model.ChapterStatus;

public class DTOMapper {

    private DTOMapper() {
    }

    public static Chapter toChapter(CreateChapterRequestDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("DTO cannot be null");
        }

        return new Chapter.Builder()
                .idChapter(dto.getIdChapter())
                .idBudget(dto.getIdBudget())
                .code(dto.getCode())
                .name(dto.getName())
                .description(dto.getDescription())
                .status(ChapterStatus.fromValue(dto.getStatus() != null ? dto.getStatus() : "active"))
                .build();
    }

    public static Chapter updateChapterFromDTO(Chapter existingChapter, UpdateChapterRequestDTO dto) {
        if (existingChapter == null || dto == null) {
            return existingChapter;
        }

        Chapter.Builder builder = new Chapter.Builder()
                .idChapter(existingChapter.getIdChapter())
                .idBudget(existingChapter.getIdBudget())
                .createdAt(existingChapter.getCreatedAt())
                .fromDatabase();

        builder.code(dto.getCode() != null ? dto.getCode() : existingChapter.getCode());
        builder.name(dto.getName() != null ? dto.getName() : existingChapter.getName());
        builder.description(dto.getDescription() != null ? dto.getDescription() : existingChapter.getDescription());

        if (dto.getStatus() != null) {
            try {
                ChapterStatus status = ChapterStatus.fromValue(dto.getStatus());
                builder.status(status);
            } catch (IllegalArgumentException e) {
                builder.status(existingChapter.getStatus());
            }
        } else {
            builder.status(existingChapter.getStatus());
        }

        return builder.build();
    }

    public static ChapterResponseDTO toResponseDTO(Chapter chapter) {
        if (chapter == null) {
            return null;
        }

        return new ChapterResponseDTO.Builder()
                .idChapter(chapter.getIdChapter())
                .idBudget(chapter.getIdBudget())
                .code(chapter.getCode())
                .name(chapter.getName())
                .description(chapter.getDescription())
                .createdAt(chapter.getCreatedAt())
                .updatedAt(chapter.getUpdatedAt())
                .status(chapter.getStatus() != null ? chapter.getStatus().getValue() : null)
                .build();
    }

    public static List<ChapterResponseDTO> toResponseDTOList(List<Chapter> chapters) {
        if (chapters == null) {
            return null;
        }

        return chapters.stream()
                .map(DTOMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}