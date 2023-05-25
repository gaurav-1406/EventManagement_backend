package com.example.demo.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Event;
import com.example.demo.model.Participant;
import com.example.demo.repository.EventRepository;
import com.example.demo.repository.ParticipantRepository;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1")
public class EventController {

    @Autowired
    private EventRepository eventRepository;
    


    // Create an Event
    @PostMapping("/events")
    public Event createEvent(@RequestBody Event event) {
        return eventRepository.save(event);
    }

    // Retrieve all event details
    @GetMapping("/events")
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    // Retrieve all event details by location
    @GetMapping("/events/location/{location}")
    public List<Event> getEventsByLocation(@PathVariable String location) {
        return eventRepository.findByLocation(location);
    }

     //Retrieve details of one event with all details
    @GetMapping("/events/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));
        List<Participant> participants = event.getParticipants();
        event.setParticipants(participants);
        return ResponseEntity.ok(event);
    }
    



    // Update an event
    @PutMapping("/events/{id}")
    public ResponseEntity<Event> updateEvent(@PathVariable Long id, @RequestBody Event eventDetails) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));

        event.setName(eventDetails.getName());
        event.setDescription(eventDetails.getDescription());
        event.setEmailId(eventDetails.getEmailId());
        event.setStartdate(eventDetails.getStartdate());
        event.setEnddate(eventDetails.getEnddate());
        event.setLocation(eventDetails.getLocation());

        Event updatedEvent = eventRepository.save(event);
        return ResponseEntity.ok(updatedEvent);
    }

    // Delete an event
    @DeleteMapping("/events/{id}")
    public ResponseEntity<Map<String, Boolean>> deleteEvent(@PathVariable Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));

        eventRepository.delete(event);
        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        return ResponseEntity.ok(response);
    }
    
    @Autowired
    private ParticipantRepository participantRepository;
    
    // Retrieve all participants of an event
    @GetMapping("/events/{id}/participants")
    public ResponseEntity<List<Participant>> getEventParticipants(@PathVariable Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));
        List<Participant> participants = event.getParticipants();
        return ResponseEntity.ok(participants);
    }

    // Register a participant for an event
    @PostMapping("/events/{id}/participants")
    public ResponseEntity<Participant> registerParticipant(@PathVariable Long id, @RequestBody Participant participant) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));
        participant.setEvent(event);
        Participant savedParticipant = participantRepository.save(participant);
        return ResponseEntity.ok(savedParticipant);
    }
    
    @PostMapping("/{eventId}/register")
    public ResponseEntity<?> registerParticipant(@PathVariable("eventId") long eventId, @RequestBody Participant participant) {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isPresent()) {
            Event event = optionalEvent.get();
            event.registerParticipant(participant);
            eventRepository.save(event);
            return ResponseEntity.ok("Participant registered successfully.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
