package com.example.eventmanagement_backend.repository;


import com.example.eventmanagement_backend.entity.ERole;
import com.example.eventmanagement_backend.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RoleRepositoryTests {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RoleRepository roleRepository;

    // Variables to hold the actual persisted Role entities
    private Role userRole;
    private Role adminRole;

    @BeforeEach
    void setUp() {

        // 1. Persist ROLE_USER
        userRole = new Role(ERole.ROLE_USER);
        entityManager.persist(userRole);

        // 2. Persist ROLE_ADMIN
        adminRole = new Role(ERole.ROLE_ADMIN);
        entityManager.persist(adminRole);

        entityManager.flush();
    }

    @Test
    @DisplayName("FIND_BY_NAME_1/3: Should find the Role entity when name exists (ROLE_USER)")
    void findByName_UserRoleExists_ReturnsRole() {
        // ACT
        Optional<Role> result = roleRepository.findByName(ERole.ROLE_USER);

        // ASSERT
        assertThat(result).as("The Optional should be present").isPresent();
        assertThat(result.get().getName()).as("The retrieved role name should be ROLE_USER").isEqualTo(ERole.ROLE_USER);
        assertThat(result.get()).as("The retrieved object should match the persisted one").isEqualTo(userRole);
    }

    @Test
    @DisplayName("FIND_BY_NAME_2/3: Should find the Role entity when name exists (ROLE_ADMIN)")
    void findByName_AdminRoleExists_ReturnsRole() {
        // ACT
        Optional<Role> result = roleRepository.findByName(ERole.ROLE_ADMIN);

        // ASSERT
        assertThat(result).as("The Optional should be present").isPresent();
        assertThat(result.get().getName()).as("The retrieved role name should be ROLE_ADMIN").isEqualTo(ERole.ROLE_ADMIN);
        assertThat(result.get()).as("The retrieved object should match the persisted one").isEqualTo(adminRole);
    }
}