package com.example.eventmanagement_backend.service;

import com.example.eventmanagement_backend.entity.Event;
import com.example.eventmanagement_backend.entity.Speaker;
import com.example.eventmanagement_backend.repository.EventRepository;
import com.example.eventmanagement_backend.repository.SpeakerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpeakerService {

    @Autowired
    private SpeakerRepository speakerRepository;

    @Autowired
    private EventRepository eventRepository;

    public Speaker createSpeaker(Long eventId, Speaker speaker) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with ID: " + eventId));

        speaker.setEvent(event);
        return speakerRepository.save(speaker);
    }

    public Speaker addSpeaker(Long eventId, Speaker speaker) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        speaker.setEvent(event);
        return speakerRepository.save(speaker);
    }

    public List<Speaker> getSpeakersByEvent(Long eventId) {
        return speakerRepository.findByEventId(eventId);
    }

    public List<Speaker> getAllSpeakers() {
        return speakerRepository.findAllWithEvent();
    }

    public Speaker updateSpeaker(Long speakerId, Speaker speakerDetails) {
        Speaker speaker = speakerRepository.findById(speakerId)
                .orElseThrow(() -> new RuntimeException("Speaker not found with ID: " + speakerId));

        speaker.setName(speakerDetails.getName());
        speaker.setBio(speakerDetails.getBio());
        // You can update other fields here

        return speakerRepository.save(speaker);
    }

    public void deleteSpeaker(Long speakerId) {
        speakerRepository.deleteById(speakerId);
    }
}

