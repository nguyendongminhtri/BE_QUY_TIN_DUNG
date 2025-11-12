package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "carousel")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CarouselEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    @Column(columnDefinition = "LONGTEXT")
    private String content;
    private String imageUrl;
    private String imageStoragePath;
    @Column(name = "content_storage_paths", columnDefinition = "TEXT")
    private String contentStoragePathsJson;
    private Boolean isShow = true;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    @ManyToOne
    User user;
}
