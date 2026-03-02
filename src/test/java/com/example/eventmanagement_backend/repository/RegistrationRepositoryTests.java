package com.example.eventmanagement_backend.repository;

import com.example.eventmanagement_backend.entity.Event;
import com.example.eventmanagement_backend.entity.Registration;
import com.example.eventmanagement_backend.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RegistrationRepositoryTests {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RegistrationRepository registrationRepository;

    // Test entities
    private User user1;
    private User user2;
    private Event eventA;
    private Event eventB;

    private Registration reg1Confirmed;
    private Registration reg2Confirmed;
    private Registration reg3Cancelled;

    @BeforeEach
    void setUp() {
        // 1. Setup Dependent Entities (User and Event)
        user1 = new User();
        user1.setEmail("user1@example.com");
        entityManager.persist(user1);

        user2 = new User();
        user2.setEmail("user2@example.com");
        entityManager.persist(user2);

        eventA = new Event();
        eventA.setTitle("Spring Boot Conference");
        entityManager.persist(eventA);

        eventB = new Event();
        eventB.setTitle("React Workshop");
        entityManager.persist(eventB);

        // 2. Setup Registration Entities
        // User 1 registered for Event A (CONFIRMED)
        reg1Confirmed = new Registration();
        reg1Confirmed.setUser(user1);
        reg1Confirmed.setEvent(eventA);
        reg1Confirmed.setStatus("CONFIRMED");
        entityManager.persist(reg1Confirmed);

        // User 2 registered for Event A (CONFIRMED)
        reg2Confirmed = new Registration();
        reg2Confirmed.setUser(user2);
        reg2Confirmed.setEvent(eventA);
        reg2Confirmed.setStatus("CONFIRMED");
        entityManager.persist(reg2Confirmed);

        // User 1 registered for Event B (CANCELLED)
        reg3Cancelled = new Registration();
        reg3Cancelled.setUser(user1);
        reg3Cancelled.setEvent(eventB);
        reg3Cancelled.setStatus("CANCELLED");
        entityManager.persist(reg3Cancelled);

        entityManager.flush();
    }


    @Test
    @DisplayName("FINDBY_USER_EVENT_1/3: Should find registration when both User ID and Event ID match")
    void findByUserIdAndEventId_Found_ReturnsRegistration() {
        // ACT
        Optional<Registration> result = registrationRepository.findByUserIdAndEventId(user1.getId(), eventA.getId());

        // ASSERT
        assertThat(result).as("Registration should be present").isPresent();
        assertThat(result.get()).isEqualTo(reg1Confirmed);
    }

    @Test
    @DisplayName("FINDBY_USER_EVENT_2/3: Should return empty optional when User ID does not match")
    void findByUserIdAndEventId_UserMismatch_ReturnsEmpty() {
        // ACT
        // Use user2's ID but search for eventB registration (which user1 owns)
        Optional<Registration> result = registrationRepository.findByUserIdAndEventId(user2.getId(), eventB.getId());

        // ASSERT
        assertThat(result).as("Registration should be empty").isEmpty();
    }

    @Test
    @DisplayName("FINDBY_USER_EVENT_3/3: Should return empty optional when Event ID does not match")
    void findByUserIdAndEventId_EventMismatch_ReturnsEmpty() {
        // ACT
        // Search for user1's registration in an event they didn't register for (using a non-existent ID or a mis-matched ID)
        // We'll search for user1 registered for a hypothetical event C (ID = 999)
        Optional<Registration> result = registrationRepository.findByUserIdAndEventId(user1.getId(), 999L);

        // ASSERT
        assertThat(result).as("Registration should be empty").isEmpty();
    }


    @Test
    @DisplayName("FINDBY_EVENT_STATUS_1/4: Should find multiple registrations for a specific event and status")
    void findByEventIdAndStatus_MultipleConfirmedRegistrations_Success() {
        // ACT
        List<Registration> results = registrationRepository.findByEventIdAndStatus(eventA.getId(), "CONFIRMED");

        // ASSERT
        assertThat(results)
                .as("Should find two confirmed registrations for Event A")
                .containsExactlyInAnyOrder(reg1Confirmed, reg2Confirmed)
                .hasSize(2);
    }

    @Test
    @DisplayName("FINDBY_EVENT_STATUS_2/4: Should find a single registration for a specific event and status")
    void findByEventIdAndStatus_SingleCancelledRegistration_Success() {
        // ACT
        List<Registration> results = registrationRepository.findByEventIdAndStatus(eventB.getId(), "CANCELLED");

        // ASSERT
        assertThat(results)
                .as("Should find one cancelled registration for Event B")
                .containsExactly(reg3Cancelled)
                .hasSize(1);
    }

    @Test
    @DisplayName("FINDBY_EVENT_STATUS_3/4: Should return empty list when no registrations match the status")
    void findByEventIdAndStatus_StatusMismatch_ReturnsEmpty() {
        // ACT
        // Event B only has CANCELLED registrations. Search for CONFIRMED.
        List<Registration> results = registrationRepository.findByEventIdAndStatus(eventB.getId(), "CONFIRMED");

        // ASSERT
        assertThat(results)
                .as("Should find no confirmed registrations for Event B")
                .isEmpty();
    }

    @Test
    @DisplayName("FINDBY_EVENT_STATUS_4/4: Should return empty list when event has no registrations")
    void findByEventIdAndStatus_EventMismatch_ReturnsEmpty() {
        // ACT
        // Search for a non-existent Event ID (e.g., 999L)
        List<Registration> results = registrationRepository.findByEventIdAndStatus(999L, "CONFIRMED");

        // ASSERT
        assertThat(results)
                .as("Should find no registrations for a non-existent event")
                .isEmpty();
    }
}