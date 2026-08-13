package com.streaming_app.VideoService.API.Controllers.V1;

import com.streaming_app.VideoService.Application.CQRS.Features.Videos.Handlers.Commands.UploadVideoCommandHandler;
import com.streaming_app.VideoService.Application.CQRS.Features.Videos.Requests.Commands.UploadMovieCommand;
import com.streaming_app.VideoService.Application.Dtos.Responses.ApiResponseDto;
import com.streaming_app.VideoService.Application.Dtos.Responses.UploadVideoResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/videos")
@RequiredArgsConstructor
@Slf4j
public class VideoController {

    private final UploadVideoCommandHandler uploadVideoCommandHandler;

    /*
    * Upload video file for a movie
    * Accepts multipart file upload
    *
    * POST /api/v1/videos/upload/{moviePublicId}
    */

    @PostMapping("/upload/{moviePublicId}")
    public ResponseEntity<ApiResponseDto<UploadVideoResponseDto>> uploadVideo(
            @PathVariable UUID moviePublicId,
            @RequestParam("file") MultipartFile file)
    {
        log.info("Video upload request for moviePublicId={}", moviePublicId);

        var command = new UploadMovieCommand(moviePublicId, file);

        var result = uploadVideoCommandHandler.handle(command);
        return ResponseEntity.ok().body(
                new ApiResponseDto<>(
                        true,
                        "Video uploaded successfully",
                        null,
                        result
                )
        );
    }
}
