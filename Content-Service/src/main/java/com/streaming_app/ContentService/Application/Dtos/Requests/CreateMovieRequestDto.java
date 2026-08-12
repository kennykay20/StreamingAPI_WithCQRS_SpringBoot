package com.streaming_app.ContentService.Application.Dtos.Requests;

import com.streaming_app.ContentService.Domain.Enums.Genre;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class CreateMovieRequestDto {

    @NotBlank(message = "Title is required")
    private String title;

    @Size()
    private String description;

    @NotNull(message = "Genre is required")
    private Genre genre;

    private String director;
    private String cast;
    private int releaseYear;

    private double rating;
    private String thumbnailUrl;
    private int durationMinutes;
}
