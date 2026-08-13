package com.streaming_app.VideoService.Application.Exceptions.Common;

import org.springframework.http.HttpStatus;

public class BusinessException extends RuntimeException {
    private final HttpStatus _status;

    protected BusinessException(String message, HttpStatus status){
        super(message);
        _status = status;
    }

    public HttpStatus getStatus() {
        return _status;
    }
}
