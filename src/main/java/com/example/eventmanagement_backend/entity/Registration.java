package com.example.eventmanagement_backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Registration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Registration ID")
    private Long id;

    @Schema(description = "Registration date")
    private LocalDateTime registrationDate = LocalDateTime.now();

    // Status can be CONFIRMED, ATTENDED, CANCELLED
    @Schema(description = "Registration status")
    private String status = "CONFIRMED";

    // Relationship: Many registrations map to one user
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Relationship: Many registrations map to one event
    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    @JsonBackReference
    private Event event;

}
