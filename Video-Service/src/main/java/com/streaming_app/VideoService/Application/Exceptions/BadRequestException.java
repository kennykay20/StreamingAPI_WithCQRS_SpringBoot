package com.streaming_app.VideoService.Application.Exceptions;

import com.streaming_app.VideoService.Application.Exceptions.Common.BusinessException;
import org.springframework.http.HttpStatus;

public class BadRequestException extends BusinessException {
    public BadRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
