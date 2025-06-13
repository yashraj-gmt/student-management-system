package com.app.repository;

import com.app.entity.Event;
import com.app.entity.EventCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByCategory(EventCategory category);

    Page<Event> findByCategoryId(Long categoryId, Pageable pageable);

    Page<Event> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);

    Page<Event> findByCategoryIdAndTitleContainingIgnoreCase(Long categoryId, String keyword, Pageable pageable);


}
