package com.example.eventmanagement_backend.service;

import com.example.eventmanagement_backend.entity.Event;
import com.example.eventmanagement_backend.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventServiceTests {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventService eventService;

    private Event event;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        event = new Event();
        event.setId(1L);
        event.setTitle("Tech Conference");
        event.setDescription("Annual tech event");
        event.setVenue("Chennai");
        event.setEventDateTime(LocalDateTime.of(2025, 11, 15, 9, 0));
        event.setCategory("Technology");
    }

    @Test
    @DisplayName("Should create and return a new event")
    void testCreateEvent() {
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        Event savedEvent = eventService.createEvent(event);

        assertNotNull(savedEvent);
        assertEquals("Tech Conference", savedEvent.getTitle());
        verify(eventRepository, times(1)).save(event);
    }

    @Test
    @DisplayName("Should return all events from repository")
    void testGetAllEvents() {
        List<Event> events = List.of(event);
        when(eventRepository.findAll()).thenReturn(events);

        List<Event> result = eventService.getAllEvents();

        assertEquals(1, result.size());
        assertEquals("Tech Conference", result.get(0).getTitle());
        verify(eventRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return event by given ID")
    void testGetEventById() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        Optional<Event> result = eventService.getEventById(1L);

        assertTrue(result.isPresent());
        assertEquals("Tech Conference", result.get().getTitle());
        verify(eventRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw RuntimeException when event ID not found during update")
    void testUpdateEventNotFound() {
        when(eventRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> eventService.updateEvent(2L, event));
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    @DisplayName("Should update existing event details successfully")
    void testUpdateEvent() {
        Event updatedEvent = new Event();
        updatedEvent.setTitle("Updated Title");
        updatedEvent.setDescription("Updated Description");
        updatedEvent.setVenue("Updated Venue");
        updatedEvent.setCategory("Updated Category");
        updatedEvent.setEventDateTime(LocalDateTime.now());

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        Event result = eventService.updateEvent(1L, updatedEvent);

        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    @DisplayName("Should delete event by ID")
    void testDeleteEvent() {
        doNothing().when(eventRepository).deleteById(1L);

        eventService.deleteEvent(1L);

        verify(eventRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should search events by keyword")
    void testSearchEvents() {
        List<Event> mockEvents = List.of(event);
        when(eventRepository.searchByTitleOrVenueOrCategory("Tech")).thenReturn(mockEvents);

        List<Event> result = eventService.searchEvents("Tech");

        assertEquals(1, result.size());
        assertEquals("Tech Conference", result.get(0).getTitle());
        verify(eventRepository, times(1)).searchByTitleOrVenueOrCategory("Tech");
    }
}