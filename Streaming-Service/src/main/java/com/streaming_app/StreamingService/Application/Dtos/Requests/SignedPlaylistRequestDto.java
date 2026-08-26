package com.streaming_app.StreamingService.Application.Dtos.Requests;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignedPlaylistRequestDto {

    @NotBlank(message = "MoviePublicId is required.")
    private UUID moviePublicId;

    @NotBlank(message = "Path is required.")
    private String path;
}
