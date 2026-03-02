package com.example.eventmanagement_backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "speakers")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Speaker {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Speaker ID")
    private Long id;

    @Schema(description = "Speaker name")
    private String name;
    @Schema(description = "Speaker bio")
    private String bio;

    // Relationship: Many speakers belong to one event
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "event_id", nullable = false)
    @JsonBackReference
    private Event event;
}
