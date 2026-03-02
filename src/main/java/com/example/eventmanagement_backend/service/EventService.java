package com.example.eventmanagement_backend.service;

import com.example.eventmanagement_backend.entity.Event;
import com.example.eventmanagement_backend.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    public Event createEvent(Event event) {
        // Add business validation here (e.g., check date validity)
        return eventRepository.save(event);
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    public Event updateEvent(Long id, Event event){
        Event existingEvent = eventRepository.findById(id).orElseThrow(() -> new RuntimeException("Id is not found"));

        existingEvent.setTitle(event.getTitle());
        existingEvent.setDescription(event.getDescription());
        existingEvent.setEventDateTime(event.getEventDateTime());
        existingEvent.setVenue(event.getVenue());
        existingEvent.setCategory(event.getCategory());

        return eventRepository.save(existingEvent);
    }

    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    public List<Event> searchEvents(String keyword) {
        return eventRepository.searchByTitleOrVenueOrCategory((keyword));
    }
}
