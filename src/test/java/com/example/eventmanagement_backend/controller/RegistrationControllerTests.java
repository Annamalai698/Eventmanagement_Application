package com.example.eventmanagement_backend.controller;

import com.example.eventmanagement_backend.entity.Registration;
import com.example.eventmanagement_backend.security.JwtAuthFilter;
import com.example.eventmanagement_backend.security.JwtUtils;
import com.example.eventmanagement_backend.service.RegistrationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.example.eventmanagement_backend.entity.User;
import com.example.eventmanagement_backend.entity.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = RegistrationController.class)
@AutoConfigureMockMvc(addFilters = false)
class RegistrationControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RegistrationService registrationService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtAuthFilter jwtAuthFilter; // Mock the filter

    @MockBean
    private JwtUtils jwtUtils;

    @Data
    private static class MockUser {
        private Long id;
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
    }

    @Data
    private static class MockEvent {
        private Long id;
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
    }

    private Registration createSampleRegistration(Long id, Long userId, Long eventId) {
        Registration reg = new Registration();
        reg.setId(id);

        // 1. Instantiate actual User entity
        User user = new User();
        user.setId(userId);

        // 2. Instantiate actual Event entity
        Event event = new Event();
        event.setId(eventId);

        // 3. Set the actual User and Event objects
        reg.setUser(user);
        reg.setEvent(event);

        reg.setRegistrationDate(LocalDateTime.now());
        reg.setStatus("CONFIRMED");
        return reg;
    }

    @Test
    @DisplayName("GET /api/registrations/event/{eventId} - Retrieve Registrations by Event ID")
    void getRegistrationsByEvent_Success() throws Exception {
        // Arrange
        Long eventId = 202L;
        Long userId1 = 1L;
        Long userId2 = 2L;

        Registration reg1 = createSampleRegistration(1L, userId1, eventId);
        Registration reg2 = createSampleRegistration(2L, userId2, eventId);
        List<Registration> expectedList = List.of(reg1, reg2);

        // Mock the service call
        when(registrationService.getRegistrationsByEvent(eventId)).thenReturn(expectedList);

        // Act & Assert
        mockMvc.perform(get("/api/registrations/event/{eventId}", eventId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(expectedList.size()));
    }

    @Test
    @DisplayName("GET /api/registrations/event/{eventId} - No Registrations Found")
    void getRegistrationsByEvent_EmptyList() throws Exception {
        // Arrange
        Long eventId = 999L;
        List<Registration> emptyList = List.of();

        // Mock the service call to return an empty list
        when(registrationService.getRegistrationsByEvent(eventId)).thenReturn(emptyList);

        // Act & Assert
        mockMvc.perform(get("/api/registrations/event/{eventId}", eventId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}