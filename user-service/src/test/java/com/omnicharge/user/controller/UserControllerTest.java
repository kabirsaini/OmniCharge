package com.omnicharge.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.omnicharge.user.dto.UserDtos.*;
import com.omnicharge.user.service.UserDashboardService;
import com.omnicharge.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import java.security.Principal;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserDashboardService dashboardService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserResponse mockResponse;

    @BeforeEach
    void setUp() {
        mockResponse = UserResponse.builder()
                .id(1L)
                .email("test@example.com")
                .fullName("Test User")
                .mobile("9876543210")
                .role("USER")
                .enabled(true)
                .build();
    }

    @Test
    void createProfile_ValidRequest_ReturnsCreated() throws Exception {
        CreateProfileRequest request = new CreateProfileRequest();
        request.setEmail("test@example.com");
        request.setFullName("Test User");
        request.setMobile("9876543210");
        request.setRole("USER");

        when(userService.createProfile(any(CreateProfileRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/api/users/internal/create")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void getMyProfile_ReturnsOk() throws Exception {
        when(userService.getUserByEmail(anyString())).thenReturn(mockResponse);
        Principal principal = new UsernamePasswordAuthenticationToken("test@example.com", null);

        mockMvc.perform(get("/api/users/me").principal(principal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void updateProfile_ValidRequest_ReturnsOk() throws Exception {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setFullName("Updated Name");

        when(userService.getUserByEmail(anyString())).thenReturn(mockResponse);
        when(userService.updateProfile(anyLong(), any(UpdateProfileRequest.class))).thenReturn(mockResponse);

        Principal principal = new UsernamePasswordAuthenticationToken("test@example.com", null);

        mockMvc.perform(put("/api/users/me")
                .principal(principal)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Test User")); // mocked to return original name
    }

    @Test
    void getUserById_ValidId_ReturnsOk() throws Exception {
        when(userService.getUserById(1L)).thenReturn(mockResponse);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getAllUsers_ReturnsOk() throws Exception {
        when(userService.getAllUsers()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk());
    }
}
