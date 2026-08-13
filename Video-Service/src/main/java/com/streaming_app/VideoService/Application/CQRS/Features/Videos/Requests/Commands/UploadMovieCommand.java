package com.streaming_app.VideoService.Application.CQRS.Features.Videos.Requests.Commands;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public record UploadMovieCommand(
    UUID moviePublicId,
    MultipartFile file
) {
}
