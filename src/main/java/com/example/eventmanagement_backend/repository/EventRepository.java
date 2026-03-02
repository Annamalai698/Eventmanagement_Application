package com.example.eventmanagement_backend.repository;

import com.example.eventmanagement_backend.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    // Custom search methods can be defined here (e.g., findByTitleContaining)

    @Query("SELECT e FROM Event e WHERE e.title LIKE %:keyword% OR e.venue LIKE %:keyword% OR e.category LIKE %:keyword%")
    List<Event> searchByTitleOrVenueOrCategory(@Param("keyword") String keyword);

}
