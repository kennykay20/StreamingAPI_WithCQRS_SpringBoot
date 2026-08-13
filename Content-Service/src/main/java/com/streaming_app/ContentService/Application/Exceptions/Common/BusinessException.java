package com.streaming_app.ContentService.Application.Exceptions.Common;

import org.springframework.http.HttpStatus;

public abstract class BusinessException extends RuntimeException {
    private final HttpStatus _status;

    protected BusinessException(String message, HttpStatus status)
    {
        super(message);
        this._status = status;
    }

    public HttpStatus getStatus() {
        return _status;
    }
}
