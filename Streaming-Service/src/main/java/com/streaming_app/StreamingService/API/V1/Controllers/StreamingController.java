package com.streaming_app.StreamingService.API.V1.Controllers;

import com.streaming_app.StreamingService.Application.CQRS.Features.Streams.Handlers.Commands.VideoStreamingCommandHandler;
import com.streaming_app.StreamingService.Application.CQRS.Features.Streams.Handlers.Queries.GetSignedPlaylistQueryHandler;
import com.streaming_app.StreamingService.Application.CQRS.Features.Streams.Handlers.Queries.GetStreamingURLQueryHandler;
import com.streaming_app.StreamingService.Application.CQRS.Features.Streams.Requests.Queries.GetSignedPlaylistQuery;
import com.streaming_app.StreamingService.Application.CQRS.Features.Streams.Requests.Queries.GetStreamingURLQuery;
import com.streaming_app.StreamingService.Application.Dtos.Requests.SignedPlaylistRequestDto;
import com.streaming_app.StreamingService.Application.Dtos.Requests.StreamingUrlRequestDto;
import com.streaming_app.StreamingService.Application.Dtos.Responses.ApiResponseDto;
import com.streaming_app.StreamingService.Application.Dtos.Responses.StreamingUrlResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/streams")
@RequiredArgsConstructor
@Slf4j
public class StreamingController {

    private final GetStreamingURLQueryHandler getStreamingURLQueryHandler;
    private final GetSignedPlaylistQueryHandler getSignedPlaylistQueryHandler;

    @GetMapping("/{moviePublicId}/master")
    public ResponseEntity<
            ApiResponseDto<StreamingUrlResponseDto>
            > getStreamingUrl(
                @PathVariable UUID moviePublicId
            )
    {

        log.info(
                "Get streaming request for movieId: {}",
                moviePublicId
        );

        var query = new GetStreamingURLQuery(new StreamingUrlRequestDto(moviePublicId));

        var result = getStreamingURLQueryHandler.handle(query);
        return ResponseEntity.ok().body(
                new ApiResponseDto<>(
                        true,
                        "Streaming URL retrieved successfully",
                        null,
                        result
                )
        );
    }

    /**
     * Server signed m3u8 playlist content
     * Called by HLS player for each quality playlist
     * @param moviePublicId
     * @param path
     * @return
     */

    @GetMapping("/{moviePublicId}/playlist")

    public ResponseEntity<ApiResponseDto<String>> getSignedPlaylist(
            @PathVariable UUID moviePublicId,
            @RequestParam String path
    ) {

        var query = new GetSignedPlaylistQuery(new SignedPlaylistRequestDto(moviePublicId, path));

        var result = getSignedPlaylistQueryHandler.handle(query);
        return ResponseEntity.ok(
                new ApiResponseDto<>(
                        true,
                        "",
                        null,
                        result
                )
        );
    }

}
