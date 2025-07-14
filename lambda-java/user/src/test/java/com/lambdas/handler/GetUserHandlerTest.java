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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetUsersHandler Tests")
class GetUserHandlerTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Mock
    private UserService userService;

    @Mock
    private Context context;

    private GetUserHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GetUserHandler(userService);
        when(context.getAwsRequestId()).thenReturn("test-request-id");
    }

    @Test
    @DisplayName("Should get all users successfully")
    void shouldGetAllUsersSuccessfully() throws JsonProcessingException {
        // Given
        var requestEvent = new APIGatewayProxyRequestEvent();
        var users = createUsersList();
        var responseDTOs = createResponseDTOList(users);

        try (MockedStatic<DTOMapper> dtoMapperMock = mockStatic(DTOMapper.class)) {
            when(userService.getAllUsers()).thenReturn(users);
            dtoMapperMock.when(() -> DTOMapper.toResponseDTOList(users)).thenReturn(responseDTOs);

            // When
            APIGatewayProxyResponseEvent response = handler.handleRequest(requestEvent, context);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            
            @SuppressWarnings("unchecked")
            List<UserResponseDTO> actualResponseDTOs = OBJECT_MAPPER.readValue(
                    response.getBody(), 
                    OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, UserResponseDTO.class)
            );
            
            assertThat(actualResponseDTOs).hasSize(2);
            assertThat(actualResponseDTOs.get(0).getIdUser()).isEqualTo("user1");
            assertThat(actualResponseDTOs.get(0).getEmail()).isEqualTo("user1@example.com");
            assertThat(actualResponseDTOs.get(1).getIdUser()).isEqualTo("user2");
            assertThat(actualResponseDTOs.get(1).getEmail()).isEqualTo("user2@example.com");

            verify(userService).getAllUsers();
            dtoMapperMock.verify(() -> DTOMapper.toResponseDTOList(users));
        }
    }

    @Test
    @DisplayName("Should return empty list when no users exist")
    void shouldReturnEmptyListWhenNoUsersExist() throws JsonProcessingException {
        // Given
        var requestEvent = new APIGatewayProxyRequestEvent();
        var emptyUsersList = Collections.<User>emptyList();
        var emptyResponseDTOs = Collections.<UserResponseDTO>emptyList();

        try (MockedStatic<DTOMapper> dtoMapperMock = mockStatic(DTOMapper.class)) {
            when(userService.getAllUsers()).thenReturn(emptyUsersList);
            dtoMapperMock.when(() -> DTOMapper.toResponseDTOList(emptyUsersList)).thenReturn(emptyResponseDTOs);

            // When
            APIGatewayProxyResponseEvent response = handler.handleRequest(requestEvent, context);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            
            @SuppressWarnings("unchecked")
            List<UserResponseDTO> actualResponseDTOs = OBJECT_MAPPER.readValue(
                    response.getBody(), 
                    OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, UserResponseDTO.class)
            );
            
            assertThat(actualResponseDTOs).isEmpty();

            verify(userService).getAllUsers();
            dtoMapperMock.verify(() -> DTOMapper.toResponseDTOList(emptyUsersList));
        }
    }

    @Test
    @DisplayName("Should return internal server error when DatabaseException is thrown")
    void shouldReturnInternalServerErrorWhenDatabaseExceptionIsThrown() {
        // Given
        var requestEvent = new APIGatewayProxyRequestEvent();
        
        when(userService.getAllUsers())
                .thenThrow(new DatabaseException("Database connection failed", new RuntimeException("Connection error")));

        // When
        APIGatewayProxyResponseEvent response = handler.handleRequest(requestEvent, context);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).contains("Internal server error");

        verify(userService).getAllUsers();
    }

    @Test
    @DisplayName("Should return internal server error for unexpected exception")
    void shouldReturnInternalServerErrorForUnexpectedException() {
        // Given
        var requestEvent = new APIGatewayProxyRequestEvent();
        
        when(userService.getAllUsers())
                .thenThrow(new RuntimeException("Unexpected error"));

        // When
        APIGatewayProxyResponseEvent response = handler.handleRequest(requestEvent, context);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).contains("Internal server error");

        verify(userService).getAllUsers();
    }

    @Test
    @DisplayName("Should handle DTOMapper serialization issues gracefully")
    void shouldHandleDTOMapperSerializationIssuesGracefully() {
        // Given
        var requestEvent = new APIGatewayProxyRequestEvent();
        var users = createUsersList();

        try (MockedStatic<DTOMapper> dtoMapperMock = mockStatic(DTOMapper.class)) {
            when(userService.getAllUsers()).thenReturn(users);
            dtoMapperMock.when(() -> DTOMapper.toResponseDTOList(users))
                    .thenThrow(new RuntimeException("Serialization error"));

            // When
            APIGatewayProxyResponseEvent response = handler.handleRequest(requestEvent, context);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(response.getBody()).contains("Internal server error");

            verify(userService).getAllUsers();
            dtoMapperMock.verify(() -> DTOMapper.toResponseDTOList(users));
        }
    }

    @Test
    @DisplayName("Should handle single user successfully")
    void shouldHandleSingleUserSuccessfully() throws JsonProcessingException {
        // Given
        var requestEvent = new APIGatewayProxyRequestEvent();
        var singleUserList = Arrays.asList(createUser("user1", "user1@example.com"));
        var singleResponseDTOList = Arrays.asList(createResponseDTO("user1", "user1@example.com"));

        try (MockedStatic<DTOMapper> dtoMapperMock = mockStatic(DTOMapper.class)) {
            when(userService.getAllUsers()).thenReturn(singleUserList);
            dtoMapperMock.when(() -> DTOMapper.toResponseDTOList(singleUserList)).thenReturn(singleResponseDTOList);

            // When
            APIGatewayProxyResponseEvent response = handler.handleRequest(requestEvent, context);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            
            @SuppressWarnings("unchecked")
            List<UserResponseDTO> actualResponseDTOs = OBJECT_MAPPER.readValue(
                    response.getBody(), 
                    OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, UserResponseDTO.class)
            );
            
            assertThat(actualResponseDTOs).hasSize(1);
            assertThat(actualResponseDTOs.get(0).getIdUser()).isEqualTo("user1");
            assertThat(actualResponseDTOs.get(0).getEmail()).isEqualTo("user1@example.com");

            verify(userService).getAllUsers();
            dtoMapperMock.verify(() -> DTOMapper.toResponseDTOList(singleUserList));
        }
    }

    private List<User> createUsersList() {
        return Arrays.asList(
                createUser("user1", "user1@example.com"),
                createUser("user2", "user2@example.com")
        );
    }

    private User createUser(String userId, String email) {
        var user = new User();
        user.setIdUser(userId);
        user.setIdCompany("company_123");
        user.setName("John");
        user.setLastName("Doe");
        user.setAddress("123 Main St");
        user.setPhone("555-1234");
        user.setEmail(email);
        user.setPassword("password123");
        user.setPosition("Developer");
        user.setStatus(UserStatus.ACTIVE);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }

    private List<UserResponseDTO> createResponseDTOList(List<User> users) {
        return users.stream()
                .map(user -> createResponseDTO(user.getIdUser(), user.getEmail()))
                .toList();
    }

    private UserResponseDTO createResponseDTO(String userId, String email) {
        var dto = new UserResponseDTO();
        dto.setIdUser(userId);
        dto.setIdCompany("company_123");
        dto.setName("John");
        dto.setLastName("Doe");
        dto.setAddress("123 Main St");
        dto.setPhone("555-1234");
        dto.setEmail(email);
        dto.setPosition("Developer");
        dto.setStatus(UserStatus.ACTIVE.getValue());
        dto.setCreatedAt(LocalDateTime.now());
        dto.setUpdatedAt(LocalDateTime.now());
        return dto;
    }
}