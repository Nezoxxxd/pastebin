package com.project.pastebin.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@Table(name = "posts")
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String postTitle;
    private String postText;
    private LocalDateTime createdAt;
    private LocalDateTime expiryTime;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // метод выполняется перед тем, как сущность будет сохранена в бд (для установки времени создания поста)
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public boolean isExpired() {
        return expiryTime != null && expiryTime.isAfter(LocalDateTime.now());
    }
}
