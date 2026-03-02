package com.example.eventmanagement_backend.service;

import com.example.eventmanagement_backend.entity.Event;
import com.example.eventmanagement_backend.entity.Registration;
import com.example.eventmanagement_backend.entity.User;
import com.example.eventmanagement_backend.repository.EventRepository;
import com.example.eventmanagement_backend.repository.RegistrationRepository;
import com.example.eventmanagement_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RegistrationService {

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Registration registerForEvent(Long userId, Long eventId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with ID: " + eventId));

        // Check for duplicate registration
        if (registrationRepository.findByUserIdAndEventId(userId, eventId).isPresent()) {
            throw new RuntimeException("User is already registered for this event.");
        }

        // Create and save the new registration
        Registration registration = new Registration();
        registration.setUser(user);
        registration.setEvent(event);
        registration = registrationRepository.save(registration);
        return registration;
    }

    public List<Registration> getRegistrationsByEvent(Long eventId) {

        return registrationRepository.findAll();
    }
}
