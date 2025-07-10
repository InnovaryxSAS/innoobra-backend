package com.lambdas.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.lambdas.dto.response.ChapterResponseDTO;
import com.lambdas.exception.DatabaseException;
import com.lambdas.mapper.DTOMapper;
import com.lambdas.model.Chapter;
import com.lambdas.service.ChapterService;
import com.lambdas.service.impl.ChapterServiceImpl;
import com.lambdas.util.HttpStatus;
import com.lambdas.util.LoggingHelper;
import com.lambdas.util.ResponseUtil;
import org.slf4j.Logger;

import java.util.Optional;

public class GetChapterByIdHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Logger logger = LoggingHelper.getLogger(GetChapterByIdHandler.class);

    private final ChapterService chapterService;

    public GetChapterByIdHandler() {
        this.chapterService = new ChapterServiceImpl();
    }

    public GetChapterByIdHandler(ChapterService chapterService) {
        this.chapterService = chapterService;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        String requestId = context.getAwsRequestId();
        LoggingHelper.initializeRequestContext(requestId);

        try {
            LoggingHelper.logProcessStart(logger, "chapter retrieval by ID");

            if (input.getPathParameters() == null) {
                LoggingHelper.logMissingParameter(logger, "Path parameters");
                return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "Chapter ID is required");
            }

            String chapterId = input.getPathParameters().get("id");
            if (chapterId == null || chapterId.trim().isEmpty()) {
                LoggingHelper.logMissingParameter(logger, "Chapter ID");
                return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "Chapter ID is required");
            }

            Optional<Chapter> chapterOpt = chapterService.getChapterById(chapterId);

            if (chapterOpt.isPresent()) {
                LoggingHelper.logSuccess(logger, "Chapter retrieval", chapterId);

                ChapterResponseDTO responseDTO = DTOMapper.toResponseDTO(chapterOpt.get());

                return ResponseUtil.createResponse(HttpStatus.OK, responseDTO);
            } else {
                LoggingHelper.logEntityNotFound(logger, "Chapter", chapterId);
                return ResponseUtil.createErrorResponse(HttpStatus.NOT_FOUND, "Chapter not found");
            }

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