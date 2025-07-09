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
import com.lambdas.util.ValidationHelper;
import com.lambdas.validation.groups.ValidationGroups;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.Optional;

public class UpdateProjectHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    
    private static final Logger logger = LoggerFactory.getLogger(UpdateProjectHandler.class);
    private static final ProjectService PROJECT_SERVICE = new ProjectService();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule());
    
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        String requestId = context.getAwsRequestId();
        MDC.put("requestId", requestId);
        
        try {
            String projectId = input.getPathParameters().get("id");
            MDC.put("projectId", projectId);
            
            logger.info("Starting project update process");
            logConnectionPoolStatus();
            
            if (projectId == null || projectId.trim().isEmpty()) {
                logger.warn("Project ID is missing or empty");
                return ResponseUtil.createErrorResponse(400, "Project ID is required");
            }
            
            if (input.getBody() == null || input.getBody().trim().isEmpty()) {
                logger.warn("Request body is empty or null");
                return ResponseUtil.createErrorResponse(400, "Request body is required");
            }
            
            UpdateProjectRequestDTO requestDTO = OBJECT_MAPPER.readValue(input.getBody(), UpdateProjectRequestDTO.class);
            
            ValidationHelper.validateAndThrow(requestDTO, ValidationGroups.Update.class);
            
            Optional<Project> existingProjectOpt = PROJECT_SERVICE.getProjectById(projectId);
            if (!existingProjectOpt.isPresent()) {
                logger.warn("Project not found for update");
                return ResponseUtil.createErrorResponse(404, "Project not found");
            }
            
            Project existingProject = existingProjectOpt.get();
            
            Project updatedProject = DTOMapper.updateProjectFromDTO(existingProject, requestDTO);
            
            Project savedProject = PROJECT_SERVICE.updateProject(updatedProject);
            logger.info("Project updated successfully");
            
            ProjectResponseDTO responseDTO = DTOMapper.toProjectResponseDTO(savedProject);
            
            logFinalConnectionPoolStatus();
            
            return ResponseUtil.createResponse(200, responseDTO);
            
        } catch (JsonProcessingException e) {
            logger.error("JSON parsing error: {}", e.getMessage());
            return ResponseUtil.createErrorResponse(400, "Invalid JSON format");
        } catch (ValidationException e) {
            logger.warn("Validation error: {}", e.getMessage());
            return ResponseUtil.createErrorResponse(400, e.toMap());
        } catch (ProjectNotFoundException e) {
            logger.warn("Project not found: {}", e.getMessage());
            return ResponseUtil.createErrorResponse(404, e.getMessage());
        } catch (DatabaseException e) {
            logger.error("Database error occurred", e);
            logConnectionPoolStatusOnError();
            return ResponseUtil.createErrorResponse(500, "Internal server error");
        } catch (Exception e) {
            logger.error("Unexpected error occurred", e);
            return ResponseUtil.createErrorResponse(500, "Internal server error");
        } finally {
            MDC.clear();
        }
    }
    
    private void logConnectionPoolStatus() {
        try {
            ConnectionPoolManager poolManager = ConnectionPoolManager.getInstance();
            poolManager.getPoolStats();
            poolManager.isHealthy();
        } catch (Exception e) {
            logger.warn("Could not retrieve connection pool status: {}", e.getMessage());
        }
    }
    
    private void logFinalConnectionPoolStatus() {
        try {
            ConnectionPoolManager poolManager = ConnectionPoolManager.getInstance();
        } catch (Exception e) {
            logger.warn("Could not retrieve final connection pool status: {}", e.getMessage());
        }
    }
    
    private void logConnectionPoolStatusOnError() {
        try {
            ConnectionPoolManager poolManager = ConnectionPoolManager.getInstance();
            logger.error("Connection pool status on error: {}, healthy: {}", 
                        poolManager.getPoolStats(), poolManager.isHealthy());
        } catch (Exception e) {
            logger.error("Could not retrieve connection pool status on error: {}", e.getMessage());
        }
    }
}