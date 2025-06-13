package com.app.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 1000)
    private String description;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate eventDate;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<EventImage> images = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "category_id")
    private EventCategory category;

    // ✅ Used only for form upload (not persisted in DB)
    @Transient
    private List<MultipartFile> imageFiles;
}
