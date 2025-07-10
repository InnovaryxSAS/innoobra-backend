package com.lambdas.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lambdas.dto.request.UpdateUserRequestDTO;
import com.lambdas.dto.response.UserResponseDTO;
import com.lambdas.exception.DatabaseException;
import com.lambdas.exception.UserNotFoundException;
import com.lambdas.exception.ValidationException;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateUserHandler Tests")
class UpdateUserHandlerTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private static final String TEST_USER_ID = "john_doe_123";

    @Mock
    private UserService userService;

    @Mock
    private Context context;

    private UpdateUserHandler handler;

    @BeforeEach
    void setUp() {
        handler = new UpdateUserHandler(userService);
        when(context.getAwsRequestId()).thenReturn("test-request-id");
    }

    @Test
    @DisplayName("Should update user successfully")
    void shouldUpdateUserSuccessfully() {
        var requestDTO = createValidUpdateRequestDTO();
        var requestEvent = createUpdateRequestEvent(requestDTO, TEST_USER_ID);
        var existingUser = createExistingUser();
        var updatedUser = createUpdatedUser();
        var responseDTO = createResponseDTO(updatedUser);

        try (MockedStatic<DTOMapper> dtoMapperMock = mockStatic(DTOMapper.class)) {
            when(userService.getUserById(TEST_USER_ID)).thenReturn(Optional.of(existingUser));
            
            dtoMapperMock.when(() -> DTOMapper.updateUserFromDTO(eq(existingUser), any(UpdateUserRequestDTO.class)))
                    .thenReturn(updatedUser);
            dtoMapperMock.when(() -> DTOMapper.toResponseDTO(any(User.class)))
                    .thenReturn(responseDTO);

            when(userService.updateUser(updatedUser)).thenReturn(updatedUser);

            APIGatewayProxyResponseEvent response = handler.handleRequest(requestEvent, context);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).contains("jane.doe@example.com");
            assertThat(response.getBody()).contains("Jane");
            assertThat(response.getBody()).contains("Smith");

            verify(userService).getUserById(TEST_USER_ID);
            verify(userService).updateUser(updatedUser);
            dtoMapperMock.verify(() -> DTOMapper.updateUserFromDTO(eq(existingUser), any(UpdateUserRequestDTO.class)));
            dtoMapperMock.verify(() -> DTOMapper.toResponseDTO(any(User.class)));
        }
    }

    @Test
    @DisplayName("Should return bad request when user ID is null")
    void shouldReturnBadRequestWhenUserIdIsNull() {
        var requestDTO = createValidUpdateRequestDTO();
        var requestEvent = createUpdateRequestEvent(requestDTO, null);

        APIGatewayProxyResponseEvent response = handler.handleRequest(requestEvent, context);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("User ID is required");
        verifyNoInteractions(userService);
    }

    @Test
    @DisplayName("Should return bad request when user ID is empty")
    void shouldReturnBadRequestWhenUserIdIsEmpty() {
        var requestDTO = createValidUpdateRequestDTO();
        var requestEvent = createUpdateRequestEvent(requestDTO, "   ");

        APIGatewayProxyResponseEvent response = handler.handleRequest(requestEvent, context);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("User ID is required");
        verifyNoInteractions(userService);
    }

    @Test
    @DisplayName("Should return bad request when body is null")
    void shouldReturnBadRequestWhenBodyIsNull() {
        var requestEvent = createUpdateRequestEventWithBody(null, TEST_USER_ID);

        APIGatewayProxyResponseEvent response = handler.handleRequest(requestEvent, context);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Request body is required");
        verifyNoInteractions(userService);
    }

    @Test
    @DisplayName("Should return bad request when body is empty")
    void shouldReturnBadRequestWhenBodyIsEmpty() {
        var requestEvent = createUpdateRequestEventWithBody("   ", TEST_USER_ID);

        APIGatewayProxyResponseEvent response = handler.handleRequest(requestEvent, context);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Request body is required");
        verifyNoInteractions(userService);
    }

    @Test
    @DisplayName("Should return bad request for invalid JSON")
    void shouldReturnBadRequestForInvalidJSON() {
        var requestEvent = createUpdateRequestEventWithBody("{invalid json}", TEST_USER_ID);

        APIGatewayProxyResponseEvent response = handler.handleRequest(requestEvent, context);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Invalid JSON format");
        verifyNoInteractions(userService);
    }

    @Test
    @DisplayName("Should return bad request for validation errors")
    void shouldReturnBadRequestForValidationErrors() {
        var requestDTO = createInvalidUpdateRequestDTO();
        var requestEvent = createUpdateRequestEvent(requestDTO, TEST_USER_ID);
        var existingUser = createExistingUser();

        when(userService.getUserById(TEST_USER_ID)).thenReturn(Optional.of(existingUser));

        try (MockedStatic<DTOMapper> dtoMapperMock = mockStatic(DTOMapper.class)) {
            var validationException = new ValidationException("Validation failed");

            dtoMapperMock.when(() -> DTOMapper.updateUserFromDTO(eq(existingUser), any(UpdateUserRequestDTO.class)))
                    .thenThrow(validationException);

            APIGatewayProxyResponseEvent response = handler.handleRequest(requestEvent, context);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            verify(userService).getUserById(TEST_USER_ID);
            verifyNoMoreInteractions(userService);
        }
    }

    @Test
    @DisplayName("Should return not found when user does not exist")
    void shouldReturnNotFoundWhenUserDoesNotExist() {
        var requestDTO = createValidUpdateRequestDTO();
        var requestEvent = createUpdateRequestEvent(requestDTO, TEST_USER_ID);

        when(userService.getUserById(TEST_USER_ID)).thenReturn(Optional.empty());

        APIGatewayProxyResponseEvent response = handler.handleRequest(requestEvent, context);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).contains("User not found");
        verify(userService).getUserById(TEST_USER_ID);
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("Should return not found when UserNotFoundException is thrown")
    void shouldReturnNotFoundWhenUserNotFoundExceptionIsThrown() {
        var requestDTO = createValidUpdateRequestDTO();
        var requestEvent = createUpdateRequestEvent(requestDTO, TEST_USER_ID);
        var existingUser = createExistingUser();
        var updatedUser = createUpdatedUser();

        try (MockedStatic<DTOMapper> dtoMapperMock = mockStatic(DTOMapper.class)) {
            when(userService.getUserById(TEST_USER_ID)).thenReturn(Optional.of(existingUser));
            
            dtoMapperMock.when(() -> DTOMapper.updateUserFromDTO(eq(existingUser), any(UpdateUserRequestDTO.class)))
                    .thenReturn(updatedUser);

            when(userService.updateUser(updatedUser))
                    .thenThrow(new UserNotFoundException("User not found during update"));

            APIGatewayProxyResponseEvent response = handler.handleRequest(requestEvent, context);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).contains("User not found during update");
            verify(userService).getUserById(TEST_USER_ID);
            verify(userService).updateUser(updatedUser);
        }
    }

    @Test
    @DisplayName("Should return internal server error for database exception")
    void shouldReturnInternalServerErrorForDatabaseException() {
        var requestDTO = createValidUpdateRequestDTO();
        var requestEvent = createUpdateRequestEvent(requestDTO, TEST_USER_ID);
        var existingUser = createExistingUser();
        var updatedUser = createUpdatedUser();

        try (MockedStatic<DTOMapper> dtoMapperMock = mockStatic(DTOMapper.class)) {
            when(userService.getUserById(TEST_USER_ID)).thenReturn(Optional.of(existingUser));
            
            dtoMapperMock.when(() -> DTOMapper.updateUserFromDTO(eq(existingUser), any(UpdateUserRequestDTO.class)))
                    .thenReturn(updatedUser);

            when(userService.updateUser(updatedUser))
                    .thenThrow(new DatabaseException("Database connection failed", new RuntimeException("Connection error")));

            APIGatewayProxyResponseEvent response = handler.handleRequest(requestEvent, context);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(response.getBody()).contains("Internal server error");
            verify(userService).getUserById(TEST_USER_ID);
            verify(userService).updateUser(updatedUser);
        }
    }

    @Test
    @DisplayName("Should return internal server error for unexpected exception")
    void shouldReturnInternalServerErrorForUnexpectedException() {
        var requestDTO = createValidUpdateRequestDTO();
        var requestEvent = createUpdateRequestEvent(requestDTO, TEST_USER_ID);
        var existingUser = createExistingUser();
        var updatedUser = createUpdatedUser();

        try (MockedStatic<DTOMapper> dtoMapperMock = mockStatic(DTOMapper.class)) {
            when(userService.getUserById(TEST_USER_ID)).thenReturn(Optional.of(existingUser));
            
            dtoMapperMock.when(() -> DTOMapper.updateUserFromDTO(eq(existingUser), any(UpdateUserRequestDTO.class)))
                    .thenReturn(updatedUser);

            when(userService.updateUser(updatedUser))
                    .thenThrow(new RuntimeException("Unexpected error"));

            APIGatewayProxyResponseEvent response = handler.handleRequest(requestEvent, context);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(response.getBody()).contains("Internal server error");
            verify(userService).getUserById(TEST_USER_ID);
            verify(userService).updateUser(updatedUser);
        }
    }

    @Test
    @DisplayName("Should handle database exception when getting user by ID")
    void shouldHandleDatabaseExceptionWhenGettingUserById() {
        var requestDTO = createValidUpdateRequestDTO();
        var requestEvent = createUpdateRequestEvent(requestDTO, TEST_USER_ID);

        when(userService.getUserById(TEST_USER_ID))
                .thenThrow(new DatabaseException("Database connection failed", new RuntimeException("Connection error")));

        APIGatewayProxyResponseEvent response = handler.handleRequest(requestEvent, context);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).contains("Internal server error");
        verify(userService).getUserById(TEST_USER_ID);
        verifyNoMoreInteractions(userService);
    }

    private UpdateUserRequestDTO createValidUpdateRequestDTO() {
        var dto = new UpdateUserRequestDTO();
        dto.setName("Jane");
        dto.setLastName("Smith");
        dto.setAddress("456 Oak St");
        dto.setPhone("555-9876");
        dto.setEmail("jane.doe@example.com");
        dto.setPassword("newpassword123");
        dto.setPosition("Senior Developer");
        dto.setStatus("active");
        return dto;
    }

    private UpdateUserRequestDTO createInvalidUpdateRequestDTO() {
        var dto = new UpdateUserRequestDTO();
        dto.setName(""); // Invalid: empty name
        dto.setEmail("invalid-email"); // Invalid: malformed email
        dto.setPassword("123"); // Invalid: too short
        return dto;
    }

    private User createExistingUser() {
        var user = new User();
        user.setIdUser(TEST_USER_ID);
        user.setIdCompany("company_123");
        user.setName("John");
        user.setLastName("Doe");
        user.setAddress("123 Main St");
        user.setPhone("555-1234");
        user.setEmail("john.doe@example.com");
        user.setPassword("password123");
        user.setPosition("Developer");
        user.setStatus(UserStatus.ACTIVE);
        user.setCreatedAt(LocalDateTime.now().minusDays(1));
        user.setUpdatedAt(LocalDateTime.now().minusDays(1));
        return user;
    }

    private User createUpdatedUser() {
        var user = new User();
        user.setIdUser(TEST_USER_ID);
        user.setIdCompany("company_123");
        user.setName("Jane");
        user.setLastName("Smith");
        user.setAddress("456 Oak St");
        user.setPhone("555-9876");
        user.setEmail("jane.doe@example.com");
        user.setPassword("newpassword123");
        user.setPosition("Senior Developer");
        user.setStatus(UserStatus.ACTIVE);
        user.setCreatedAt(LocalDateTime.now().minusDays(1));
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

    private APIGatewayProxyRequestEvent createUpdateRequestEvent(UpdateUserRequestDTO requestDTO, String userId) {
        var requestEvent = new APIGatewayProxyRequestEvent();
        if (userId != null) {
            requestEvent.setPathParameters(Map.of("id", userId));
        }
        try {
            requestEvent.setBody(OBJECT_MAPPER.writeValueAsString(requestDTO));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error creating test request", e);
        }
        return requestEvent;
    }

    private APIGatewayProxyRequestEvent createUpdateRequestEventWithBody(String body, String userId) {
        var requestEvent = new APIGatewayProxyRequestEvent();
        if (userId != null) {
            requestEvent.setPathParameters(Map.of("id", userId));
        }
        requestEvent.setBody(body);
        return requestEvent;
    }
}