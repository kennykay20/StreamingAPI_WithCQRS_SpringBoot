package com.streaming_app.ContentService.Domain.Enums;
/*
 * Tracks the video processing lifecycle
 *
 * FLOW:
 * PENDING -> UPLOADED -> ENCODING -> ENCODED -> READY
 *                                            -> FAILED
 */
public enum VideoStatus {
    PENDING, // movie added but not uploaded yet
    UPLOADED, // raw movie uploaded to Blog storage
    ENCODING, // FFmpeg is encoding the video
    ENCODED,  // Encoding complete
    READY,    // HLS playlist ready - can be streamed
    REMOVED,  // delete or remove movie
    FAILED    // Encoding failed
}
