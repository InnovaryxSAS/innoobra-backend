package com.lambdas.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lambdas.dto.request.CreateApuDetailRequestDTO;
import com.lambdas.dto.response.ApuDetailResponseDTO;
import com.lambdas.exception.ApuDetailAlreadyExistsException;
import com.lambdas.exception.DatabaseException;
import com.lambdas.exception.ValidationException;
import com.lambdas.mapper.DTOMapper;
import com.lambdas.model.ApuDetail;
import com.lambdas.service.ApuDetailService;
import com.lambdas.service.impl.ApuDetailServiceImpl;
import com.lambdas.util.HttpStatus;
import com.lambdas.util.LoggingHelper;
import com.lambdas.util.ResponseUtil;
import com.lambdas.util.ValidationHelper;
import com.lambdas.validation.groups.ValidationGroups;
import org.slf4j.Logger;

public class CreateApuDetailHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Logger logger = LoggingHelper.getLogger(CreateApuDetailHandler.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule());
    
    private final ApuDetailService apuDetailService;

    public CreateApuDetailHandler() {
        this.apuDetailService = new ApuDetailServiceImpl();
    }

    // Constructor para inyección de dependencias (útil para testing)
    public CreateApuDetailHandler(ApuDetailService apuDetailService) {
        this.apuDetailService = apuDetailService;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        String requestId = context.getAwsRequestId();
        LoggingHelper.initializeRequestContext(requestId);
        
        try {
            if (input.getBody() == null || input.getBody().trim().isEmpty()) {
                return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "Request body is required");
            }
            
            CreateApuDetailRequestDTO requestDTO = OBJECT_MAPPER.readValue(input.getBody(), CreateApuDetailRequestDTO.class);
            
            ValidationHelper.validateAndThrow(requestDTO, ValidationGroups.Create.class);
            
            ApuDetail apuDetail = DTOMapper.toApuDetail(requestDTO);
            
            ApuDetail createdApuDetail = apuDetailService.createApuDetail(apuDetail);
            
            ApuDetailResponseDTO responseDTO = DTOMapper.toResponseDTO(createdApuDetail);
            
            return ResponseUtil.createResponse(HttpStatus.CREATED, responseDTO);
            
        } catch (JsonProcessingException e) {
            LoggingHelper.logJsonParsingError(logger, e.getMessage());
            return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "Invalid JSON format");
        } catch (ValidationException e) {
            LoggingHelper.logValidationError(logger, e.getMessage());
            return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, e.toMap());
        } catch (ApuDetailAlreadyExistsException e) {
            LoggingHelper.logEntityAlreadyExists(logger, "ApuDetail", e.getMessage());
            return ResponseUtil.createErrorResponse(HttpStatus.CONFLICT, e.getMessage());
        } catch (DatabaseException e) {
            LoggingHelper.logDatabaseError(logger, e.getMessage(), e);
            return ResponseUtil.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
        } catch (Exception e) {
            LoggingHelper.logUnexpectedError(logger, e.getMessage(), e);
            return ResponseUtil.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
        } finally {
            LoggingHelper.clearContext();
        }
    }
}