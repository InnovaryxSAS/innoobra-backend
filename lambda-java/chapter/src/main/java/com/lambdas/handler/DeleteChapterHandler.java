package com.lambdas.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.lambdas.dto.response.DeleteResponseDTO;
import com.lambdas.exception.ChapterNotFoundException;
import com.lambdas.exception.DatabaseException;
import com.lambdas.service.ChapterService;
import com.lambdas.service.impl.ChapterServiceImpl;
import com.lambdas.util.HttpStatus;
import com.lambdas.util.LoggingHelper;
import com.lambdas.util.ResponseUtil;
import org.slf4j.Logger;

public class DeleteChapterHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    
    private static final Logger logger = LoggingHelper.getLogger(DeleteChapterHandler.class);

    private final ChapterService chapterService;

    public DeleteChapterHandler() {
        this.chapterService = new ChapterServiceImpl();
    }

    public DeleteChapterHandler(ChapterService chapterService) {
        this.chapterService = chapterService;
    }
    
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        String requestId = context.getAwsRequestId();
        LoggingHelper.initializeRequestContext(requestId);
        
        try {
            LoggingHelper.logProcessStart(logger, "chapter deletion");
            
            String chapterId = input.getPathParameters().get("id");
            if (chapterId == null || chapterId.trim().isEmpty()) {
                LoggingHelper.logMissingParameter(logger, "Chapter ID");
                return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "Chapter ID is required");
            }
            
            boolean deleted = chapterService.deactivateChapter(chapterId);
            
            if (deleted) {
                LoggingHelper.logSuccess(logger, "Chapter deletion", chapterId);
                
                DeleteResponseDTO responseDTO = new DeleteResponseDTO.Builder()
                        .message("Chapter successfully deactivated")
                        .chapterId(chapterId)
                        .success(true)
                        .build();
                
                return ResponseUtil.createResponse(HttpStatus.OK, responseDTO);
            } else {
                LoggingHelper.logEntityNotFound(logger, "Chapter", chapterId);
                
                DeleteResponseDTO responseDTO = new DeleteResponseDTO.Builder()
                        .message("Chapter not found")
                        .chapterId(chapterId)
                        .success(false)
                        .build();
                
                return ResponseUtil.createResponse(HttpStatus.NOT_FOUND, responseDTO);
            }
            
        } catch (ChapterNotFoundException e) {
            LoggingHelper.logEntityNotFound(logger, "Chapter", e.getMessage());
            
            DeleteResponseDTO responseDTO = new DeleteResponseDTO.Builder()
                    .message(e.getMessage())
                    .chapterId(input.getPathParameters().get("id"))
                    .success(false)
                    .build();
                    
            return ResponseUtil.createResponse(HttpStatus.NOT_FOUND, responseDTO);
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