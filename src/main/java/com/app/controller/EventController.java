package com.app.controller;

import com.app.entity.Event;
import com.app.entity.EventCategory;
import com.app.repository.EventCategoryRepository;
import com.app.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private EventCategoryRepository eventCategoryRepository;

//    @GetMapping("/list")
//    public String listEvents(Model model) {
//        model.addAttribute( "events", eventService.getAllEvents());
//        return "admin/event_list";
//    }

    @GetMapping("/list")
    public String listEvents(@RequestParam(name = "categoryId", required = false) Long categoryId, Model model) {
        List<Event> events = (categoryId != null)
                ? eventService.getEventsByCategory(categoryId)
                : eventService.getAllEvents();
        model.addAttribute("categories", eventService.getAllCategories());
        model.addAttribute("events", events);
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("activePage", "gallery");
        return "admin/event_list";
    }

    // Show create form
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("event", new Event());
        model.addAttribute("categories", eventService.getAllCategories());
        return "admin/event_form";
    }

    // Save or update event
    @PostMapping("/save")
    public String saveOrUpdate(@ModelAttribute("event") Event event,
                               @RequestParam("categoryId") Long categoryId,
                               @RequestParam(value = "deletedImageIds", required = false) String deletedImageIds,
                               RedirectAttributes redirectAttributes) throws IOException {

        List<MultipartFile> images = event.getImageFiles();

        List<Long> idsToDelete = new ArrayList<>();
        if (deletedImageIds != null && !deletedImageIds.isEmpty()) {
            for (String idStr : deletedImageIds.split(",")) {
                idsToDelete.add(Long.parseLong(idStr));
            }
        }

        eventService.getCategoryById(categoryId).ifPresent(event::setCategory);

        if (event.getId() == null) {
            eventService.saveEvent(event, images);
            redirectAttributes.addFlashAttribute("successMessage", "Event created successfully.");
        } else {
            eventService.updateEvent(event.getId(), event, images, idsToDelete);
            redirectAttributes.addFlashAttribute("successMessage", "Event updated successfully.");
        }

        return "redirect:/admin/events/list";
    }


    // Show edit form
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Event> eventOpt = eventService.getEventById(id);
        if (eventOpt.isPresent()) {
            model.addAttribute("event", eventOpt.get());
            model.addAttribute("categories", eventService.getAllCategories());
            return "admin/event_form";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Event not found.");
            return "redirect:/admin/events/list";
        }
    }

    // View event details
    @GetMapping("/details/{id}")
    public String viewEventDetails(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Event> eventOpt = eventService.getEventById(id);
        if (eventOpt.isPresent()) {
            model.addAttribute("event", eventOpt.get());
            return "admin/event_details";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Event not found.");
            return "redirect:/admin/events/list";
        }
    }

    // Delete event
    @GetMapping("/delete/{id}")
    public String deleteEvent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            eventService.deleteEvent(id);
            redirectAttributes.addFlashAttribute("successMessage", "Event deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete event. Reason: " + e.getMessage());
        }
        return "redirect:/admin/events/list";
    }
}
