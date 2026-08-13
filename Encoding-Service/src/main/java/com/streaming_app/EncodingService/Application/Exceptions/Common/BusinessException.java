package com.streaming_app.EncodingService.Application.Exceptions.Common;

import org.springframework.http.HttpStatus;

public abstract class BusinessException extends RuntimeException{
    private final HttpStatus _status;

    public BusinessException(String message, HttpStatus status)
    {
        super(message);
        _status = status;
    }

    public HttpStatus getStatus()
    {
        return _status;
    }
}
