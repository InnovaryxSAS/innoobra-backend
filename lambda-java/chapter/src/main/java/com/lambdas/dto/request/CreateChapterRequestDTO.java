package com.lambdas.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lambdas.validation.annotations.ChapterStatusValid;
import com.lambdas.validation.groups.ValidationGroups;
import jakarta.validation.constraints.*;

public class CreateChapterRequestDTO {

    @JsonProperty("idChapter")
    @NotBlank(message = "Chapter ID cannot be blank", groups = ValidationGroups.Create.class)
    @Size(max = 255, message = "Chapter ID cannot exceed 255 characters")
    @Pattern(regexp = "^[A-Za-z0-9_-]{1,255}$", message = "Chapter ID must contain only alphanumeric characters, underscores, and hyphens")
    private String idChapter;

    @JsonProperty("idBudget")
    @NotBlank(message = "Budget ID cannot be blank", groups = ValidationGroups.Create.class)
    @Size(max = 255, message = "Budget ID cannot exceed 255 characters")
    private String idBudget;

    @JsonProperty("code")
    @NotBlank(message = "Code cannot be blank", groups = ValidationGroups.Create.class)
    @Size(min = 1, max = 100, message = "Code must be between 1 and 100 characters")
    private String code;

    @JsonProperty("name")
    @NotBlank(message = "Name cannot be blank", groups = ValidationGroups.Create.class)
    @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
    private String name;

    @JsonProperty("description")
    @Size(max = 200, message = "Description cannot exceed 200 characters")
    private String description;

    @JsonProperty("status")
    @ChapterStatusValid
    private String status = "active";

    // Default constructor
    public CreateChapterRequestDTO() {
    }

    // Getters and Setters
    public String getIdChapter() {
        return idChapter;
    }

    public void setIdChapter(String idChapter) {
        this.idChapter = idChapter;
    }

    public String getIdBudget() {
        return idBudget;
    }

    public void setIdBudget(String idBudget) {
        this.idBudget = idBudget;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "CreateChapterRequestDTO{" +
                "idChapter='" + idChapter + '\'' +
                ", idBudget='" + idBudget + '\'' +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}