package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "news")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class NewsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Column(columnDefinition = "LONGTEXT")
    private String title;
    @NotNull
    @Column(columnDefinition = "LONGTEXT")
    private String description;
    private String imageUrl;
    private String imageStoragePath;
    @Column(columnDefinition = "LONGTEXT")
    private String content;
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
    CategoryEntity category;
    @ManyToOne
    User user;
}
