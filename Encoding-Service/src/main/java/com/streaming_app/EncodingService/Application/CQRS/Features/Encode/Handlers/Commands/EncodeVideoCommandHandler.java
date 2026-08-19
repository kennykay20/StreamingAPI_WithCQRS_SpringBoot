package com.streaming_app.EncodingService.Application.CQRS.Features.Encode.Handlers.Commands;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.models.BlobHttpHeaders;
import com.streaming_app.EncodingService.Application.CQRS.Features.Encode.Requests.Commands.EncodeVideoCommand;
import com.streaming_app.EncodingService.Application.Events.VideoEncodedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Service
//@RequiredArgsConstructor
@Slf4j
public class EncodeVideoCommandHandler {

    private final KafkaTemplate<String, VideoEncodedEvent> kafkaTemplate;

    @Qualifier("rawBlobContainerClient")
    private final BlobContainerClient rawBlobContainerClient;

    @Qualifier("encodedBlobContainerClient")
    private final BlobContainerClient encodedBlobContainerClient;

    @Value("${ffmpeg.path}")
    private String ffmpegPath;

    @Value("${encoding.base-path}")
    private String basePath;

//    @Value("${azure.storage.endpoint}")
//    private String blobEndpoint;

    private static final String VIDEO_ENCODED_TOPIC = "video.encoded";

    // Video qualities to encode
    // Format: resolution, bitrate, height

    private static final List<int[]> VIDEO_QUALITIES = Arrays.asList(
            new int[]{1920, 5000, 1080},   // 1080px - 5000 bitrate
            new int[]{1280, 2800, 720},    // 720px - 2800 bitrate
            new int[]{854, 1200, 480},      // 480px - 1200 bitrate
            new int[]{640, 800, 360}       // 360px - 800 bitrate
    );

    public EncodeVideoCommandHandler(
            KafkaTemplate<String, VideoEncodedEvent> kafkaTemplate,
            @Qualifier("rawBlobContainerClient")
            BlobContainerClient rawBlobContainerClient,
            @Qualifier("encodedBlobContainerClient")
            BlobContainerClient encodedBlobContainerClient
    ){
        this.kafkaTemplate = kafkaTemplate;
        this.rawBlobContainerClient = rawBlobContainerClient;
        this.encodedBlobContainerClient = encodedBlobContainerClient;
    }

