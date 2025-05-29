/*
package com.app.service.impl;

import com.app.entity.Event;
import com.app.entity.EventCategory;
import com.app.repository.EventRepository;
import com.app.repository.EventCategoryRepository;
import com.app.service.EventService;
import com.app.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventCategoryRepository categoryRepository;

    private final String uploadDir = "event-images";

    @Autowired
    public EventServiceImpl(EventRepository eventRepository, EventCategoryRepository categoryRepository) {
        this.eventRepository = eventRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @Override
    public List<Event> getEventsByCategory(Long categoryId) {
        Optional<EventCategory> category = categoryRepository.findById(categoryId);
        return category.map(eventRepository::findByCategory).orElse(List.of());
    }

    @Override
    public Optional<Event> getEventById(Long id) {
        return Optional.ofNullable(eventRepository.findById(id).orElse(null));
    }

    @Override
    public Event saveEvent(Event event, MultipartFile imageFile) throws IOException {
        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = FileUploadService.saveFile(uploadDir, imageFile.getOriginalFilename(), imageFile);
            event.setImageUrl("/" + uploadDir + "/" + fileName);
        }
        return eventRepository.save(event);
    }

    @Override
    public Event updateEvent(Long id, Event updatedEvent, MultipartFile imageFile) throws IOException {
        Event existingEvent = eventRepository.findById(id).orElseThrow(() -> new RuntimeException("Event not found"));

        existingEvent.setTitle(updatedEvent.getTitle());
        existingEvent.setDescription(updatedEvent.getDescription());
        existingEvent.setEventDate(updatedEvent.getEventDate());
        existingEvent.setCategory(updatedEvent.getCategory());

        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = FileUploadService.saveFile(uploadDir, imageFile.getOriginalFilename(), imageFile);
            existingEvent.setImageUrl("/" + uploadDir + "/" + fileName);
        }

        return eventRepository.save(existingEvent);
    }

    @Override
    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    @Override
    public List<EventCategory> getAllCategories() {
        return categoryRepository.findAll();
    }
}
*/
