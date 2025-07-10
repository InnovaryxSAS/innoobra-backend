package com.lambdas.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lambdas.dto.response.DeleteResponseDTO;
import com.lambdas.exception.DatabaseException;
import com.lambdas.exception.UserNotFoundException;
import com.lambdas.service.UserService;
import com.lambdas.util.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeleteUserHandler Tests")
class DeleteUserHandlerTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Mock
    private UserService userService;

    @Mock
    private Context context;

    private DeleteUserHandler handler;

    @BeforeEach
    void setUp() {
        handler = new DeleteUserHandler(userService);
        when(context.getAwsRequestId()).thenReturn("test-request-id");
    }

    @Test
    @DisplayName("Should delete user successfully")
    void shouldDeleteUserSuccessfully() throws JsonProcessingException {
        // Given
        String userId = "test-user-id";
        var requestEvent = createRequestEvent(userId);
        
        when(userService.deactivateUser(userId)).thenReturn(true);

        // When
        APIGatewayProxyResponseEvent response = handler.handleRequest(requestEvent, context);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        DeleteResponseDTO responseDTO = OBJECT_MAPPER.readValue(response.getBody(), DeleteResponseDTO.class);
        assertThat(responseDTO.getMessage()).isEqualTo("User successfully deactivated");
        assertThat(responseDTO.getUserId()).isEqualTo(userId);
        assertThat(responseDTO.isSuccess()).isTrue();

        verify(userService).deactivateUser(userId);
    }

    @Test
    @DisplayName("Should return not found when user does not exist")
    void shouldReturnNotFoundWhenUserDoesNotExist() throws JsonProcessingException {
        // Given
        String userId = "non-existent-user";
        var requestEvent = createRequestEvent(userId);
        
        when(userService.deactivateUser(userId)).thenReturn(false);

        // When
        APIGatewayProxyResponseEvent response = handler.handleRequest(requestEvent, context);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        
        DeleteResponseDTO responseDTO = OBJECT_MAPPER.readValue(response.getBody(), DeleteResponseDTO.class);
        assertThat(responseDTO.getMessage()).isEqualTo("User not found");
        assertThat(responseDTO.getUserId()).isEqualTo(userId);
        assertThat(responseDTO.isSuccess()).isFalse();

        verify(userService).deactivateUser(userId);
    }

    @Test
    @DisplayName("Should return bad request when user ID is null")
    void shouldReturnBadRequestWhenUserIdIsNull() {
        // Given
        var requestEvent = new APIGatewayProxyRequestEvent();
        Map<String, String> pathParameters = new HashMap<>();
        pathParameters.put("id", null);
        requestEvent.setPathParameters(pathParameters);

        // When
        APIGatewayProxyResponseEvent response = handler.handleRequest(requestEvent, context);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("User ID is required");
        
        verifyNoInteractions(userService);
    }

    @Test
    @DisplayName("Should return bad request when user ID is empty")
    void shouldReturnBadRequestWhenUserIdIsEmpty() {
        // Given
        var requestEvent = createRequestEvent("   ");

        // When
        APIGatewayProxyResponseEvent response = handler.handleRequest(requestEvent, context);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("User ID is required");
        
        verifyNoInteractions(userService);
    }

    @Test
    @DisplayName("Should return not found when UserNotFoundException is thrown")
    void shouldReturnNotFoundWhenUserNotFoundExceptionIsThrown() throws JsonProcessingException {
        // Given
        String userId = "test-user-id";
        var requestEvent = createRequestEvent(userId);
        
        when(userService.deactivateUser(userId))
                .thenThrow(new UserNotFoundException("User not found with ID: " + userId));

        // When
        APIGatewayProxyResponseEvent response = handler.handleRequest(requestEvent, context);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        
        DeleteResponseDTO responseDTO = OBJECT_MAPPER.readValue(response.getBody(), DeleteResponseDTO.class);
        assertThat(responseDTO.getMessage()).isEqualTo("User not found with ID: " + userId);
        assertThat(responseDTO.getUserId()).isEqualTo(userId);
        assertThat(responseDTO.isSuccess()).isFalse();

        verify(userService).deactivateUser(userId);
    }

    @Test
    @DisplayName("Should return internal server error when DatabaseException is thrown")
    void shouldReturnInternalServerErrorWhenDatabaseExceptionIsThrown() {
        // Given
        String userId = "test-user-id";
        var requestEvent = createRequestEvent(userId);
        
        when(userService.deactivateUser(userId))
                .thenThrow(new DatabaseException("Database connection failed", new RuntimeException("Connection error")));

        // When
        APIGatewayProxyResponseEvent response = handler.handleRequest(requestEvent, context);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).contains("Internal server error");

        verify(userService).deactivateUser(userId);
    }

    @Test
    @DisplayName("Should return internal server error for unexpected exception")
    void shouldReturnInternalServerErrorForUnexpectedException() {
        // Given
        String userId = "test-user-id";
        var requestEvent = createRequestEvent(userId);
        
        when(userService.deactivateUser(userId))
                .thenThrow(new RuntimeException("Unexpected error"));

        // When
        APIGatewayProxyResponseEvent response = handler.handleRequest(requestEvent, context);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).contains("Internal server error");

        verify(userService).deactivateUser(userId);
    }

    private APIGatewayProxyRequestEvent createRequestEvent(String userId) {
        var requestEvent = new APIGatewayProxyRequestEvent();
        requestEvent.setPathParameters(Map.of("id", userId));
        return requestEvent;
    }
}