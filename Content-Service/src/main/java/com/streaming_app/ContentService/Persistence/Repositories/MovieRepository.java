package com.streaming_app.ContentService.Persistence.Repositories;

import com.streaming_app.ContentService.Application.Dtos.Requests.GetGenreRequestDto;
import com.streaming_app.ContentService.Domain.Entities.Movie;
import com.streaming_app.ContentService.Domain.Enums.Genre;
import com.streaming_app.ContentService.Domain.Enums.VideoStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    List<Movie> findByGenre(Genre genre);
    List<Movie> findByTitleContainingIgnoreCase(String title);
    List<Movie> findByVideoStatus(VideoStatus videoStatus);
    Optional<Movie> findByPublicId(UUID publicId);
}
