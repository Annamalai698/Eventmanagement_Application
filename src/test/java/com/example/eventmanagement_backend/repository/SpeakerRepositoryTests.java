package com.example.eventmanagement_backend.repository;

import com.example.eventmanagement_backend.entity.Event;
import com.example.eventmanagement_backend.entity.Speaker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SpeakerRepositoryTests {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SpeakerRepository speakerRepository;

    // Test entities
    private Event eventA;
    private Event eventB;

    private Speaker speaker1A; // Speaker 1 for Event A
    private Speaker speaker2A; // Speaker 2 for Event A
    private Speaker speaker3B; // Speaker 3 for Event B

    // Helper method to create and persist an Event
    private Event createTestEvent(String title) {
        Event event = new Event(); // Assuming Event has a default constructor/setters
        event.setTitle(title);
        // Set other required fields if necessary (omitted for brevity)
        return entityManager.persist(event);
    }

    // Helper method to create and persist a Speaker
    private Speaker createTestSpeaker(String name, Event event) {
        Speaker speaker = new Speaker(); // Assuming Speaker has a default constructor/setters
        speaker.setName(name);
        speaker.setBio("Bio for " + name);
        speaker.setEvent(event);
        return entityManager.persist(speaker);
    }

    @BeforeEach
    void setUp() {
        // 1. Setup Dependent Entities (Events)
        eventA = createTestEvent("Tech Conference");
        eventB = createTestEvent("Design Workshop");

        // 2. Setup Speaker Entities
        speaker1A = createTestSpeaker("Alice Johnson", eventA);
        speaker2A = createTestSpeaker("Bob Smith", eventA);
        speaker3B = createTestSpeaker("Charlie Brown", eventB);

        entityManager.flush(); // Commit transactions
    }

    @Test
    @DisplayName("FINDBY_EVENT_ID_1/3: Should return all speakers associated with a specific event ID (Event A)")
    void findByEventId_SpecificEventId_ReturnsSpeakersList() {
        // ACT
        List<Speaker> results = speakerRepository.findByEventId(eventA.getId());

        // ASSERT
        assertThat(results)
                .as("Should find two speakers (Alice and Bob) for Event A")
                .containsExactlyInAnyOrder(speaker1A, speaker2A)
                .hasSize(2);
    }

    @Test
    @DisplayName("FINDBY_EVENT_ID_2/3: Should return a single speaker associated with a specific event ID (Event B)")
    void findByEventId_SingleSpeakerEvent_ReturnsSingleSpeaker() {
        // ACT
        List<Speaker> results = speakerRepository.findByEventId(eventB.getId());

        // ASSERT
        assertThat(results)
                .as("Should find one speaker (Charlie) for Event B")
                .containsExactly(speaker3B)
                .hasSize(1);
    }

    @Test
    @DisplayName("FINDBY_EVENT_ID_3/3: Should return an empty list when no speakers are associated with the event ID")
    void findByEventId_NonExistentEvent_ReturnsEmptyList() {
        // ACT
        List<Speaker> results = speakerRepository.findByEventId(999L); // Use a non-existent ID

        // ASSERT
        assertThat(results)
                .as("Should find no speakers for a non-existent event")
                .isEmpty();
    }

    @Test
    @DisplayName("FIND_ALL_WITH_EVENT_1/2: Should return all speakers with their associated event eagerly fetched")
    void findAllWithEvent_ReturnsAllSpeakers_FetchesEvents() {
        // ACT
        List<Speaker> results = speakerRepository.findAllWithEvent();

        // ASSERT
        assertThat(results)
                .as("Should retrieve all three speakers")
                .containsExactlyInAnyOrder(speaker1A, speaker2A, speaker3B)
                .hasSize(3);

        assertThat(results.stream().anyMatch(s -> s.getEvent().getTitle().equals("Tech Conference"))).isTrue();
        assertThat(results.stream().anyMatch(s -> s.getEvent().getTitle().equals("Design Workshop"))).isTrue();
    }

    @Test
    @DisplayName("FIND_ALL_WITH_EVENT_2/2: Should return an empty list if no speakers exist")
    void findAllWithEvent_NoSpeakers_ReturnsEmptyList() {
        // Arrange: Clear existing speakers
        speakerRepository.deleteAll();

        // ACT
        List<Speaker> results = speakerRepository.findAllWithEvent();

        // ASSERT
        assertThat(results)
                .as("Should return an empty list when no speakers are present")
                .isEmpty();
    }
}