# Streaming Platform

A scalable video streaming platform built with **Java Spring Boot** 
and a **microservices architecture**, designed to handle video upload, 
encoding, content management, and adaptive streaming.

## Architecture

The platform is composed of several independent services:

* **API Gateway** – Central entry point for client requests.
* **Content Service** – Manages movies, titles, genres, descriptions, thumbnails, and streaming metadata.
* **Video Service** – Handles raw video uploads and cloud storage.
* **Encoding Service** – Converts uploaded videos into multiple resolutions and HLS formats for adaptive streaming.
* **Streaming Service** – Provides the processed video streams to clients.

## Technologies

* Java & Spring Boot
* Spring Data JPA
* PostgreSQL / MySQL
* Apache Kafka
* Redis
* Docker & Docker Compose
* Cloud Blob Storage
* HLS (HTTP Live Streaming)
* Maven

## Video Processing Flow

```text
Client
  ↓
API Gateway
  ↓
Content Service
  ↓
Video Service
  ↓
Cloud Storage
  ↓
Kafka
  ↓
Encoding Service
  ↓
HLS Encoding
  ↓
Cloud Storage
  ↓
Content Service
  ↓
Streaming Service
  ↓
Client
```

The system uses **Kafka for asynchronous communication** 
between the Video and Encoding services, allowing video processing to happen 
independently without blocking the upload process.

## Goal

The project demonstrates how to build a production-oriented 
**Netflix-style video streaming backend** using microservices, 
event-driven communication, cloud storage, asynchronous video processing, 
and adaptive HLS streaming.
