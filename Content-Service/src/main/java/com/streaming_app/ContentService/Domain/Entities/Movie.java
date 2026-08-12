package com.streaming_app.ContentService.Domain.Entities;

import com.streaming_app.ContentService.Domain.Enums.Genre;
import com.streaming_app.ContentService.Domain.Enums.VideoStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table( name = "Movies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID publicId = UUID.randomUUID();

    @Column(nullable = false)
    private String title;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    private Genre genre;

    private String director;
    private String cast;
    private int releaseYear;

    private double rating;
    private String thumbnailUrl;
    private int durationMinutes;

    //Storage/Identity Key for the video file
    private String videoKey;

    // HLS master playlist URL for streaming
    private String hlsUrl;

    @Enumerated(EnumType.STRING)
    // status of video processing
    private VideoStatus videoStatus;

    @Column
    private Boolean isDeleted;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
