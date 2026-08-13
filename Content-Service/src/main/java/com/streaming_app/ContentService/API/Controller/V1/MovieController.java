package com.streaming_app.ContentService.API.Controller.V1;

import com.streaming_app.ContentService.Application.CQRS.Features.Movie.Handlers.Commands.CreateMovieCommandHandler;
import com.streaming_app.ContentService.Application.CQRS.Features.Movie.Handlers.Queries.*;
import com.streaming_app.ContentService.Application.CQRS.Features.Movie.Requests.Commands.CreateMovieCommand;
import com.streaming_app.ContentService.Application.CQRS.Features.Movie.Requests.Queries.*;
import com.streaming_app.ContentService.Application.Dtos.Requests.CreateMovieRequestDto;
import com.streaming_app.ContentService.Application.Dtos.Responses.ApiResponseDto;
import com.streaming_app.ContentService.Application.Dtos.Responses.MovieResponseDto;
import com.streaming_app.ContentService.Domain.Enums.Genre;
import com.streaming_app.ContentService.Domain.Enums.VideoStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/movies")
@RequiredArgsConstructor
@Slf4j
public class MovieController {

    private final CreateMovieCommandHandler createMovieCommandHandler;
    private final GetMoviesQueryHandler getMoviesQueryHandler;
    private final GetMoviesByGenreQueryHandler getMoviesByGenreQueryHandler;
    private final GetMovieByPublicIdQueryHandler getMovieByPublicIdQueryHandler;
    private final GetMoviesByVideoStatusQueryHandler getGetMoviesByVideoStatusQueryHandler;
    private final GetMoviesBySearchQueryHandler getMoviesBySearchQueryHandler;

    @PostMapping
    public ResponseEntity<ApiResponseDto<MovieResponseDto>> addMovie(
            @Valid @RequestBody CreateMovieRequestDto requestDto) {

        var command = new CreateMovieCommand(requestDto);

        var result = createMovieCommandHandler.handle(command);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponseDto<>(
                        true,
                        "Movie added successfully",
                        null,
                        result,
                        result.getPublicId().toString()
                )
        );
    }

    // get all movies
    @GetMapping
    public ResponseEntity<ApiResponseDto<List<MovieResponseDto>>> getAllMovies()
    {
        var query = new GetMoviesQuery();

        var result = getMoviesQueryHandler.handle(query);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponseDto<>(
                        true,
                        "Retrieved Movies successfully",
                        null,
                        result,
                        null
                )
        );
    }

    // get movies by genre
    @GetMapping("/genre/{genre}")
    public ResponseEntity<ApiResponseDto<List<MovieResponseDto>>> getMoviesByGenre(
            @PathVariable Genre genre)
    {
        var query = new GetMoviesByGenreQuery(genre);

        var result = getMoviesByGenreQueryHandler.handle(query);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponseDto<>(
                        true,
                        "Retrieved Movies successfully",
                        null,
                        result,
                        null
                )
        );
    }

    // get all movies by video status -
    @GetMapping("/video/{status}")
    public ResponseEntity<ApiResponseDto<List<MovieResponseDto>>> getMoviesByVideoStatus(
            @PathVariable VideoStatus status)
    {
        var query = new GetMoviesByVideoStatusQuery(status);

        var result = getGetMoviesByVideoStatusQueryHandler.handle(query);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponseDto<>(
                        true,
                        "Retrieved Movies successfully",
                        null,
                        result,
                        null
                )
        );
    }

    // get movie by publicId
    @GetMapping("/{publicId}")
    public ResponseEntity<ApiResponseDto<MovieResponseDto>> getMovieByPublicId(
            @PathVariable UUID publicId)
    {
        var query = new GetMovieByPublicIdQuery(publicId);

        var result = getMovieByPublicIdQueryHandler.handle(query);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponseDto<>(
                        true,
                        "Retrieved Movies successfully",
                        null,
                        result,
                        publicId.toString()
                )
        );
    }

    // get all movies by search with title
    @GetMapping("/search")
    public ResponseEntity<ApiResponseDto<List<MovieResponseDto>>> searchMovies(
            @RequestParam String title)
    {
        var query = new GetMoviesBySearchQuery(title);

        var result = getMoviesBySearchQueryHandler.handle(query);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponseDto<>(
                        true,
                        "Retrieved Movies successfully",
                        null,
                        result,
                        null
                )
        );
    }
}
