package com.streaming_app.ContentService.Application.Dtos.Responses;

import com.streaming_app.ContentService.Domain.Enums.Genre;
import com.streaming_app.ContentService.Domain.Enums.VideoStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class MovieResponseDto {

    private UUID publicId;
    private String title;
    private String description;
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

    private Boolean isDeleted;

    // status of video processing
    private VideoStatus videoStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
