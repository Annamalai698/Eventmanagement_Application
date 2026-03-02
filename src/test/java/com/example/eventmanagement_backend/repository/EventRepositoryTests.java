package com.example.eventmanagement_backend.repository;

import com.example.eventmanagement_backend.entity.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest // Configures an in-memory database and only the JPA components
class EventRepositoryTests {

    @Autowired
    private TestEntityManager entityManager; // Used to insert test data

    @Autowired
    private EventRepository eventRepository; // The repository under test

    // Variables to hold the actual persisted Event entities
    private Event concertEvent;
    private Event conferenceEvent;
    private Event workshopEvent;
    private Event foodFestival;

    // Helper method to create and persist an Event (based on your entity structure)
    private Event createTestEvent(String title, String venue, String category) {
        // Since your entity has many fields, we use setters for robustness
        Event event = new Event();
        event.setTitle(title);
        event.setVenue(venue);
        event.setCategory(category);
        event.setDescription("Test Description");
        event.setEventDateTime(LocalDateTime.now());

        return entityManager.persist(event);
    }

    @BeforeEach
    void setUp() {
        // Clear and insert fresh test entities before each test

        // Event 1: Matching 'Music', 'Stadium', 'Concert'
        concertEvent = createTestEvent("Rock Concert", "City Stadium", "Music");

        // Event 2: Matching 'Tech', 'Center', 'Conference'
        conferenceEvent = createTestEvent("Java Conference", "Exhibition Center", "Tech");

        // Event 3: Matching 'Workshop' (only)
        workshopEvent = createTestEvent("Cooking Masterclass", "Kitchen Studio", "Food Workshop");

        // Event 4: Only 'Festival'
        foodFestival = createTestEvent("Summer Food Fest", "Park Grounds", "Cuisine");

        entityManager.flush(); // Commit transactions
    }

// -------------------------------------------------------------
// --- Test Cases for searchByTitleOrVenueOrCategory ---
// -------------------------------------------------------------

    @Test
    @DisplayName("SEARCH 1/5: Should return event matching keyword in Title (partial match)")
    void searchByTitleOrVenueOrCategory_MatchByTitle_Success() {
        // ACT
        List<Event> results = eventRepository.searchByTitleOrVenueOrCategory("Rock");

        // ASSERT
        assertThat(results)
                .as("Should find the 'Rock Concert' event by title keyword 'Rock'")
                .containsExactly(concertEvent)
                .hasSize(1);
    }

    @Test
    @DisplayName("SEARCH 2/5: Should return event matching keyword in Venue (partial match)")
    void searchByTitleOrVenueOrCategory_MatchByVenue_Success() {
        // ACT
        List<Event> results = eventRepository.searchByTitleOrVenueOrCategory("Studio");

        // ASSERT
        assertThat(results)
                .as("Should find the 'Cooking Masterclass' event by venue keyword 'Studio'")
                .containsExactly(workshopEvent)
                .hasSize(1);
    }

    @Test
    @DisplayName("SEARCH 3/5: Should return event matching keyword in Category (partial match)")
    void searchByTitleOrVenueOrCategory_MatchByCategory_Success() {
        // ACT
        List<Event> results = eventRepository.searchByTitleOrVenueOrCategory("Tech");

        // ASSERT
        assertThat(results)
                .as("Should find the 'Java Conference' event by category keyword 'Tech'")
                .containsExactly(conferenceEvent)
                .hasSize(1);
    }

    @Test
    @DisplayName("SEARCH 5/5: Should return an empty list when no events match the keyword")
    void searchByTitleOrVenueOrCategory_NoMatch_EmptyList() {
        // ACT
        List<Event> results = eventRepository.searchByTitleOrVenueOrCategory("NonexistentEventTag");

        // ASSERT
        assertThat(results)
                .as("Should not find any events")
                .isEmpty();
    }
}