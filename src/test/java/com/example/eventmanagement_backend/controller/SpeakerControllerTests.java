package com.example.eventmanagement_backend.controller;

import com.example.eventmanagement_backend.entity.Event;
import com.example.eventmanagement_backend.entity.Speaker;
import com.example.eventmanagement_backend.service.SpeakerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class SpeakerControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SpeakerService speakerService;

    private Speaker speaker1;
    private Speaker speaker2;

    @BeforeEach
    void setup() {
        speaker1 = new Speaker();
        speaker1.setId(1L);
        speaker1.setName("John Doe");
        speaker1.setBio("Java Basics");

        speaker2 = new Speaker();
        speaker2.setId(2L);
        speaker2.setName("Jane Smith");
        speaker2.setBio("Spring Boot");
    }

    @Test
    @DisplayName("GET /api/speakers/event/{eventId} - should return speakers for a specific event")
    void testGetSpeakersByEvent() throws Exception {
        // Arrange
        Event event = new Event();
        event.setId(1L);
        event.setTitle("Tech Conference");

        Speaker speaker = new Speaker();
        speaker.setId(10L);
        speaker.setName("John Doe");
        speaker.setBio("Expert in Cloud Computing");
        speaker.setEvent(event);

        when(speakerService.getSpeakersByEvent(1L)).thenReturn(List.of(speaker));

        // Act & Assert
        mockMvc.perform(get("/api/speakers/event/{eventId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10L))
                .andExpect(jsonPath("$[0].name").value("John Doe"))
                .andExpect(jsonPath("$[0].bio").value("Expert in Cloud Computing"))
                .andExpect(jsonPath("$[0].event.id").value(1L))
                .andExpect(jsonPath("$[0].event.title").value("Tech Conference"));
    }

    // -------------------------------------------------------------
    @Test
    @DisplayName("GET /api/speakers/event/All - should return all speakers across all events")
    void testGetAllSpeakersByAllEvent() throws Exception {
        // Arrange
        Event event1 = new Event();
        event1.setId(1L);
        event1.setTitle("Tech Summit");

        Event event2 = new Event();
        event2.setId(2L);
        event2.setTitle("Health Expo");

        Speaker speaker1 = new Speaker(1L, "Alice", "AI Specialist", event1);
        Speaker speaker2 = new Speaker(2L, "Bob", "Nutrition Expert", event2);

        when(speakerService.getAllSpeakers()).thenReturn(List.of(speaker1, speaker2));

        // Act & Assert
        mockMvc.perform(get("/api/speakers/event/All")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Alice"))
                .andExpect(jsonPath("$[0].event.title").value("Tech Summit"))
                .andExpect(jsonPath("$[1].name").value("Bob"))
                .andExpect(jsonPath("$[1].event.title").value("Health Expo"));
    }

    /*@Test
    @DisplayName("GET /api/speakers - Retrieve All Speakers")
    void getAllSpeakers_Success() throws Exception {
        List<Speaker> speakers = List.of(speaker1, speaker2);
        when(speakerService.getAllSpeakers()).thenReturn(speakers);

        mockMvc.perform(get("/api/speakers")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(2));

        verify(speakerService, times(1)).getAllSpeakers();
    }*/

    @Test
    @DisplayName("DELETE /api/speakers/{speakerId} - Delete Speaker")
    void deleteSpeaker_Success() throws Exception {
        doNothing().when(speakerService).deleteSpeaker(1L);

        mockMvc.perform(delete("/api/speakers/{speakerId}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string("Speaker deleted successfully."));

        verify(speakerService, times(1)).deleteSpeaker(1L);
    }
}