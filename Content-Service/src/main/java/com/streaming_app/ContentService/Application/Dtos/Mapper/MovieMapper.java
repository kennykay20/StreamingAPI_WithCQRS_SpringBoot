package com.streaming_app.ContentService.Application.Dtos.Mapper;

import com.streaming_app.ContentService.Application.Dtos.Requests.CreateMovieRequestDto;
import com.streaming_app.ContentService.Application.Dtos.Responses.MovieResponseDto;
import com.streaming_app.ContentService.Domain.Entities.Movie;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MovieMapper {

    MovieResponseDto toDto(Movie movie);

    List<MovieResponseDto> toListDto(List<Movie> movies);
    Movie toEntity(CreateMovieRequestDto request);
}
