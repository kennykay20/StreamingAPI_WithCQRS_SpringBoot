package com.streaming_app.VideoService.Application.Exceptions;

import com.streaming_app.VideoService.Application.Dtos.Responses.ApiResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDto<Void>> handleException(Exception ex)
    {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                    new ApiResponseDto<>(
                            false,
                            "An unexceptional error occur " + ex.getMessage(),
                            null,
                            null
                    )
                );
    }
}
