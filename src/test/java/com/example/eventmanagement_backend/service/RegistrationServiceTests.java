package com.example.eventmanagement_backend.service;

import com.example.eventmanagement_backend.entity.Event;
import com.example.eventmanagement_backend.entity.Registration;
import com.example.eventmanagement_backend.entity.User;
import com.example.eventmanagement_backend.repository.EventRepository;
import com.example.eventmanagement_backend.repository.RegistrationRepository;
import com.example.eventmanagement_backend.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTests {

    @Mock
    private RegistrationRepository registrationRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RegistrationService registrationService;

    private User user;
    private Event event;
    private Registration registration;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setName("John");

        event = new Event();
        event.setId(10L);
        event.setTitle("Tech Conference");

        registration = new Registration();
        registration.setId(100L);
        registration.setUser(user);
        registration.setEvent(event);
    }

    @Test
    @DisplayName("Should successfully register user for event")
    void testRegisterForEvent_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));
        when(registrationRepository.findByUserIdAndEventId(1L, 10L)).thenReturn(Optional.empty());
        when(registrationRepository.save(any(Registration.class))).thenReturn(registration);

        // Act
        Registration result = registrationService.registerForEvent(1L, 10L);

        // Assert
        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertEquals(event, result.getEvent());
        verify(registrationRepository, times(1)).save(any(Registration.class));
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void testRegisterForEvent_UserNotFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> registrationService.registerForEvent(1L, 10L));

        assertEquals("User not found with ID: 1", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when event not found")
    void testRegisterForEvent_EventNotFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(eventRepository.findById(10L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> registrationService.registerForEvent(1L, 10L));

        assertEquals("Event not found with ID: 10", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception if user already registered for event")
    void testRegisterForEvent_AlreadyRegistered() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));
        when(registrationRepository.findByUserIdAndEventId(1L, 10L))
                .thenReturn(Optional.of(registration));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> registrationService.registerForEvent(1L, 10L));

        assertEquals("User is already registered for this event.", exception.getMessage());
    }

    @Test
    @DisplayName("Should return all registrations for given event ID")
    void testGetRegistrationsByEvent() {
        // Arrange
        when(registrationRepository.findAll()).thenReturn(List.of(registration));

        // Act
        List<Registration> result = registrationService.getRegistrationsByEvent(10L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(registration, result.get(0));
    }
}