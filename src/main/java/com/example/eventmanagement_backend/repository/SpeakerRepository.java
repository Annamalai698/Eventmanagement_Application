package com.example.eventmanagement_backend.repository;

import com.example.eventmanagement_backend.entity.Speaker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpeakerRepository extends JpaRepository<Speaker, Long> {

    List<Speaker> findByEventId(Long eventId);

    @Query("SELECT s FROM Speaker s JOIN FETCH s.event")
    List<Speaker> findAllWithEvent();
}
