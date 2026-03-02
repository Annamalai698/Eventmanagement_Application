package com.example.eventmanagement_backend.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "events")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Event ID")
    private Long id;

    @Schema(description = "Event title")
    private String title;
    @Schema(description = "Event description")
    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
    @Schema(description = "Event dateTime")
    private LocalDateTime eventDateTime;
    @Schema(description = "Event venue")
    private String venue;
    @Schema(description = "Event category")
    private String category;

    // Relationship: One event has multiple speakers
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Speaker> speakers = new ArrayList<>();

    // Relationship: One event has multiple registrations
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Registration> registrations = new ArrayList<>();
}
