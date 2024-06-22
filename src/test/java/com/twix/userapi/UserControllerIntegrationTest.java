package com.twix.userapi;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.twix.userapi.business.GlobalExceptionHandler;
import com.twix.userapi.business.exceptions.InvalidCredentialsException;
import com.twix.userapi.business.exceptions.UserNameAlreadyExistsException;
import com.twix.userapi.business.exceptions.UserNotExistException;
import com.twix.userapi.controller.CreateUserRequest;
import com.twix.userapi.controller.dto.LoginRequest;
import com.twix.userapi.controller.dto.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.twix.userapi.business.UserService;
import com.twix.userapi.controller.UserController;
import com.twix.userapi.repository.UserEntity;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserControllerIntegrationTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController, globalExceptionHandler).build();
    }

    @Test
    public void getAllUsers_HappyPath() throws Exception {
        List<UserEntity> users = List.of(new UserEntity(1L, "user1", "pass1", new HashSet<>(), new HashSet<>()));
        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/user/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].userName", is("user1")));
    }

    @Test
    public void getUserById_HappyPath() throws Exception {
        UserDTO userDTO = new UserDTO(1L, "user1", new HashSet<>(), new HashSet<>());
        when(userService.getUserById(1L)).thenReturn(userDTO);

        mockMvc.perform(get("/user/id/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.userName", is("user1")));
    }

    @Test
    public void getUserById_UserNotFound() throws Exception {
        when(userService.getUserById(1L)).thenThrow(new UserNotExistException("User with id 1 does not exist."));

        mockMvc.perform(get("/user/id/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void createUser_HappyPath() throws Exception {
        CreateUserRequest createUserRequest = new CreateUserRequest("newUser", "password");
        when(userService.createUser(any(CreateUserRequest.class))).thenReturn(1L);

        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().string("1"));
    }

    @Test
    public void createUser_UserNameExists() throws Exception {
        CreateUserRequest createUserRequest = new CreateUserRequest("existingUser", "password");
        doThrow(new UserNameAlreadyExistsException("Username already exists.")).when(userService).createUser(any(CreateUserRequest.class));

        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().isConflict())
                .andExpect(content().string(containsString("Username already exists.")));
    }

    @Test
    public void loginUser_HappyPath() throws Exception {
        LoginRequest loginRequest = new LoginRequest("user", "password");
        when(userService.loginUser("user", "password")).thenReturn(Optional.of("token"));

        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("token")));
    }

    @Test
    public void loginUser_InvalidCredentials() throws Exception {
        LoginRequest loginRequest = new LoginRequest("user", "wrongpassword");
        doThrow(new InvalidCredentialsException("Invalid credentials")).when(userService).loginUser("user", "wrongpassword");

        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString("Invalid credentials")));
    }

    @Test
    public void getUserByUsername_UserExists() throws Exception {
        UserDTO userDTO = new UserDTO(1L, "existingUser", new HashSet<>(), new HashSet<>());
        when(userService.getUserByUserName("existingUser")).thenReturn(userDTO);

        mockMvc.perform(get("/user/username/existingUser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName", is("existingUser")));
    }

    @Test
    public void getUserByUsername_UserNotFound() throws Exception {
        when(userService.getUserByUserName("nonExistingUser")).thenThrow(new UserNotExistException("User with username nonExistingUser does not exist."));

        mockMvc.perform(get("/user/username/nonExistingUser"))
                .andExpect(status().isNotFound());
    }
    @Test
    public void deleteUser_UserNotFound() throws Exception {
        when(userService.deleteUser(1L)).thenThrow(new UserNotExistException("User not found"));

        mockMvc.perform(delete("/user/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteUser_SuccessfulDeletion() throws Exception {
        when(userService.deleteUser(1L)).thenReturn(1L);  // Assuming the service returns the ID of the deleted user

        mockMvc.perform(delete("/user/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }

    @Test
    public void updateUser_UserNotFound() throws Exception {
        UserEntity userToUpdate = new UserEntity(1L, "user1", "password", new HashSet<>(), new HashSet<>());
        when(userService.updateUser(1L, userToUpdate)).thenThrow(new UserNotExistException("User not found"));

        mockMvc.perform(put("/user/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userToUpdate)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateUser_SuccessfulUpdate() throws Exception {
        UserEntity userToUpdate = new UserEntity(1L, "user1", "newpassword", new HashSet<>(), new HashSet<>());
        when(userService.updateUser(1L, userToUpdate)).thenReturn(1L);

        mockMvc.perform(put("/user/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userToUpdate)))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }
    @Test
    public void followUser_Success() throws Exception {
        doNothing().when(userService).followUser(1L, 2L);

        mockMvc.perform(post("/user/1/follow/2"))
                .andExpect(status().isOk());
    }
}
