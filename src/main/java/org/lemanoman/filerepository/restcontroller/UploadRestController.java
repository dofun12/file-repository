package org.lemanoman.filerepository.restcontroller;

import org.lemanoman.filerepository.FileExtraInfo;
import org.lemanoman.filerepository.service.UploadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController("/api/v1/upload")
public class UploadRestController {

    private final UploadService uploadService;

    public UploadRestController(UploadService uploadService) {
        this.uploadService = uploadService;
    }

    @PostMapping("/generic")
    public ResponseEntity<Void> genericUpload(@RequestParam("file") MultipartFile file) {
        FileExtraInfo fileExtraInfo = new FileExtraInfo();
        fileExtraInfo.setFilename(file.getOriginalFilename());
        if(file.getOriginalFilename() != null && !file.getOriginalFilename().isEmpty() && file.getOriginalFilename().lastIndexOf('.') > -1) {
            fileExtraInfo.setFileExt(file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.') + 1));
        }
        fileExtraInfo.setFilesize(file.getSize());
        fileExtraInfo.setCreationDate(new java.util.Date());
        try {
            uploadService.storeFile(file.getInputStream(), fileExtraInfo);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }
}
