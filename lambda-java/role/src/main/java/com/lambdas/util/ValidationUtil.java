package com.lambdas.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.lambdas.dto.request.CreateRoleRequestDTO;
import com.lambdas.dto.request.UpdateRoleRequestDTO;
import com.lambdas.exception.ValidationException;
import com.lambdas.model.Role;
import com.lambdas.model.RoleStatus;

public class ValidationUtil {
    
    private static final Pattern ID_PATTERN = Pattern.compile("^[A-Za-z0-9]{1,255}$");
    private static final Pattern STATUS_PATTERN = Pattern.compile("^(active|inactive|pending|suspended|completed|cancelled)$");

    public static void validateRoleForCreation(Role role) {
        if (role == null) {
            throw new ValidationException("Role cannot be null");
        }
        
        List<String> errors = new ArrayList<>();
        
        // ID validation
        if (role.getIdRole() == null || role.getIdRole().trim().isEmpty()) {
            errors.add("Role ID is required");
        } else if (!ID_PATTERN.matcher(role.getIdRole()).matches()) {
            errors.add("Invalid role ID format");
        }
        
        // Name validation
        if (role.getName() == null || role.getName().trim().isEmpty()) {
            errors.add("Role name is required");
        } else if (role.getName().trim().length() > 50) {
            errors.add("Role name cannot exceed 50 characters");
        }
        
        // Description validation
        if (role.getDescription() == null || role.getDescription().trim().isEmpty()) {
            errors.add("Role description is required");
        } else if (role.getDescription().trim().length() > 100) {
            errors.add("Role description cannot exceed 100 characters");
        }
        
        // Status validation (implicit - should be set by default constructor)
        if (role.getStatus() == null) {
            errors.add("Role status is required");
        }
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Validation failed: " + String.join(", ", errors));
        }
    }
    
    public static void validateRoleForUpdate(Role role) {
        if (role == null) {
            throw new ValidationException("Role cannot be null");
        }
        
        List<String> errors = new ArrayList<>();
        
        // ID validation (required for update)
        if (role.getIdRole() == null || role.getIdRole().trim().isEmpty()) {
            errors.add("Role ID is required for update");
        }
        
        // Name validation (if provided)
        if (role.getName() != null) {
            if (role.getName().trim().isEmpty()) {
                errors.add("Role name cannot be empty");
            } else if (role.getName().trim().length() > 50) {
                errors.add("Role name cannot exceed 50 characters");
            }
        }
        
        // Description validation (if provided)
        if (role.getDescription() != null) {
            if (role.getDescription().trim().isEmpty()) {
                errors.add("Role description cannot be empty");
            } else if (role.getDescription().trim().length() > 100) {
                errors.add("Role description cannot exceed 100 characters");
            }
        }
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Validation failed: " + String.join(", ", errors));
        }
    }

    public static ValidationResult validateCreateRoleRequest(CreateRoleRequestDTO dto) {
        List<String> errors = new ArrayList<>();

        // ID validation
        if (dto.getIdRole() == null || dto.getIdRole().trim().isEmpty()) {
            errors.add("Role ID is required");
        } else if (!ID_PATTERN.matcher(dto.getIdRole()).matches()) {
            errors.add("Invalid role ID format");
        }

        // Name validation
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            errors.add("Role name is required");
        } else if (dto.getName().trim().length() > 50) {
            errors.add("Role name cannot exceed 50 characters");
        }

        // Description validation
        if (dto.getDescription() == null || dto.getDescription().trim().isEmpty()) {
            errors.add("Role description is required");
        } else if (dto.getDescription().trim().length() > 100) {
            errors.add("Role description cannot exceed 100 characters");
        }

        return new ValidationResult(errors.isEmpty(), errors);
    }

    public static ValidationResult validateUpdateRoleRequest(UpdateRoleRequestDTO dto) {
        List<String> errors = new ArrayList<>();

        // Name validation (if provided)
        if (dto.getName() != null) {
            if (dto.getName().trim().isEmpty()) {
                errors.add("Role name cannot be empty");
            } else if (dto.getName().trim().length() > 50) {
                errors.add("Role name cannot exceed 50 characters");
            }
        }

        // Description validation (if provided)
        if (dto.getDescription() != null) {
            if (dto.getDescription().trim().isEmpty()) {
                errors.add("Role description cannot be empty");
            } else if (dto.getDescription().trim().length() > 100) {
                errors.add("Role description cannot exceed 100 characters");
            }
        }

        // Status validation (if provided)
        if (dto.getStatus() != null) {
            String statusString = dto.getStatus().toString().toLowerCase();
            if (!STATUS_PATTERN.matcher(statusString).matches()) {
                errors.add("Status must be one of: active, inactive, pending, suspended, completed, cancelled");
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

    public static RoleStatus parseStatus(String status) {
        if (status == null) return null;
        return RoleStatus.fromValue(status.toLowerCase());
    }
}