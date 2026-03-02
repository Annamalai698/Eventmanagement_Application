package com.example.eventmanagement_backend.repository;

import com.example.eventmanagement_backend.entity.ERole;
import com.example.eventmanagement_backend.entity.Role;
import com.example.eventmanagement_backend.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTests {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    // Test entities
    private Role userRole;
    private User testUser;

    // Test constants
    private final String EXISTING_EMAIL = "test.user@example.com";
    private final String NON_EXISTENT_EMAIL = "missing@example.com";

    @BeforeEach
    void setUp() {
        // 1. Setup Dependent Entity (Role)
        userRole = new Role(ERole.ROLE_USER);
        entityManager.persist(userRole);

        // 2. Setup Test User Entity
        testUser = new User(
                "Test User Name",
                EXISTING_EMAIL,
                "hashedpassword"
        );
        testUser.setRole(userRole);
        entityManager.persist(testUser);

        entityManager.flush();
    }

    @Test
    @DisplayName("FINDBY_EMAIL_1/2: Should find User when the email exists")
    void findByEmail_Exists_ReturnsUser() {
        // ACT
        Optional<User> result = userRepository.findByEmail(EXISTING_EMAIL);

        // ASSERT
        assertThat(result).as("Optional should contain the user").isPresent();
        assertThat(result.get().getEmail()).as("The retrieved email should match the expected email").isEqualTo(EXISTING_EMAIL);
        assertThat(result.get()).as("The retrieved object should match the persisted one").isEqualTo(testUser);
    }

    @Test
    @DisplayName("FINDBY_EMAIL_2/2: Should return empty optional when the email does not exist")
    void findByEmail_NotExists_ReturnsEmpty() {
        // ACT
        Optional<User> result = userRepository.findByEmail(NON_EXISTENT_EMAIL);

        // ASSERT
        assertThat(result).as("Optional should be empty").isEmpty();
    }

    @Test
    @DisplayName("EXISTSBY_EMAIL_1/2: Should return true when the email exists in the database")
    void existsByEmail_Exists_ReturnsTrue() {
        // ACT
        Boolean exists = userRepository.existsByEmail(EXISTING_EMAIL);

        // ASSERT
        assertThat(exists).as("Should return true for an existing email").isTrue();
    }

    @Test
    @DisplayName("EXISTSBY_EMAIL_2/2: Should return false when the email does not exist in the database")
    void existsByEmail_NotExists_ReturnsFalse() {
        // ACT
        Boolean exists = userRepository.existsByEmail(NON_EXISTENT_EMAIL);

        // ASSERT
        assertThat(exists).as("Should return false for a non-existent email").isFalse();
    }
}