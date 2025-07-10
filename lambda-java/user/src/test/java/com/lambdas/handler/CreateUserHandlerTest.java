package com.lambdas.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lambdas.dto.request.CreateUserRequestDTO;
import com.lambdas.dto.response.UserResponseDTO;
import com.lambdas.exception.DatabaseException;
import com.lambdas.exception.UserAlreadyExistsException;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateUserHandler Tests")
class CreateUserHandlerTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Mock
    private UserService userService;

    @Mock
    private Context context;

    private CreateUserHandler handler;

    @BeforeEach
    void setUp() {
        handler = new CreateUserHandler(userService);
        when(context.getAwsRequestId()).thenReturn("test-request-id");
    }

    @Test
    @DisplayName("Should create user successfully")
    void shouldCreateUserSuccessfully() {
        var requestDTO = createValidRequestDTO();
        var requestEvent = createRequestEvent(requestDTO);
        var user = createUser();
        var createdUser = createCreatedUser();
        var responseDTO = createResponseDTO(createdUser);

        try (MockedStatic<DTOMapper> dtoMapperMock = mockStatic(DTOMapper.class)) {
            dtoMapperMock.when(() -> DTOMapper.toUser(any(CreateUserRequestDTO.class)))
                    .thenReturn(user);
            dtoMapperMock.when(() -> DTOMapper.toResponseDTO(any(User.class)))
                    .thenReturn(responseDTO);

            when(userService.createUser(user)).thenReturn(createdUser);

            APIGatewayProxyResponseEvent response = handler.handleRequest(requestEvent, context);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody()).contains("john.doe@example.com");
            assertThat(response.getBody()).contains("John");
            assertThat(response.getBody()).contains("Doe");

            verify(userService).createUser(user);
            dtoMapperMock.verify(() -> DTOMapper.toUser(any(CreateUserRequestDTO.class)));
            dtoMapperMock.verify(() -> DTOMapper.toResponseDTO(any(User.class)));
        }
    }

    @Test
    @DisplayName("Should return bad request when body is null")
    void shouldReturnBadRequestWhenBodyIsNull() {
        var requestEvent = new APIGatewayProxyRequestEvent();
        requestEvent.setBody(null);

        APIGatewayProxyResponseEvent response = handler.handleRequest(requestEvent, context);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Request body is required");
        verifyNoInteractions(userService);
    }

    @Test
    @DisplayName("Should return bad request when body is empty")
    void shouldReturnBadRequestWhenBodyIsEmpty() {
        var requestEvent = new APIGatewayProxyRequestEvent();
        requestEvent.setBody("   ");

        APIGatewayProxyResponseEvent response = handler.handleRequest(requestEvent, context);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Request body is required");
        verifyNoInteractions(userService);
    }

    @Test
    @DisplayName("Should return bad request for invalid JSON")
    void shouldReturnBadRequestForInvalidJSON() {
        var requestEvent = new APIGatewayProxyRequestEvent();
        requestEvent.setBody("{invalid json}");

        APIGatewayProxyResponseEvent response = handler.handleRequest(requestEvent, context);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Invalid JSON format");
        verifyNoInteractions(userService);
    }

    @Test
    @DisplayName("Should return bad request for validation errors")
    void shouldReturnBadRequestForValidationErrors() {
        var requestDTO = createInvalidRequestDTO();
        var requestEvent = createRequestEvent(requestDTO);

        try (MockedStatic<DTOMapper> dtoMapperMock = mockStatic(DTOMapper.class)) {
            var validationException = new ValidationException("Validation failed");

            dtoMapperMock.when(() -> DTOMapper.toUser(any(CreateUserRequestDTO.class)))
                    .thenThrow(validationException);

            APIGatewayProxyResponseEvent response = handler.handleRequest(requestEvent, context);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            verifyNoInteractions(userService);
        }
    }

    @Test
    @DisplayName("Should return conflict when user already exists")
    void shouldReturnConflictWhenUserAlreadyExists() {
        var requestDTO = createValidRequestDTO();
        var requestEvent = createRequestEvent(requestDTO);
        var user = createUser();

        try (MockedStatic<DTOMapper> dtoMapperMock = mockStatic(DTOMapper.class)) {
            dtoMapperMock.when(() -> DTOMapper.toUser(any(CreateUserRequestDTO.class)))
                    .thenReturn(user);

            when(userService.createUser(user))
                    .thenThrow(new UserAlreadyExistsException("User with email john.doe@example.com already exists"));

            APIGatewayProxyResponseEvent response = handler.handleRequest(requestEvent, context);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(response.getBody()).contains("User with email john.doe@example.com already exists");
            verify(userService).createUser(user);
        }
    }

    @Test
    @DisplayName("Should return internal server error for database exception")
    void shouldReturnInternalServerErrorForDatabaseException() {
        var requestDTO = createValidRequestDTO();
        var requestEvent = createRequestEvent(requestDTO);
        var user = createUser();

        try (MockedStatic<DTOMapper> dtoMapperMock = mockStatic(DTOMapper.class)) {
            dtoMapperMock.when(() -> DTOMapper.toUser(any(CreateUserRequestDTO.class)))
                    .thenReturn(user);

            when(userService.createUser(user))
                    .thenThrow(new DatabaseException("Database connection failed", new RuntimeException("Connection error")));

            APIGatewayProxyResponseEvent response = handler.handleRequest(requestEvent, context);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(response.getBody()).contains("Internal server error");
            verify(userService).createUser(user);
        }
    }

    @Test
    @DisplayName("Should return internal server error for unexpected exception")
    void shouldReturnInternalServerErrorForUnexpectedException() {
        var requestDTO = createValidRequestDTO();
        var requestEvent = createRequestEvent(requestDTO);
        var user = createUser();

        try (MockedStatic<DTOMapper> dtoMapperMock = mockStatic(DTOMapper.class)) {
            dtoMapperMock.when(() -> DTOMapper.toUser(any(CreateUserRequestDTO.class)))
                    .thenReturn(user);

            when(userService.createUser(user))
                    .thenThrow(new RuntimeException("Unexpected error"));

            APIGatewayProxyResponseEvent response = handler.handleRequest(requestEvent, context);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(response.getBody()).contains("Internal server error");
            verify(userService).createUser(user);
        }
    }

    @Test
    @DisplayName("Should handle serialization error in response")
    void shouldHandleSerializationErrorInResponse() {
        var requestDTO = createValidRequestDTO();
        var requestEvent = createRequestEvent(requestDTO);
        var user = createUser();
        var createdUser = createCreatedUser();

        try (MockedStatic<DTOMapper> dtoMapperMock = mockStatic(DTOMapper.class)) {
            dtoMapperMock.when(() -> DTOMapper.toUser(any(CreateUserRequestDTO.class)))
                    .thenReturn(user);

            when(userService.createUser(user)).thenReturn(createdUser);

            var problematicResponseDTO = mock(UserResponseDTO.class);
            dtoMapperMock.when(() -> DTOMapper.toResponseDTO(any(User.class)))
                    .thenReturn(problematicResponseDTO);

            APIGatewayProxyResponseEvent response = handler.handleRequest(requestEvent, context);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            verify(userService).createUser(user);
        }
    }


    private CreateUserRequestDTO createValidRequestDTO() {
        var dto = new CreateUserRequestDTO();
        dto.setIdUser("john_doe_123");
        dto.setIdCompany("company_123");
        dto.setName("John");
        dto.setLastName("Doe");
        dto.setAddress("123 Main St");
        dto.setPhone("555-1234");
        dto.setEmail("john.doe@example.com");
        dto.setPassword("password123");
        dto.setPosition("Developer");
        dto.setStatus("active"); 
        return dto;
    }

    private CreateUserRequestDTO createInvalidRequestDTO() {
        var dto = new CreateUserRequestDTO();
        dto.setIdUser(""); 
        dto.setEmail("invalid-email"); 
        dto.setName(""); 
        return dto;
    }

    private User createUser() {
        var user = new User();
        user.setIdUser("john_doe_123");
        user.setIdCompany("company_123");
        user.setName("John");
        user.setLastName("Doe");
        user.setAddress("123 Main St");
        user.setPhone("555-1234");
        user.setEmail("john.doe@example.com");
        user.setPassword("password123");
        user.setPosition("Developer");
        user.setStatus(UserStatus.ACTIVE);
        return user;
    }

    private User createCreatedUser() {
        var user = createUser();
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

    private APIGatewayProxyRequestEvent createRequestEvent(CreateUserRequestDTO requestDTO) {
        var requestEvent = new APIGatewayProxyRequestEvent();
        try {
            requestEvent.setBody(OBJECT_MAPPER.writeValueAsString(requestDTO));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error creating test request", e);
        }
        return requestEvent;
    }
}