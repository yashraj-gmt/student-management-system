package com.app.service;

import com.app.entity.Event;
import com.app.entity.EventCategory;
import jdk.jfr.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface EventService {

    List<Event> getAllEvents();

    List<Event> getEventsByCategory(Long categoryId);

    Optional<Event> getEventById(Long id);

    Event saveEvent(Event event, List<MultipartFile> imageFiles) throws IOException;

    Event updateEvent(Long id, Event updatedEvent, List<MultipartFile> imageFiles, List<Long> deleteImageIds) throws IOException;
    void deleteEvent(Long id);

    List<EventCategory> getAllCategories();


    Optional<EventCategory> getCategoryById(Long id);

    Page<Event> findEventsByFilter(Long categoryId, String keyword, int page, int size);


}