    /*
     * Main encoding pipeline
     *
     * Steps
     * 1. Download raw video from Blob-Container/storage
     * 2. Encode to multiple qualities using FFmpeg
     * 3. Generate HLS playlist (.m3u8) for each quality
     * 4. Create master playlist
     * 5. Upload all encoded files back to Blob-Storage
     * 6. Publish VideoEncodedEvent to kafka
     * @param request
     */
    public void handle(EncodeVideoCommand request) {
        log.info(
                "Starting encoding platform for movie: {}",
                request.eventRequest().moviePublicId()
        );

        var moviePublicId = request.eventRequest().moviePublicId();
        var videoKey = request.eventRequest().videoKey();

        /* Create a unique path for a movie */
        String jobPath =
                basePath + "/" + moviePublicId;

        try
        {
            // Create temp directories
            Files.createDirectories(
                    Paths.get(jobPath)
            );
            Files.createDirectories(
                    Paths.get(jobPath + "/encoded")
            );

            // Step 1: Download raw video from Blob-Storage
            String localVideoPath =
                    Paths.get(
                            jobPath,
                            "/raw_video.mp4"
                    ).toString();
            downloadFromBlob(
                    videoKey,
                    localVideoPath
            );
            log.info("Raw video downloaded to: {} ", localVideoPath);

            // Step 2 & 3: Encode to multiple qualities + generate HLS
            for (int[] qualities : VIDEO_QUALITIES) {
                int width = qualities[0];
                int bitrate = qualities[1];
                int height = qualities[2];

                String qualityDir =
                        Paths.get(jobPath,
                                "encoded",
                                height + "p"
                        ).toString();
                Files.createDirectories(
                        Paths.get(qualityDir)
                );

                encodeToHLS(
                        localVideoPath,
                        qualityDir,
                        width,
                        height,
                        bitrate
                );
                log.info(
                        "Encoded {}p successfully",
                        height
                );
            }

            // Step 4: Generate master playlist
            String masterPlaylistPath =
                    Paths.get(
                            jobPath,
                            "encoded",
                            "master.m3u8"
                    ).toString();

            generateMasterPlaylist(masterPlaylistPath);
            log.info("Master Playlist generated");

            // Step 5: Upload all resources file to Blob-storage(Azure)
            String encodedPrefix =
                    "encoded/" + moviePublicId + "/";
            uploadEncodedFilesToBlob(
                    Paths.get(
                            jobPath,
                            "/encoded"
                    ).toString(),
                    encodedPrefix
            );

            // Step 6: Publish encoded videos to kafka
            String masterPlaylistKey =
                    encodedPrefix + "master.m3u8";

            String hlsUrl =
                    encodedBlobContainerClient.getBlobContainerUrl()
                           + "/" + masterPlaylistKey;


            log.info("before we send an event, hlsUrl - {}", hlsUrl);
            var event =
                    new VideoEncodedEvent(
                            moviePublicId,
                            hlsUrl,
                            masterPlaylistKey,
                    true,
                    null
                    );

            kafkaTemplate.send(
                    VIDEO_ENCODED_TOPIC,
                    moviePublicId.toString(),
                    event
            );

            log.info(
                    "VideoEncodedEvent published for movieId: {}",
                    moviePublicId
            );

        } catch (Exception e) {
            log.error(
                    "Encoding video failed for movieId - {}",
                    moviePublicId,
                    e
            );

            var failureEvent = new VideoEncodedEvent(
                    moviePublicId,
                    null,
                    null,
                    false,
                    "FFmpeg encoding failed..."
            );

            kafkaTemplate.send(
                    VIDEO_ENCODED_TOPIC,
                    moviePublicId.toString(),
                    failureEvent
            );
            throw new RuntimeException(
                    "Failed to encode",
                    e
            );
        }
        finally {
            cleanUpTempFiles(jobPath);
        }
    }

    // download file from Blob
    private void downloadFromBlob(String videoKey, String videoPath) {
        log.info(
                "Downloading raw video from Azure Blob Storage: {}",
                videoKey
        );

        var blobClient =
                rawBlobContainerClient.getBlobClient(videoKey);

        if (!blobClient.exists()) {
            throw new RuntimeException(
                    "Video blob does not exist: " + videoKey
            );
        }

        try {

            blobClient.downloadToFile(
                    videoPath,
                    true
            );

            log.info(
                    "Raw video downloaded successfully to: {}",
                    videoPath
            );

        } catch (Exception ex) {
            log.error(
                    "Failed to download video from Blob Storage: {}",
                    videoKey,
                    ex
            );

            throw new RuntimeException(
                    "Failed to download video from Blob Storage",
                    ex
            );
        }
    }

    private void encodeToHLS(String inputPath, String outputDirectory, int width, int height, int bitrate) {

        log.info(
                "Starting FFmpeg encoding: {}x{} @ {} kbps",
                width,
                height,
                bitrate
        );

        String playlistPath = outputDirectory + "/playlist.m3u8";
        String segmentPattern = outputDirectory + "/segment_%03d.ts";

        try
        {
            // FFmpeg command for HLS encoding
            List<String> command = Arrays.asList(
                    ffmpegPath,
                    "-i", inputPath,                             // Input file
                    "-vf", "scale=" + width + ":" + height,      // scale to resolution
                    "-c:v", "libx264",                           // video codec
                    "-b:v", bitrate + "k",                       // video bitrate
                    "-c:a", "aac",                               // audio coded
                    "-b:a", "128k",                              // audio bitrate
                    "-hls_time", "10",                           // 10 second segments
                    "-hls_list_size", "0",                       // keep all segments
                    "-hls_segment_filename", segmentPattern,     // segment naming
                    "-f", "hls",                                 // output format HLS
                    playlistPath                                 // output playlist
            );

            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.inheritIO();
            Process process = processBuilder.start();

//            try (BufferedReader reader = new BufferedReader(
//                    new InputStreamReader(process.getInputStream())
//            )) {
//                String line;
//
//                while ((line = reader.readLine()) != null) {
//                    log.info("FFmpeg: {}", line);
//                }
//            }

            int exitCode = process.waitFor();
            if(exitCode != 0) {
                throw new RuntimeException(
                        "FFmpeg encoding failed with exit code " +
                        exitCode
                );
            }
            log.info(
                    "FFmpeg process completed for {}p",
                    height
            );
        } catch (Exception e) {
            throw new RuntimeException(
                    "Interrupted exception",
                    e
            );
        }
    }

