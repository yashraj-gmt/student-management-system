/*
package com.app.controller;

import com.app.entity.Event;
import com.app.service.EventService;
import com.app.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @GetMapping("/list")
    public String listEvents(Model model) {
        model.addAttribute( "events", eventService.getAllEvents());
        return "list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("event", new Event());
        model.addAttribute("categories", eventService.getAllCategories());
        return "form";
    }

    @PostMapping("/save")
    public String saveEvent(@ModelAttribute Event event,
                            @RequestParam("image") MultipartFile multipartFile) {
        try {
            if (!multipartFile.isEmpty()) {
                String fileName = FileUploadService.saveFile("event-images", multipartFile.getOriginalFilename(), multipartFile);
                event.setImageUrl("/event-images/" + fileName);
            }
            eventService.saveEvent(event, multipartFile);
            return "redirect:list";
        } catch (IOException e) {
            e.printStackTrace();
            return "error";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<Event> eventOpt = eventService.getEventById(id);
        if (eventOpt.isPresent()) {
            model.addAttribute("event", eventOpt.get());
            model.addAttribute("categories", eventService.getAllCategories());
            return "form";
        } else {
            return "redirect:list";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return "redirect:list";
    }

    @GetMapping("/details/{id}")
    public String viewEventDetails(@PathVariable Long id, Model model) {
        Optional<Event> eventOpt = eventService.getEventById(id);
        if (eventOpt.isPresent()) {
            model.addAttribute("event", eventOpt.get());
            return "details"; // Make sure templates/events/details.html exists
        } else {
            return "redirect:list";
        }
    }
}
*/
