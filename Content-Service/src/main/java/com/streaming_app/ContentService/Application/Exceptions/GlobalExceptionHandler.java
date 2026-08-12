package com.streaming_app.ContentService.Application.Exceptions;

import com.streaming_app.ContentService.Application.Dtos.Responses.ApiResponseDto;
import com.streaming_app.ContentService.Application.Exceptions.Common.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDto<Void>> handleException(Exception ex)
    {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        new ApiResponseDto<>(
                                false,
                                "An unexception error occur" + ex.getMessage(),
                                null,
                                null,
                                null
                        )
                );
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponseDto<String>> handleBusinessException(BadRequestException ex, WebRequest request)
    {
        return ResponseEntity.status(ex.getStatus()).body(
                new ApiResponseDto<>(
                        false,
                        ex.getMessage(),
                        List.of(ex.getMessage()),
                        null,
                        null
                )
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDto<String>> handleValidationException(MethodArgumentNotValidException ex) {

        List<String> errorList = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());



        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ApiResponseDto<>(
                        false,
                        "Validation failed",
                        errorList,
                        null,
                        null
                )
        );
    }
}
