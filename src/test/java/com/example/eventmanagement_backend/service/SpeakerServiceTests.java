package com.example.eventmanagement_backend.service;

import com.example.eventmanagement_backend.entity.Event;
import com.example.eventmanagement_backend.entity.Speaker;
import com.example.eventmanagement_backend.repository.EventRepository;
import com.example.eventmanagement_backend.repository.SpeakerRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpeakerServiceTests {

    @Mock
    private SpeakerRepository speakerRepository;

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private SpeakerService speakerService;

    private Event event;
    private Speaker speaker;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        event = new Event();
        event.setId(1L);
        event.setTitle("Tech Summit");

        speaker = new Speaker();
        speaker.setId(100L);
        speaker.setName("Dr. Alex");
        speaker.setBio("Expert in AI");
        speaker.setEvent(event);
    }

    @Test
    @DisplayName("Should successfully create a new speaker for an event")
    void testCreateSpeaker_Success() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(speakerRepository.save(any(Speaker.class))).thenReturn(speaker);

        Speaker result = speakerService.createSpeaker(1L, speaker);

        assertNotNull(result);
        assertEquals("Dr. Alex", result.getName());
        assertEquals(event, result.getEvent());
        verify(speakerRepository, times(1)).save(speaker);
    }

    @Test
    @DisplayName("Should throw exception when event not found while creating speaker")
    void testCreateSpeaker_EventNotFound() {
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> speakerService.createSpeaker(1L, speaker));

        assertEquals("Event not found with ID: 1", exception.getMessage());
    }

    @Test
    @DisplayName("Should successfully add speaker to an existing event")
    void testAddSpeaker_Success() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(speakerRepository.save(any(Speaker.class))).thenReturn(speaker);

        Speaker result = speakerService.addSpeaker(1L, speaker);

        assertNotNull(result);
        assertEquals(event, result.getEvent());
        verify(speakerRepository, times(1)).save(speaker);
    }

    @Test
    @DisplayName("Should throw exception when event not found while adding speaker")
    void testAddSpeaker_EventNotFound() {
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> speakerService.addSpeaker(1L, speaker));

        assertEquals("Event not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should return all speakers for a given event ID")
    void testGetSpeakersByEvent() {
        when(speakerRepository.findByEventId(1L)).thenReturn(List.of(speaker));

        List<Speaker> result = speakerService.getSpeakersByEvent(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Dr. Alex", result.get(0).getName());
    }

    @Test
    @DisplayName("Should return all speakers with their events")
    void testGetAllSpeakers() {
        when(speakerRepository.findAllWithEvent()).thenReturn(List.of(speaker));

        List<Speaker> result = speakerService.getAllSpeakers();

        assertEquals(1, result.size());
        verify(speakerRepository, times(1)).findAllWithEvent();
    }

    @Test
    @DisplayName("Should successfully update an existing speaker")
    void testUpdateSpeaker_Success() {
        Speaker updatedDetails = new Speaker();
        updatedDetails.setName("Dr. Smith");
        updatedDetails.setBio("AI and Robotics Expert");

        when(speakerRepository.findById(100L)).thenReturn(Optional.of(speaker));
        when(speakerRepository.save(any(Speaker.class))).thenReturn(speaker);

        Speaker result = speakerService.updateSpeaker(100L, updatedDetails);

        assertEquals("Dr. Smith", result.getName());
        assertEquals("AI and Robotics Expert", result.getBio());
        verify(speakerRepository, times(1)).save(speaker);
    }

    @Test
    @DisplayName("Should throw exception when updating non-existing speaker")
    void testUpdateSpeaker_NotFound() {
        when(speakerRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> speakerService.updateSpeaker(999L, speaker));

        assertEquals("Speaker not found with ID: 999", exception.getMessage());
    }

    @Test
    @DisplayName("Should successfully delete a speaker by ID")
    void testDeleteSpeaker_Success() {
        doNothing().when(speakerRepository).deleteById(100L);

        speakerService.deleteSpeaker(100L);

        verify(speakerRepository, times(1)).deleteById(100L);
    }
}