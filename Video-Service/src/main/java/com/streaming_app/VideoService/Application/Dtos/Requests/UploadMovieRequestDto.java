package com.streaming_app.VideoService.Application.Dtos.Requests;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadMovieRequestDto {

    @NotBlank(message = "MoviePublicId is required")
    private UUID moviePublicId;

    @NotBlank(message = "File is required")
    private MultipartFile file;
}