    /* generate master HLS playlist
     *
     */
    private void generateMasterPlaylist(String masterPlaylistPath){
        StringBuilder master = new StringBuilder();
        master.append("#EXTM3U\n");
        master.append("EXT-X-VERSION:3\n\n");

       try
       {
           // Add each quality to master playlist
           int[][] qualities = {{1920, 5000, 1080},{1280, 2800, 720},
                                {854, 1200, 480}, {640, 800, 360}};

           for (int [] q : qualities){
               int width = q[0];
               int bitrate = q[1];
               int height = q[2];

               master.append("#EXT-X-STREAM-INF:BANDWIDTH=")
                       .append(bitrate*1000)
                       .append(", RESOLUTION=").append(width).append("x").append(height)
                       .append(",CODECS=\"avc1.42e01e.mp4a.40.2\"\n");
               master.append(height).append("p/playlist.m3u8\n\n");
           }

           Files.writeString(Paths.get(masterPlaylistPath), master.toString());
       } catch (Exception e) {
           throw new RuntimeException(
                   "", e
           );
       }
    }


    // Upload all encoded files from local directory back to blob
    private void uploadEncodedFilesToBlob(String localDir, String blobPrefix){
        File directory = new File(localDir);

        if(!directory.exists() || !directory.isDirectory()) {
            throw new RuntimeException(
                "Encoded directory does not exist: " + localDir
            );
        }

        uploadDirectoryToBlob(directory, localDir, blobPrefix);
    }

    private void uploadDirectoryToBlob(File dir, String baseDir, String blobPrefix) {

        File[] files = dir.listFiles();

        if (files == null){
            return;
        }

        for (File file : files) {

            if(file.isDirectory()) {
                uploadDirectoryToBlob(
                        file,
                        baseDir,
                        blobPrefix
                );
            }
            else {
                String relativePath = file.getAbsolutePath()
                        .substring(baseDir.length() + 1)
                        .replace("\\", "/");

                String blobKey = blobPrefix + relativePath;

                String contentType = getContentType(file);

                log.info(
                        "Uploading encoded file to Blob Storage: {}",
                        blobKey
                );

                try
                {
                    var blobClient = encodedBlobContainerClient.getBlobClient(blobKey);

                    blobClient.uploadFromFile(
                            file.getAbsolutePath(),
                            true
                    );

                    blobClient.setHttpHeaders(
                            new BlobHttpHeaders()
                                    .setContentType(contentType)
                    );

                    log.info(
                            "Uploaded successfully: {}",
                            blobKey
                    );

                } catch (Exception ex) {
                    log.error(
                            "Failed to upload encoded file: {}",
                            blobKey,
                            ex
                    );

                    throw new RuntimeException(
                            "Failed to upload encoded file: "
                                    + blobKey,
                            ex
                    );
                }
            }
        }
    }

    private String getContentType(File file)
    {
        String fileName = file.getName().toLowerCase();

        if(fileName.endsWith(".m3u8")) {
            return "application/vnd.apple.mpegurl";
        }

        if(fileName.endsWith(".ts")) {
            return "video/mp2t";
        }

        if(fileName.endsWith(".mp4")) {
            return "video/mp4";
        }

        return "application/octet-stream";
    }

    // Clean up temp files after encoding
    private void cleanUpTempFiles(String jobPath)
    {
        try{
            Path dirPath = Paths.get(jobPath);
            if(Files.exists(dirPath)) {
                Files.walk(dirPath)
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);

                log.info("Temp files cleaned up for job: {}", jobPath);
            }
        }
        catch (IOException ex) {
            log.warn("Failed to clean temp files: {}", ex.getMessage());
        }
    }
}
