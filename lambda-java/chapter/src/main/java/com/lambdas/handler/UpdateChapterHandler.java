package com.lambdas.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lambdas.dto.request.UpdateChapterRequestDTO;
import com.lambdas.dto.response.ChapterResponseDTO;
import com.lambdas.exception.ChapterNotFoundException;
import com.lambdas.exception.DatabaseException;
import com.lambdas.exception.ValidationException;
import com.lambdas.mapper.DTOMapper;
import com.lambdas.model.Chapter;
import com.lambdas.repository.ConnectionPoolManager;
import com.lambdas.service.ChapterService;
import com.lambdas.service.impl.ChapterServiceImpl;
import com.lambdas.util.HttpStatus;
import com.lambdas.util.LoggingHelper;
import com.lambdas.util.ResponseUtil;
import com.lambdas.util.ValidationHelper;
import com.lambdas.validation.groups.ValidationGroups;
import org.slf4j.Logger;

import java.util.Optional;

public class UpdateChapterHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Logger logger = LoggingHelper.getLogger(UpdateChapterHandler.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private final ChapterService chapterService;

    public UpdateChapterHandler() {
        this.chapterService = new ChapterServiceImpl();
    }

    public UpdateChapterHandler(ChapterService chapterService) {
        this.chapterService = chapterService;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        String requestId = context.getAwsRequestId();
        LoggingHelper.initializeRequestContext(requestId);

        try {
            String chapterId = null;
            if (input.getPathParameters() != null) {
                chapterId = input.getPathParameters().get("id");
            }
            
            if (chapterId == null || chapterId.trim().isEmpty()) {
                LoggingHelper.logMissingParameter(logger, "Chapter ID");
                return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "Chapter ID is required");
            }

            LoggingHelper.logProcessStart(logger, "chapter update");

            if (input.getBody() == null || input.getBody().trim().isEmpty()) {
                LoggingHelper.logEmptyRequestBody(logger);
                return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "Request body is required");
            }

            UpdateChapterRequestDTO requestDTO = OBJECT_MAPPER.readValue(input.getBody(), UpdateChapterRequestDTO.class);

            ValidationHelper.validateAndThrow(requestDTO, ValidationGroups.Update.class);

            Optional<Chapter> existingChapterOpt = chapterService.getChapterById(chapterId);
            if (!existingChapterOpt.isPresent()) {
                LoggingHelper.logEntityNotFound(logger, "Chapter", chapterId);
                return ResponseUtil.createErrorResponse(HttpStatus.NOT_FOUND, "Chapter not found");
            }

            Chapter existingChapter = existingChapterOpt.get();

            Chapter updatedChapter = DTOMapper.updateChapterFromDTO(existingChapter, requestDTO);
            Chapter savedChapter = chapterService.updateChapter(updatedChapter);

            LoggingHelper.logSuccess(logger, "Chapter update", chapterId);

            ChapterResponseDTO responseDTO = DTOMapper.toResponseDTO(savedChapter);

            return ResponseUtil.createResponse(HttpStatus.OK, responseDTO);

        } catch (JsonProcessingException e) {
            LoggingHelper.logJsonParsingError(logger, e.getMessage());
            return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "Invalid JSON format");
        } catch (ValidationException e) {
            LoggingHelper.logValidationError(logger, e.getMessage());
            return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, e.toMap());
        } catch (ChapterNotFoundException e) {
            LoggingHelper.logEntityNotFound(logger, "Chapter", e.getMessage());
            return ResponseUtil.createErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (DatabaseException e) {
            LoggingHelper.logDatabaseError(logger, e.getMessage(), e);
            logConnectionPoolStatusOnError();
            return ResponseUtil.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
        } catch (Exception e) {
            LoggingHelper.logUnexpectedError(logger, e.getMessage(), e);
            return ResponseUtil.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
        } finally {
            LoggingHelper.clearContext();
        }
    }

    private void logConnectionPoolStatusOnError() {
        try {
            ConnectionPoolManager poolManager = ConnectionPoolManager.getInstance();
            if (!poolManager.isHealthy()) {
                LoggingHelper.logConnectionPoolError(logger, 
                    poolManager.getPoolStats().toString(), false);
            }
        } catch (Exception e) {
            LoggingHelper.logConnectionPoolWarning(logger, 
                "Connection pool health check failed: " + e.getMessage());
        }
    }
}