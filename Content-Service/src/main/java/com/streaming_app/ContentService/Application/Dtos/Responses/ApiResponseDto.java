package com.streaming_app.ContentService.Application.Dtos.Responses;

import java.util.List;

public record ApiResponseDto<T> (
        Boolean success,
        String message,
        List<String> errors,
        T data,
        String publicId
){}

