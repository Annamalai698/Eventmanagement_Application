package com.example.eventmanagement_backend.controller;

import com.example.eventmanagement_backend.entity.Registration;
import com.example.eventmanagement_backend.service.RegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/registrations")
@RequiredArgsConstructor
@Tag(name = "Event Registration Controller",
        description = "Manage the event registration status maintained")
public class RegistrationController {

    private final RegistrationService registrationService;

    @Operation(summary = "Register event REST API",
            description = "store the event registration info of user in database")
    @ApiResponse(responseCode = "200",
            description = "HTTP Status 200 OK")
    @PostMapping("/register")
    public ResponseEntity<Registration> register(@RequestBody Map<String, Long> payload) {
        Long userId = payload.get("userId");
        Long eventId = payload.get("eventId");
        Registration reg = registrationService.registerForEvent(userId, eventId);
        return ResponseEntity.ok(reg);
    }

    @Operation(summary = "Retrieve registration by event REST API",
            description = "Display the registered event by event id from the database")
    @ApiResponse(responseCode = "200",
            description = "HTTP Status 200 OK")
    @GetMapping(value = "/event/{eventId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Registration>> getRegistrationsByEvent(@PathVariable Long eventId) {
        List<Registration> registrations = registrationService.getRegistrationsByEvent(eventId);
        return ResponseEntity.ok(registrations);
    }


}

