package com.example.eventmanagement_backend.controller;

import com.example.eventmanagement_backend.entity.Event;
import com.example.eventmanagement_backend.repository.EventRepository;
import com.example.eventmanagement_backend.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@Tag(name = "Event Management Controller",
        description = "Manage the event related operations from admin to database")
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private EventRepository eventRepository;

    // Publicly accessible endpoint
    @Operation(summary = "Retrieve all events REST API",
            description = "Give all event details to client from database")
    @GetMapping
    public List<Event> getAllEvents() {
        return eventService.getAllEvents();
    }

    // Publicly accessible endpoint
    @Operation(summary = "Retrieve events by id REST API",
            description = "share event details by event id to client from database")
    @ApiResponse(responseCode = "200",
            description = "HTTP Status 200 OK")
    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        return eventService.getEventById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create new event REST API",
            description = "Store event details to database")
    @ApiResponse(responseCode = "201",
            description = "HTTP Status 201 CREATED")
    @PostMapping()
    public ResponseEntity<Event> createEvent(@RequestBody Event event) {
        Event savedEvent = eventService.createEvent(event);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedEvent);
    }

    @Operation(summary = "update event REST API",
            description = "Update event info by event id in database")
    @ApiResponse(responseCode = "200",
            description = "HTTP Status 200 OK")
    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(@PathVariable Long id, @RequestBody Event event) {
        Event updated = eventService.updateEvent(id, event);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Delete event REST API",
            description = "Remove event by event id from database")
    @ApiResponse(responseCode = "204",
            description = "HTTP Status 204 No Content")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Search event REST API",
            description = "Display Search events by event title,category,venue from database")
    @GetMapping("/search")
    public List<Event> searchEvents(@RequestParam String keyword) {
        return eventService.searchEvents(keyword);
    }

}
