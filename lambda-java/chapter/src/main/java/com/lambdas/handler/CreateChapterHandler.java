package com.lambdas.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lambdas.dto.request.CreateChapterRequestDTO;
import com.lambdas.dto.response.ChapterResponseDTO;
import com.lambdas.exception.ChapterAlreadyExistsException;
import com.lambdas.exception.DatabaseException;
import com.lambdas.exception.ValidationException;
import com.lambdas.mapper.DTOMapper;
import com.lambdas.model.Chapter;
import com.lambdas.service.ChapterService;
import com.lambdas.service.impl.ChapterServiceImpl;
import com.lambdas.util.HttpStatus;
import com.lambdas.util.LoggingHelper;
import com.lambdas.util.ResponseUtil;
import com.lambdas.util.ValidationHelper;
import com.lambdas.validation.groups.ValidationGroups;
import org.slf4j.Logger;

public class CreateChapterHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Logger logger = LoggingHelper.getLogger(CreateChapterHandler.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule());
    
    private final ChapterService chapterService;

    public CreateChapterHandler() {
        this.chapterService = new ChapterServiceImpl();
    }

    public CreateChapterHandler(ChapterService chapterService) {
        this.chapterService = chapterService;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        String requestId = context.getAwsRequestId();
        LoggingHelper.initializeRequestContext(requestId);
        
        try {
            LoggingHelper.logProcessStart(logger, "chapter creation");
            
            if (input.getBody() == null || input.getBody().trim().isEmpty()) {
                LoggingHelper.logEmptyRequestBody(logger);
                return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "Request body is required");
            }
            
            CreateChapterRequestDTO requestDTO = OBJECT_MAPPER.readValue(input.getBody(), CreateChapterRequestDTO.class);
            
            ValidationHelper.validateAndThrow(requestDTO, ValidationGroups.Create.class);
            
            Chapter chapter = DTOMapper.toChapter(requestDTO);
            
            Chapter createdChapter = chapterService.createChapter(chapter);
            
            ChapterResponseDTO responseDTO = DTOMapper.toResponseDTO(createdChapter);
            
            LoggingHelper.logSuccess(logger, "Chapter creation", createdChapter.getIdChapter());
            
            return ResponseUtil.createResponse(HttpStatus.CREATED, responseDTO);
            
        } catch (JsonProcessingException e) {
            LoggingHelper.logJsonParsingError(logger, e.getMessage());
            return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "Invalid JSON format");
        } catch (ValidationException e) {
            LoggingHelper.logValidationError(logger, e.getMessage());
            return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, e.toMap());
        } catch (ChapterAlreadyExistsException e) {
            LoggingHelper.logEntityAlreadyExists(logger, "Chapter", e.getMessage());
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