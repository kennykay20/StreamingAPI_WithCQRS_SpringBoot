package com.streaming_app.ContentService.Application.Dtos.Requests;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMovieHlsUrlDto {

    @NotBlank(message = "PublicId is required")
    private UUID publicId;

    @NotBlank(message = "HlsUrl is required")
    private String hlsUrl;
}
