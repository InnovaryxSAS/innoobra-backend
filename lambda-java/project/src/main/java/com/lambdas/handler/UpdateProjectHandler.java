package com.lambdas.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lambdas.dto.request.UpdateProjectRequestDTO;
import com.lambdas.dto.response.ProjectResponseDTO;
import com.lambdas.exception.ProjectNotFoundException;
import com.lambdas.exception.DatabaseException;
import com.lambdas.exception.ValidationException;
import com.lambdas.mapper.DTOMapper;
import com.lambdas.model.Project;
import com.lambdas.repository.ConnectionPoolManager;
import com.lambdas.service.ProjectService;
import com.lambdas.util.ResponseUtil;
import com.lambdas.util.ValidationUtil;

import java.util.Optional;

public class UpdateProjectHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    
    private static final ProjectService PROJECT_SERVICE = new ProjectService();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule());
    
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        String requestId = context.getAwsRequestId();
        context.getLogger().log("Processing request: " + requestId);
        
        try {
            ConnectionPoolManager poolManager = ConnectionPoolManager.getInstance();
            context.getLogger().log("Connection pool status: " + poolManager.getPoolStats());
            context.getLogger().log("Connection pool healthy: " + poolManager.isHealthy());
        } catch (Exception e) {
            context.getLogger().log("Warning: Could not get pool stats: " + e.getMessage());
        }
        
        try {
            String projectId = input.getPathParameters().get("id");
            if (projectId == null || projectId.trim().isEmpty()) {
                return ResponseUtil.createErrorResponse(400, "Project ID is required");
            }
            
            if (input.getBody() == null || input.getBody().trim().isEmpty()) {
                return ResponseUtil.createErrorResponse(400, "Request body is required");
            }
            
            UpdateProjectRequestDTO requestDTO = OBJECT_MAPPER.readValue(input.getBody(), UpdateProjectRequestDTO.class);
            context.getLogger().log("Parsed update request DTO for project: " + projectId);
            
            ValidationUtil.ValidationResult validationResult = ValidationUtil.validateUpdateProjectRequest(requestDTO);
            if (!validationResult.isValid()) {
                return ResponseUtil.createErrorResponse(400, validationResult.getErrorsAsString());
            }
            
            Optional<Project> existingProjectOpt = PROJECT_SERVICE.getProjectById(projectId);
            if (!existingProjectOpt.isPresent()) {
                return ResponseUtil.createErrorResponse(404, "Project not found");
            }
            
            Project existingProject = existingProjectOpt.get();
            
            Project updatedProject = DTOMapper.updateProjectFromDTO(existingProject, requestDTO);
            
            Project savedProject = PROJECT_SERVICE.updateProject(updatedProject);
            
            ProjectResponseDTO responseDTO = DTOMapper.toProjectResponseDTO(savedProject);
            
            context.getLogger().log("Project updated successfully: " + projectId);
            
            try {
                ConnectionPoolManager poolManager = ConnectionPoolManager.getInstance();
                context.getLogger().log("Final connection pool status: " + poolManager.getPoolStats());
            } catch (Exception e) {
                context.getLogger().log("Warning: Could not get final pool stats: " + e.getMessage());
            }
            
            return ResponseUtil.createResponse(200, responseDTO);
            
        } catch (JsonProcessingException e) {
            context.getLogger().log("JSON parsing error: " + e.getMessage());
            return ResponseUtil.createErrorResponse(400, "Invalid JSON format");
        } catch (ValidationException e) {
            context.getLogger().log("Validation error: " + e.getMessage());
            return ResponseUtil.createErrorResponse(400, e.getMessage());
        } catch (ProjectNotFoundException e) {
            context.getLogger().log("Project not found: " + e.getMessage());
            return ResponseUtil.createErrorResponse(404, e.getMessage());
        } catch (DatabaseException e) {
            context.getLogger().log("Database error for request " + requestId + ": " + e.getMessage());
            try {
                ConnectionPoolManager poolManager = ConnectionPoolManager.getInstance();
                context.getLogger().log("Connection pool status on error: " + poolManager.getPoolStats());
                context.getLogger().log("Connection pool healthy on error: " + poolManager.isHealthy());
            } catch (Exception poolException) {
                context.getLogger().log("Could not get pool stats on error: " + poolException.getMessage());
            }
            return ResponseUtil.createErrorResponse(500, "Internal server error");
        } catch (Exception e) {
            context.getLogger().log("Unexpected error for request " + requestId + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseUtil.createErrorResponse(500, "Internal server error");
        }
    }
}