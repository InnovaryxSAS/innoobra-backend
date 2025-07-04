package com.lambdas.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.lambdas.dto.request.CreateProjectRequestDTO;
import com.lambdas.dto.request.UpdateProjectRequestDTO;
import com.lambdas.exception.ValidationException;
import com.lambdas.model.Project;
import com.lambdas.model.ProjectStatus;
import com.lambdas.util.ValidationUtil.ValidationResult;

public class ValidationUtil {
    
    private static final Pattern ID_PATTERN = Pattern.compile("^[A-Za-z0-9]{1,255}$");
    private static final Pattern COUNTRY_PATTERN = Pattern.compile("^[A-Z]{2,3}$");
    private static final Pattern STATUS_PATTERN = Pattern.compile("^(active|inactive|pending|suspended|completed|cancelled)$");

    public static void validateProjectForCreation(Project project) {
        if (project == null) {
            throw new ValidationException("Project cannot be null");
        }
        
        List<String> errors = new ArrayList<>();
        
        // ID validation
        if (project.getId() == null || project.getId().trim().isEmpty()) {
            errors.add("Project ID is required");
        } else if (!ID_PATTERN.matcher(project.getId()).matches()) {
            errors.add("Invalid project ID format");
        }
        
        // Name validation
        if (project.getName() == null || project.getName().trim().isEmpty()) {
            errors.add("Project name is required");
        } else if (project.getName().trim().length() > 100) {
            errors.add("Project name cannot exceed 100 characters");
        }
        
        // Description validation
        if (project.getDescription() == null || project.getDescription().trim().isEmpty()) {
            errors.add("Project description is required");
        } else if (project.getDescription().trim().length() > 500) {
            errors.add("Project description cannot exceed 500 characters");
        }
        
        // Address validation (optional)
        if (project.getAddress() != null && project.getAddress().length() > 150) {
            errors.add("Address cannot exceed 150 characters");
        }
        
        // City validation
        if (project.getCity() == null || project.getCity().trim().isEmpty()) {
            errors.add("City is required");
        } else if (project.getCity().trim().length() > 50) {
            errors.add("City cannot exceed 50 characters");
        }
        
        // State validation
        if (project.getState() == null || project.getState().trim().isEmpty()) {
            errors.add("State is required");
        } else if (project.getState().trim().length() > 100) {
            errors.add("State cannot exceed 100 characters");
        }
        
        // Country validation
        if (project.getCountry() == null || project.getCountry().trim().isEmpty()) {
            errors.add("Country is required");
        } else if (!COUNTRY_PATTERN.matcher(project.getCountry()).matches()) {
            errors.add("Country must be 2 or 3 uppercase letters (ISO code)");
        }
        
        // Responsible user validation
        if (project.getResponsibleUser() == null || project.getResponsibleUser().trim().isEmpty()) {
            errors.add("Responsible user is required");
        } else if (project.getResponsibleUser().trim().length() > 255) {
            errors.add("Responsible user cannot exceed 255 characters");
        }
        
        // Data source validation
        if (project.getDataSource() == null || project.getDataSource().trim().isEmpty()) {
            errors.add("Data source is required");
        } else if (project.getDataSource().trim().length() > 255) {
            errors.add("Data source cannot exceed 255 characters");
        }
        
        // Company validation
        if (project.getCompany() == null || project.getCompany().trim().isEmpty()) {
            errors.add("Company is required");
        } else if (project.getCompany().trim().length() > 255) {
            errors.add("Company cannot exceed 255 characters");
        }
        
        // Created by validation
        if (project.getCreatedBy() == null || project.getCreatedBy().trim().isEmpty()) {
            errors.add("Created by is required");
        } else if (project.getCreatedBy().trim().length() > 255) {
            errors.add("Created by cannot exceed 255 characters");
        }
        
        // Budget validation
        if (project.getBudget() == null) {
            errors.add("Budget is required");
        } else if (project.getBudget().compareTo(BigDecimal.ZERO) < 0) {
            errors.add("Budget cannot be negative");
        }
        
        // Inventory validation
        if (project.getInventory() == null || project.getInventory().trim().isEmpty()) {
            errors.add("Inventory is required");
        } else if (project.getInventory().trim().length() > 500) {
            errors.add("Inventory cannot exceed 500 characters");
        }
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Validation failed: " + String.join(", ", errors));
        }
    }
    
    public static void validateProjectForUpdate(Project project) {
        if (project == null) {
            throw new ValidationException("Project cannot be null");
        }
        
        List<String> errors = new ArrayList<>();
        
        // ID validation (required for update)
        if (project.getId() == null || project.getId().trim().isEmpty()) {
            errors.add("Project ID is required for update");
        }
        
        // Name validation (if provided)
        if (project.getName() != null) {
            if (project.getName().trim().isEmpty()) {
                errors.add("Project name cannot be empty");
            } else if (project.getName().trim().length() > 100) {
                errors.add("Project name cannot exceed 100 characters");
            }
        }
        
        // Description validation (if provided)
        if (project.getDescription() != null) {
            if (project.getDescription().trim().isEmpty()) {
                errors.add("Project description cannot be empty");
            } else if (project.getDescription().trim().length() > 500) {
                errors.add("Project description cannot exceed 500 characters");
            }
        }
        
        // Address validation (optional)
        if (project.getAddress() != null && project.getAddress().length() > 150) {
            errors.add("Address cannot exceed 150 characters");
        }
        
        // City validation (if provided)
        if (project.getCity() != null) {
            if (project.getCity().trim().isEmpty()) {
                errors.add("City cannot be empty");
            } else if (project.getCity().trim().length() > 50) {
                errors.add("City cannot exceed 50 characters");
            }
        }
        
        // State validation (if provided)
        if (project.getState() != null) {
            if (project.getState().trim().isEmpty()) {
                errors.add("State cannot be empty");
            } else if (project.getState().trim().length() > 100) {
                errors.add("State cannot exceed 100 characters");
            }
        }
        
        // Country validation (if provided)
        if (project.getCountry() != null && !project.getCountry().trim().isEmpty()) {
            if (!COUNTRY_PATTERN.matcher(project.getCountry()).matches()) {
                errors.add("Country must be 2 or 3 uppercase letters (ISO code)");
            }
        }
        
        // Responsible user validation (if provided)
        if (project.getResponsibleUser() != null) {
            if (project.getResponsibleUser().trim().isEmpty()) {
                errors.add("Responsible user cannot be empty");
            } else if (project.getResponsibleUser().trim().length() > 255) {
                errors.add("Responsible user cannot exceed 255 characters");
            }
        }
        
        // Data source validation (if provided)
        if (project.getDataSource() != null) {
            if (project.getDataSource().trim().isEmpty()) {
                errors.add("Data source cannot be empty");
            } else if (project.getDataSource().trim().length() > 255) {
                errors.add("Data source cannot exceed 255 characters");
            }
        }
        
        // Company validation (if provided)
        if (project.getCompany() != null) {
            if (project.getCompany().trim().isEmpty()) {
                errors.add("Company cannot be empty");
            } else if (project.getCompany().trim().length() > 255) {
                errors.add("Company cannot exceed 255 characters");
            }
        }
        
        // Created by validation (if provided)
        if (project.getCreatedBy() != null) {
            if (project.getCreatedBy().trim().isEmpty()) {
                errors.add("Created by cannot be empty");
            } else if (project.getCreatedBy().trim().length() > 255) {
                errors.add("Created by cannot exceed 255 characters");
            }
        }
        
        // Budget validation (if provided)
        if (project.getBudget() != null && project.getBudget().compareTo(BigDecimal.ZERO) < 0) {
            errors.add("Budget cannot be negative");
        }
        
        // Inventory validation (if provided)
        if (project.getInventory() != null) {
            if (project.getInventory().trim().isEmpty()) {
                errors.add("Inventory cannot be empty");
            } else if (project.getInventory().trim().length() > 500) {
                errors.add("Inventory cannot exceed 500 characters");
            }
        }
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Validation failed: " + String.join(", ", errors));
        }
    }

    public static ValidationResult validateCreateProjectRequest(CreateProjectRequestDTO dto) {
        List<String> errors = new ArrayList<>();

        // ID validation
        if (dto.getId() == null || dto.getId().trim().isEmpty()) {
            errors.add("Project ID is required");
        } else if (!ID_PATTERN.matcher(dto.getId()).matches()) {
            errors.add("Invalid project ID format");
        }

        // Name validation
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            errors.add("Project name is required");
        } else if (dto.getName().trim().length() > 100) {
            errors.add("Project name cannot exceed 100 characters");
        }

        // Description validation
        if (dto.getDescription() == null || dto.getDescription().trim().isEmpty()) {
            errors.add("Project description is required");
        } else if (dto.getDescription().trim().length() > 500) {
            errors.add("Project description cannot exceed 500 characters");
        }

        // Address validation (optional)
        if (dto.getAddress() != null && dto.getAddress().length() > 150) {
            errors.add("Address cannot exceed 150 characters");
        }

        // City validation
        if (dto.getCity() == null || dto.getCity().trim().isEmpty()) {
            errors.add("City is required");
        } else if (dto.getCity().trim().length() > 50) {
            errors.add("City cannot exceed 50 characters");
        }

        // State validation
        if (dto.getState() == null || dto.getState().trim().isEmpty()) {
            errors.add("State is required");
        } else if (dto.getState().trim().length() > 100) {
            errors.add("State cannot exceed 100 characters");
        }

        // Country validation
        if (dto.getCountry() == null || dto.getCountry().trim().isEmpty()) {
            errors.add("Country is required");
        } else if (!COUNTRY_PATTERN.matcher(dto.getCountry()).matches()) {
            errors.add("Country must be 2 or 3 uppercase letters (ISO code)");
        }

        // Responsible user validation
        if (dto.getResponsibleUser() == null || dto.getResponsibleUser().trim().isEmpty()) {
            errors.add("Responsible user is required");
        } else if (dto.getResponsibleUser().trim().length() > 255) {
            errors.add("Responsible user cannot exceed 255 characters");
        }

        // Data source validation
        if (dto.getDataSource() == null || dto.getDataSource().trim().isEmpty()) {
            errors.add("Data source is required");
        } else if (dto.getDataSource().trim().length() > 255) {
            errors.add("Data source cannot exceed 255 characters");
        }

        // Company validation
        if (dto.getCompany() == null || dto.getCompany().trim().isEmpty()) {
            errors.add("Company is required");
        } else if (dto.getCompany().trim().length() > 255) {
            errors.add("Company cannot exceed 255 characters");
        }

        // Created by validation
        if (dto.getCreatedBy() == null || dto.getCreatedBy().trim().isEmpty()) {
            errors.add("Created by is required");
        } else if (dto.getCreatedBy().trim().length() > 255) {
            errors.add("Created by cannot exceed 255 characters");
        }

        // Budget validation
        if (dto.getBudget() == null) {
            errors.add("Budget is required");
        } else if (dto.getBudget().compareTo(BigDecimal.ZERO) < 0) {
            errors.add("Budget cannot be negative");
        }

        // Inventory validation
        if (dto.getInventory() == null || dto.getInventory().trim().isEmpty()) {
            errors.add("Inventory is required");
        } else if (dto.getInventory().trim().length() > 500) {
            errors.add("Inventory cannot exceed 500 characters");
        }

        return new ValidationResult(errors.isEmpty(), errors);
    }

    public static ValidationResult validateUpdateProjectRequest(UpdateProjectRequestDTO dto) {
        List<String> errors = new ArrayList<>();

        // Name validation (if provided)
        if (dto.getName() != null) {
            if (dto.getName().trim().isEmpty()) {
                errors.add("Project name cannot be empty");
            } else if (dto.getName().trim().length() > 100) {
                errors.add("Project name cannot exceed 100 characters");
            }
        }

        // Description validation (if provided)
        if (dto.getDescription() != null) {
            if (dto.getDescription().trim().isEmpty()) {
                errors.add("Project description cannot be empty");
            } else if (dto.getDescription().trim().length() > 500) {
                errors.add("Project description cannot exceed 500 characters");
            }
        }

        // Address validation (optional)
        if (dto.getAddress() != null && dto.getAddress().length() > 150) {
            errors.add("Address cannot exceed 150 characters");
        }

        // City validation (if provided)
        if (dto.getCity() != null) {
            if (dto.getCity().trim().isEmpty()) {
                errors.add("City cannot be empty");
            } else if (dto.getCity().trim().length() > 50) {
                errors.add("City cannot exceed 50 characters");
            }
        }

        // State validation (if provided)
        if (dto.getState() != null) {
            if (dto.getState().trim().isEmpty()) {
                errors.add("State cannot be empty");
            } else if (dto.getState().trim().length() > 100) {
                errors.add("State cannot exceed 100 characters");
            }
        }

        // Country validation (if provided)
        if (dto.getCountry() != null && !dto.getCountry().trim().isEmpty()) {
            if (!COUNTRY_PATTERN.matcher(dto.getCountry()).matches()) {
                errors.add("Country must be 2 or 3 uppercase letters (ISO code)");
            }
        }

        // Status validation (if provided)
        if (dto.getStatus() != null && !dto.getStatus().trim().isEmpty()) {
            if (!STATUS_PATTERN.matcher(dto.getStatus().toLowerCase()).matches()) {
                errors.add("Status must be one of: active, inactive, pending, suspended, completed, cancelled");
            }
        }

        // Responsible user validation (if provided)
        if (dto.getResponsibleUser() != null) {
            if (dto.getResponsibleUser().trim().isEmpty()) {
                errors.add("Responsible user cannot be empty");
            } else if (dto.getResponsibleUser().trim().length() > 255) {
                errors.add("Responsible user cannot exceed 255 characters");
            }
        }

        // Data source validation (if provided)
        if (dto.getDataSource() != null) {
            if (dto.getDataSource().trim().isEmpty()) {
                errors.add("Data source cannot be empty");
            } else if (dto.getDataSource().trim().length() > 255) {
                errors.add("Data source cannot exceed 255 characters");
            }
        }

        // Company validation (if provided)
        if (dto.getCompany() != null) {
            if (dto.getCompany().trim().isEmpty()) {
                errors.add("Company cannot be empty");
            } else if (dto.getCompany().trim().length() > 255) {
                errors.add("Company cannot exceed 255 characters");
            }
        }

        // Created by validation (if provided)
        if (dto.getCreatedBy() != null) {
            if (dto.getCreatedBy().trim().isEmpty()) {
                errors.add("Created by cannot be empty");
            } else if (dto.getCreatedBy().trim().length() > 255) {
                errors.add("Created by cannot exceed 255 characters");
            }
        }

        // Budget validation (if provided)
        if (dto.getBudget() != null && dto.getBudget().compareTo(BigDecimal.ZERO) < 0) {
            errors.add("Budget cannot be negative");
        }

        // Inventory validation (if provided)
        if (dto.getInventory() != null) {
            if (dto.getInventory().trim().isEmpty()) {
                errors.add("Inventory cannot be empty");
            } else if (dto.getInventory().trim().length() > 500) {
                errors.add("Inventory cannot exceed 500 characters");
            }
        }

        return new ValidationResult(errors.isEmpty(), errors);
    }

    public static class ValidationResult {
        private final boolean valid;
        private final List<String> errors;

        public ValidationResult(boolean valid, List<String> errors) {
            this.valid = valid;
            this.errors = errors;
        }

        public boolean isValid() {
            return valid;
        }

        public List<String> getErrors() {
            return errors;
        }

        public String getErrorsAsString() {
            return String.join(", ", errors);
        }
    }

    public static boolean isValidStatus(String status) {
        if (status == null) return false;
        return STATUS_PATTERN.matcher(status.toLowerCase()).matches();
    }

    public static ProjectStatus parseStatus(String status) {
        if (status == null) return null;
        return ProjectStatus.fromValue(status.toLowerCase());
    }
}