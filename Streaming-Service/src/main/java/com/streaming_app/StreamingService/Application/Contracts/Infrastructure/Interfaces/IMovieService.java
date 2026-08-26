package com.streaming_app.StreamingService.Application.Contracts.Infrastructure.Interfaces;

public interface IMovieService {
    String GeneratePresignedUrl(String blobKey, long presignedUrlExpiry);
}
