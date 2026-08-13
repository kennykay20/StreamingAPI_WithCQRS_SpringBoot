package com.streaming_app.VideoService.Application.Dtos.Responses;

import java.util.List;

public record ApiResponseDto<T>(
        Boolean success,
        String message,
        List<String> errors,
        T Data
) {
}
