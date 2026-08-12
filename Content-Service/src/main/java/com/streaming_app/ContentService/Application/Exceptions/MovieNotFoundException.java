package com.streaming_app.ContentService.Application.Exceptions;

import com.streaming_app.ContentService.Application.Exceptions.Common.BusinessException;
import org.springframework.http.HttpStatus;

public class MovieNotFoundException extends BusinessException {
    public MovieNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
