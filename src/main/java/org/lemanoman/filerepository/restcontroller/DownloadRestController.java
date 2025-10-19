package org.lemanoman.filerepository.restcontroller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.lemanoman.filerepository.FileExtraInfo;
import org.lemanoman.filerepository.service.LocationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@RestController
@RequestMapping("/api/v1/download")
public class DownloadRestController {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Logger logger = LoggerFactory.getLogger(DownloadRestController.class);
    private final LocationService locationService;

    public DownloadRestController(LocationService locationService) {
        this.locationService = locationService;
    }

    private MediaType getMediaTypeForFileName(String filename) {
        String mimeType = "application/octet-stream";
        if (filename.endsWith(".mp4")) {
            mimeType = "video/mp4";
        } else if (filename.endsWith(".avi")) {
            mimeType = "video/x-msvideo";
        } else if (filename.endsWith(".mkv")) {
            mimeType = "video/x-matroska";
        } else if (filename.endsWith(".mov")) {
            mimeType = "video/quicktime";
        } else if (filename.endsWith(".png")) {
            mimeType = "image/png";
        } else if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
            mimeType = "image/jpeg";
        } else if (filename.endsWith(".gif")) {
            mimeType = "image/gif";
        } else if (filename.endsWith(".pdf")) {
            mimeType = "application/pdf";
        } else if (filename.endsWith(".txt")) {
            mimeType = "text/plain";
        }
        return MediaType.parseMediaType(mimeType);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InputStreamResource> getDownloadVideo(@PathVariable String id) {

        File metadataFile = new File(locationService.getMetadataFolder(), id+".json");
        if (!metadataFile.exists() || !metadataFile.isFile()) {
            return ResponseEntity.notFound().build();
        }
        FileExtraInfo metadata = null;
        try {
            metadata = objectMapper.readValue(metadataFile, FileExtraInfo.class);
        } catch (IOException e) {
            logger.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
        if (metadata == null) {
            return ResponseEntity.notFound().build();
        }
        File realFile = new File(metadata.getPath());
        if (!realFile.exists() || !realFile.isFile()) {
            return ResponseEntity.notFound().build();
        }
        MediaType mediaType = getMediaTypeForFileName(metadata.getFileExt());
        try {
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + metadata.getFilename()).contentLength(realFile.length()).contentType(mediaType).body(new InputStreamResource(new FileInputStream(realFile)));
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return ResponseEntity.notFound().build();
    }
}
