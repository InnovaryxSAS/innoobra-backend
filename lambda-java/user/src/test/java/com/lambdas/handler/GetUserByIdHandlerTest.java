package com.lambdas.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lambdas.dto.response.UserResponseDTO;
import com.lambdas.exception.DatabaseException;
import com.lambdas.mapper.DTOMapper;
import com.lambdas.model.User;
import com.lambdas.model.UserStatus;
import com.lambdas.service.UserService;
import com.lambdas.util.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetUserByIdHandler Tests")
class GetUserByIdHandlerTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Mock
    private UserService userService;

    @Mock
    private Context context;

    private GetUserByIdHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GetUserByIdHandler(userService);
        when(context.getAwsRequestId()).thenReturn("test-request-id");
    }

    @Test
    @DisplayName("Should get user by ID successfully")
    void shouldGetUserByIdSuccessfully() throws JsonProcessingException {
        // Given
        String userId = "test-user-id";
        var requestEvent = createRequestEvent(userId);
        var user = createUser();
        var responseDTO = createResponseDTO(user);

        try (MockedStatic<DTOMapper> dtoMapperMock = mockStatic(DTOMapper.class)) {
            when(userService.getUserById(userId)).thenReturn(Optional.of(user));
            dtoMapperMock.when(() -> DTOMapper.toResponseDTO(user)).thenReturn(responseDTO);

            // When
            APIGatewayProxyResponseEvent response = handler.handleRequest(requestEvent, context);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            
            UserResponseDTO actualResponseDTO = OBJECT_MAPPER.readValue(response.getBody(), UserResponseDTO.class);
            assertThat(actualResponseDTO.getIdUser()).isEqualTo(user.getIdUser());
            assertThat(actualResponseDTO.getEmail()).isEqualTo(user.getEmail());
            assertThat(actualResponseDTO.getName()).isEqualTo(user.getName());
            assertThat(actualResponseDTO.getLastName()).isEqualTo(user.getLastName());

            verify(userService).getUserById(userId);
            dtoMapperMock.verify(() -> DTOMapper.toResponseDTO(user));
        }
    }

    @Test
    @DisplayName("Should return not found when user does not exist")
    void shouldReturnNotFoundWhenUserDoesNotExist() {
        // Given
        String userId = "non-existent-user";
        var requestEvent = createRequestEvent(userId);
        
        when(userService.getUserById(userId)).thenReturn(Optional.empty());

        // When
        APIGatewayProxyResponseEvent response = handler.handleRequest(requestEvent, context);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).contains("User not found");

        verify(userService).getUserById(userId);
    }

    @Test
    @DisplayName("Should return bad request when path parameters are null")
    void shouldReturnBadRequestWhenPathParametersAreNull() {
        // Given
        var requestEvent = new APIGatewayProxyRequestEvent();
        requestEvent.setPathParameters(null); // Más realista en API Gateway

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
    @DisplayName("Should return internal server error when DatabaseException is thrown")
    void shouldReturnInternalServerErrorWhenDatabaseExceptionIsThrown() {
        // Given
        String userId = "test-user-id";
        var requestEvent = createRequestEvent(userId);
        
        when(userService.getUserById(userId))
                .thenThrow(new DatabaseException("Database connection failed", new RuntimeException("Connection error")));

        // When
        APIGatewayProxyResponseEvent response = handler.handleRequest(requestEvent, context);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).contains("Internal server error");

        verify(userService).getUserById(userId);
    }

    @Test
    @DisplayName("Should return internal server error for unexpected exception")
    void shouldReturnInternalServerErrorForUnexpectedException() {
        // Given
        String userId = "test-user-id";
        var requestEvent = createRequestEvent(userId);
        
        when(userService.getUserById(userId))
                .thenThrow(new RuntimeException("Unexpected error"));

        // When
        APIGatewayProxyResponseEvent response = handler.handleRequest(requestEvent, context);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).contains("Internal server error");

        verify(userService).getUserById(userId);
    }

    @Test
    @DisplayName("Should handle DTOMapper serialization issues gracefully")
    void shouldHandleDTOMapperSerializationIssuesGracefully() {
        // Given
        String userId = "test-user-id";
        var requestEvent = createRequestEvent(userId);
        var user = createUser();

        try (MockedStatic<DTOMapper> dtoMapperMock = mockStatic(DTOMapper.class)) {
            when(userService.getUserById(userId)).thenReturn(Optional.of(user));
            dtoMapperMock.when(() -> DTOMapper.toResponseDTO(user))
                    .thenThrow(new RuntimeException("Serialization error"));

            // When
            APIGatewayProxyResponseEvent response = handler.handleRequest(requestEvent, context);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(response.getBody()).contains("Internal server error");

            verify(userService).getUserById(userId);
            dtoMapperMock.verify(() -> DTOMapper.toResponseDTO(user));
        }
    }

    private APIGatewayProxyRequestEvent createRequestEvent(String userId) {
        var requestEvent = new APIGatewayProxyRequestEvent();
        requestEvent.setPathParameters(Map.of("id", userId));
        return requestEvent;
    }

    private User createUser() {
        var user = new User();
        user.setIdUser("test-user-id");
        user.setIdCompany("company_123");
        user.setName("John");
        user.setLastName("Doe");
        user.setAddress("123 Main St");
        user.setPhone("555-1234");
        user.setEmail("john.doe@example.com");
        user.setPassword("password123");
        user.setPosition("Developer");
        user.setStatus(UserStatus.ACTIVE);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }

    private UserResponseDTO createResponseDTO(User user) {
        var dto = new UserResponseDTO();
        dto.setIdUser(user.getIdUser());
        dto.setIdCompany(user.getIdCompany());
        dto.setName(user.getName());
        dto.setLastName(user.getLastName());
        dto.setAddress(user.getAddress());
        dto.setPhone(user.getPhone());
        dto.setEmail(user.getEmail());
        dto.setPosition(user.getPosition());
        dto.setStatus(user.getStatus().getValue());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }
}