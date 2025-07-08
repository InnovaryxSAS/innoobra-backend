package com.lambdas.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lambdas.dto.request.CreateProjectRequestDTO;
import com.lambdas.dto.response.ProjectResponseDTO;
import com.lambdas.exception.ProjectAlreadyExistsException;
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

public class CreateProjectHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Logger logger = LoggerFactory.getLogger(CreateProjectHandler.class);
    private static final ProjectService PROJECT_SERVICE = new ProjectService();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        String requestId = context.getAwsRequestId();
        MDC.put("requestId", requestId);
        
        try {
            logger.info("Starting project creation process");
            logConnectionPoolStatus();
            
            if (input.getBody() == null || input.getBody().trim().isEmpty()) {
                logger.warn("Request body is empty or null");
                return ResponseUtil.createErrorResponse(400, "Request body is required");
            }
            
            CreateProjectRequestDTO requestDTO = OBJECT_MAPPER.readValue(input.getBody(), CreateProjectRequestDTO.class);
            logger.debug("Parsed create request DTO");
            
            ValidationHelper.validateAndThrow(requestDTO, ValidationGroups.Create.class);
            logger.debug("Request validation passed");
            
            Project project = DTOMapper.toProject(requestDTO);
            logger.debug("Mapped request DTO to Project entity");
            
            Project createdProject = PROJECT_SERVICE.createProject(project);
            logger.info("Project created successfully with ID: {}", createdProject.getId());
            
            ProjectResponseDTO responseDTO = DTOMapper.toProjectResponseDTO(createdProject);
            logger.debug("Mapped created Project entity to response DTO");
            
            logFinalConnectionPoolStatus();
            
            return ResponseUtil.createResponse(201, responseDTO);
            
        } catch (JsonProcessingException e) {
            logger.error("JSON parsing error: {}", e.getMessage());
            return ResponseUtil.createErrorResponse(400, "Invalid JSON format");
        } catch (ValidationException e) {
            logger.warn("Validation error: {}", e.getMessage());
            return ResponseUtil.createErrorResponse(400, e.toMap());
        } catch (ProjectAlreadyExistsException e) {
            logger.warn("Project already exists: {}", e.getMessage());
            return ResponseUtil.createErrorResponse(409, e.getMessage());
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
            logger.debug("Connection pool status: {}, healthy: {}", 
                        poolManager.getPoolStats(), poolManager.isHealthy());
        } catch (Exception e) {
            logger.warn("Could not retrieve connection pool status: {}", e.getMessage());
        }
    }
    
    private void logFinalConnectionPoolStatus() {
        try {
            ConnectionPoolManager poolManager = ConnectionPoolManager.getInstance();
            logger.debug("Final connection pool status: {}", poolManager.getPoolStats());
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