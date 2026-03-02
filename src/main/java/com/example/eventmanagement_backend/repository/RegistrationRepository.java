package com.example.eventmanagement_backend.repository;

import com.example.eventmanagement_backend.entity.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository

public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    // Find a registration by User ID and Event ID to prevent duplicate registrations
    Optional<Registration> findByUserIdAndEventId(Long userId, Long eventId);

    List<Registration> findByEventIdAndStatus(Long eventId, String status);

}
