package com.app.service.impl;

import com.app.entity.Event;
import com.app.entity.EventCategory;
import com.app.entity.EventImage;
import com.app.repository.EventRepository;
import com.app.repository.EventCategoryRepository;
import com.app.service.EventService;
import com.app.service.FileUploadService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventCategoryRepository categoryRepository;

    private final String baseUploadDir = "event-images";

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
        return eventRepository.findById(id);
    }

    @Override
    public Event saveEvent(Event event, List<MultipartFile> imageFiles) throws IOException {
        Event savedEvent = eventRepository.save(event); // save first to get ID
        Long eventId = savedEvent.getId();
        String eventUploadDir = baseUploadDir + "/" + eventId;

        if (imageFiles != null && !imageFiles.isEmpty()) {
            for (MultipartFile imageFile : imageFiles) {
                if (imageFile != null && !imageFile.isEmpty()) {
                    String fileName = FileUploadService.saveFile(eventUploadDir, imageFile.getOriginalFilename(), imageFile);
                    String imageUrl = "/" + eventUploadDir + "/" + fileName;

                    EventImage img = new EventImage();
                    img.setImageUrl(imageUrl);
                    img.setEvent(savedEvent);
                    savedEvent.getImages().add(img);
                }
            }
        }

        return eventRepository.save(savedEvent);
    }

    @Override
    public Event updateEvent(Long id, Event updatedEvent, List<MultipartFile> imageFiles, List<Long> deleteImageIds) throws IOException {
        Event existingEvent = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        existingEvent.setTitle(updatedEvent.getTitle());
        existingEvent.setDescription(updatedEvent.getDescription());
        existingEvent.setEventDate(updatedEvent.getEventDate());
        existingEvent.setCategory(updatedEvent.getCategory());

        // Delete selected existing images
        if (deleteImageIds != null && !deleteImageIds.isEmpty()) {
            existingEvent.getImages().removeIf(img -> deleteImageIds.contains(img.getId()));
        }

        // Upload new images
        if (imageFiles != null && !imageFiles.isEmpty()) {
            String uploadDir = baseUploadDir + "/" + id;
            for (MultipartFile imageFile : imageFiles) {
                if (imageFile != null && !imageFile.isEmpty()) {
                    String fileName = FileUploadService.saveFile(uploadDir, imageFile.getOriginalFilename(), imageFile);
                    String imageUrl = "/" + uploadDir + "/" + fileName;

                    EventImage img = new EventImage();
                    img.setImageUrl(imageUrl);
                    img.setEvent(existingEvent);
                    existingEvent.getImages().add(img);
                }
            }
        }

        return eventRepository.save(existingEvent);
    }


 /*   @Override
    public Event updateEvent(Long id, Event updatedEvent, List<MultipartFile> imageFiles) throws IOException {
        Event existingEvent = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        existingEvent.setTitle(updatedEvent.getTitle());
        existingEvent.setDescription(updatedEvent.getDescription());
        existingEvent.setEventDate(updatedEvent.getEventDate());
        existingEvent.setCategory(updatedEvent.getCategory());

        // Save new images
        if (imageFiles != null && !imageFiles.isEmpty()) {
            String uploadDir = baseUploadDir + "/" + id;

            for (MultipartFile imageFile : imageFiles) {
                if (imageFile != null && !imageFile.isEmpty()) {
                    String fileName = FileUploadService.saveFile(uploadDir, imageFile.getOriginalFilename(), imageFile);
                    String imageUrl = "/" + uploadDir + "/" + fileName;

                    EventImage img = new EventImage();
                    img.setImageUrl(imageUrl);
                    img.setEvent(existingEvent);
                    existingEvent.getImages().add(img);
                }
            }
        }

        return eventRepository.save(existingEvent);
    }
*/

    @Transactional
    @Override
    public void deleteEvent(Long id) {
        Optional<Event> eventOpt = eventRepository.findById(id);
        if (eventOpt.isPresent()) {
            Event event = eventOpt.get();

            // Remove child images to avoid FK conflict
            event.getImages().clear();

            // Flush changes before deletion
            eventRepository.save(event);

            // Now delete the parent
            eventRepository.delete(event);
        } else {
            throw new RuntimeException("Event not found with ID: " + id);
        }
    }

    @Override
    public Page<Event> findEventsByFilter(Long categoryId, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("eventDate").descending());

        if (categoryId != null && keyword != null && !keyword.isEmpty()) {
            return eventRepository.findByCategoryIdAndTitleContainingIgnoreCase(categoryId, keyword, pageable);
        } else if (categoryId != null) {
            return eventRepository.findByCategoryId(categoryId, pageable);
        } else if (keyword != null && !keyword.isEmpty()) {
            return eventRepository.findByTitleContainingIgnoreCase(keyword, pageable);
        } else {
            return eventRepository.findAll(pageable);
        }
    }


    @Override
    public List<EventCategory> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Optional<EventCategory> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

}
