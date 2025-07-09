package com.lambdas.util;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.HashMap;
import java.util.Map;

public class ResponseUtil {
    
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());
    
    public static APIGatewayProxyResponseEvent createResponse(int statusCode, Object body) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setStatusCode(statusCode);
        response.setHeaders(createHeaders());
        
        try {
            if (body != null) {
                response.setBody(objectMapper.writeValueAsString(body));
            }
        } catch (JsonProcessingException e) {
            return createErrorResponse(500, "Error serializing response");
        }
        
        return response;
    }
    
    public static APIGatewayProxyResponseEvent createErrorResponse(int statusCode, String message) {
        Map<String, String> errorBody = new HashMap<>();
        errorBody.put("error", message);
        errorBody.put("statusCode", String.valueOf(statusCode));
        
        return createResponse(statusCode, errorBody);
    }
    
    public static APIGatewayProxyResponseEvent createErrorResponse(int statusCode, Object errorObject) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setStatusCode(statusCode);
        response.setHeaders(createHeaders());
        
        try {
            response.setBody(objectMapper.writeValueAsString(errorObject));
        } catch (JsonProcessingException e) {
            Map<String, String> errorBody = new HashMap<>();
            errorBody.put("error", "Internal server error");
            errorBody.put("statusCode", String.valueOf(statusCode));
            
            try {
                response.setBody(objectMapper.writeValueAsString(errorBody));
            } catch (JsonProcessingException ex) {
                response.setBody("{\"error\":\"Internal server error\",\"statusCode\":\"" + statusCode + "\"}");
            }
        }
        
        return response;
    }
    
    private static Map<String, String> createHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        headers.put("Access-Control-Allow-Headers", "Content-Type, Authorization");
        return headers;
    }
}