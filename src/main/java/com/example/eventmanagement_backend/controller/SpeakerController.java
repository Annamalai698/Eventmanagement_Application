package com.example.eventmanagement_backend.controller;

import com.example.eventmanagement_backend.entity.Speaker;
import com.example.eventmanagement_backend.service.SpeakerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/speakers")
@Tag(name = "Speaker Management Controller",
        description = "Manage the event speaker related operations from admin to database")
public class SpeakerController {

    @Autowired
    private SpeakerService speakerService;

    // Create Speaker for an Event
    @Operation(summary = "Create Speaker REST API",
            description = "store the event speaker info to database")
    @ApiResponse(responseCode = "200",
            description = "HTTP Status 200 OK")
    @PostMapping("/event/{eventId}")
    public ResponseEntity<Speaker> createSpeaker(@PathVariable Long eventId, @RequestBody Speaker speaker) {
        Speaker savedSpeaker = speakerService.createSpeaker(eventId, speaker);
        return ResponseEntity.ok(savedSpeaker);
    }

    @Operation(summary = "Retrieve speaker REST API",
            description = "Display the event speakers info by event from database")
    @GetMapping("/event/{eventId}")
    public List<Map<String, Object>> getSpeakersByEvent(@PathVariable Long eventId) {
        return speakerService.getSpeakersByEvent(eventId).stream()
                .map(s -> Map.of(
                        "id", s.getId(),
                        "name", s.getName(),
                        "bio", s.getBio(),
                        "event", Map.of("id", s.getEvent().getId(), "title", s.getEvent().getTitle())
                ))
                .collect(Collectors.toList());
    }

    @Operation(summary = "Retrieve every speaker REST API",
            description = "Show the every speaker from the database")
    @GetMapping
    public List<Speaker> getAllSpeakers() {
        return speakerService.getAllSpeakers();
    }

    @Operation(summary = "Retrieve every speaker by all event REST API",
            description = "Show the every speaker from the database")
    @GetMapping("/event/All")
    public List<Map<String, Object>> getAllSpeakersByAllEvent() {
        return speakerService.getAllSpeakers().stream()
                .map(s -> Map.of(
                        "id", s.getId(),
                        "name", s.getName(),
                        "bio", s.getBio(),
                        "event", Map.of("id", s.getEvent().getId(), "title", s.getEvent().getTitle())
                ))
                .collect(Collectors.toList());
    }


    // Update Speaker by ID
    @Operation(summary = "Update speaker REST API",
            description = "Update the speaker data from the existing speaker in database")
    @ApiResponse(responseCode = "200",
            description = "HTTP Status 200 OK")
    @PutMapping("/{speakerId}")
    public ResponseEntity<Speaker> updateSpeaker(@PathVariable Long speakerId, @RequestBody Speaker speakerDetails) {
        Speaker updatedSpeaker = speakerService.updateSpeaker(speakerId, speakerDetails);
        return ResponseEntity.ok(updatedSpeaker);
    }

    // Delete Speaker by ID
    @Operation(summary = "Remove speaker REST API",
            description = "Delete the speaker by id from the database")
    @ApiResponse(responseCode = "200",
            description = "HTTP Status 200 ok")
    @DeleteMapping("/{speakerId}")
    public ResponseEntity<String> deleteSpeaker(@PathVariable Long speakerId) {
        speakerService.deleteSpeaker(speakerId);
        return ResponseEntity.ok("Speaker deleted successfully.");
    }
}

