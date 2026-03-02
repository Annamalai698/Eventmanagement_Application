package com.example.eventmanagement_backend.controller;

import com.example.eventmanagement_backend.entity.Event;
import com.example.eventmanagement_backend.repository.EventRepository;
import com.example.eventmanagement_backend.security.JwtAuthFilter;
import com.example.eventmanagement_backend.security.JwtUtils;
import com.example.eventmanagement_backend.service.EventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(EventController.class)
@AutoConfigureMockMvc(addFilters = false)
public class EventControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter; // If controller has filter dependency

    @MockBean
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EventRepository eventRepository;

    private Event event1;
    private Event event2;

    @BeforeEach
    void setup() {
        event1 = new Event();
        event1.setId(1L);
        event1.setTitle("Food Workshop");
        event1.setDescription("Cooking session");
        event1.setEventDateTime(LocalDateTime.of(2025,11,25,18,0));
        event1.setVenue("Chennai");
        event1.setCategory("Cooking");

        event2 = new Event();
        event2.setId(2L);
        event2.setTitle("Music Night");
        event2.setDescription("Open mic event");
        event2.setVenue("Bangalore");
        event2.setCategory("Music");
    }

    @Test
    void testGetAllEvents() throws Exception {
        List<Event> events = Arrays.asList(event1, event2);
        when(eventService.getAllEvents()).thenReturn(events);

        mockMvc.perform(get("/api/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(events.size()))
                .andExpect(jsonPath("$[0].title").value("Food Workshop"))
                .andExpect(jsonPath("$[1].title").value("Music Night"));

        verify(eventService, times(1)).getAllEvents();
    }

    @Test
    void testGetEventById_Found() throws Exception {
        when(eventService.getEventById(1L)).thenReturn(Optional.of(event1));

        mockMvc.perform(get("/api/events/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Food Workshop"))
                .andExpect(jsonPath("$.venue").value("Chennai"));

        verify(eventService, times(1)).getEventById(1L);
    }

    @Test
    void testGetEventById_NotFound() throws Exception {
        when(eventService.getEventById(3L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/events/3"))
                .andExpect(status().isNotFound());

        verify(eventService, times(1)).getEventById(3L);
    }

    @Test
    void testDeleteEvent() throws Exception {
        doNothing().when(eventService).deleteEvent(1L);

        mockMvc.perform(delete("/api/events/1"))
                .andExpect(status().isNoContent());

        verify(eventService, times(1)).deleteEvent(1L);
    }

    @Test
    void testSearchEvents() throws Exception {
        List<Event> searchResult = Arrays.asList(event2);
        when(eventService.searchEvents("Music")).thenReturn(searchResult);

        mockMvc.perform(get("/api/events/search").param("keyword", "Music"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].title").value("Music Night"));

        verify(eventService, times(1)).searchEvents("Music");
    }
}