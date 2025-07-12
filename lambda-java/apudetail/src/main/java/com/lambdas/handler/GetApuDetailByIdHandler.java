package com.lambdas.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.lambdas.dto.response.ApuDetailResponseDTO;
import com.lambdas.exception.DatabaseException;
import com.lambdas.mapper.DTOMapper;
import com.lambdas.model.ApuDetail;
import com.lambdas.service.ApuDetailService;
import com.lambdas.service.impl.ApuDetailServiceImpl;
import com.lambdas.util.HttpStatus;
import com.lambdas.util.LoggingHelper;
import com.lambdas.util.ResponseUtil;
import org.slf4j.Logger;

import java.util.Optional;

public class GetApuDetailByIdHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Logger logger = LoggingHelper.getLogger(GetApuDetailByIdHandler.class);

    private final ApuDetailService apuDetailService;

    public GetApuDetailByIdHandler() {
        this.apuDetailService = new ApuDetailServiceImpl();
    }

    // Constructor para inyección de dependencias (útil para testing)
    public GetApuDetailByIdHandler(ApuDetailService apuDetailService) {
        this.apuDetailService = apuDetailService;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        String requestId = context.getAwsRequestId();
        LoggingHelper.initializeRequestContext(requestId);

        try {
            if (input.getPathParameters() == null) {
                return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "ApuDetail ID is required");
            }

            String apuDetailId = input.getPathParameters().get("id");
            if (apuDetailId == null || apuDetailId.trim().isEmpty()) {
                return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "ApuDetail ID is required");
            }

            LoggingHelper.addApuDetailId(apuDetailId);

            Optional<ApuDetail> apuDetailOpt = apuDetailService.getApuDetailById(apuDetailId);

            if (apuDetailOpt.isPresent()) {
                ApuDetailResponseDTO responseDTO = DTOMapper.toResponseDTO(apuDetailOpt.get());
                return ResponseUtil.createResponse(HttpStatus.OK, responseDTO);
            } else {
                return ResponseUtil.createErrorResponse(HttpStatus.NOT_FOUND, "ApuDetail not found");
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