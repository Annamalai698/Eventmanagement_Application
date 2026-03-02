package com.example.eventmanagement_backend.controller;

import com.example.eventmanagement_backend.entity.ERole;
import com.example.eventmanagement_backend.entity.Role;
import com.example.eventmanagement_backend.payload.request.SignupRequest;
import com.example.eventmanagement_backend.repository.RoleRepository;
import com.example.eventmanagement_backend.repository.SpeakerRepository;
import com.example.eventmanagement_backend.repository.UserRepository;
import com.example.eventmanagement_backend.security.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private PasswordEncoder encoder;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RoleRepository roleRepository;

    @MockBean
    private SpeakerRepository speakerRepository;

    @MockBean
    private JwtUtils jwtUtils;

    private Role role;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setId(1);
        role.setName(ERole.ROLE_USER);
    }

    @Test
    @DisplayName("Should return error when email already exists during signup")
    void testRegisterUser_EmailAlreadyExists() throws Exception {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setName("John");
        signupRequest.setEmail("john@example.com");
        signupRequest.setPassword("password");

        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Error: Email is already in use!")));

        verify(userRepository, times(1)).existsByEmail("john@example.com");
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should register user successfully with default USER role")
    void testRegisterUser_Success() throws Exception {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setName("John");
        signupRequest.setEmail("john@example.com");
        signupRequest.setPassword("password");

        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(encoder.encode(anyString())).thenReturn("encodedPass");
        when(roleRepository.findByName(ERole.ROLE_USER)).thenReturn(Optional.of(role));

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", containsString("User registered successfully")));

        verify(userRepository, times(1)).save(any());
    }
}