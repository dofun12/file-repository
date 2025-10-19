package org.lemanoman.filerepository.restcontroller;

import org.lemanoman.filerepository.FileExtraInfo;
import org.lemanoman.filerepository.data.ResponseDTO;
import org.lemanoman.filerepository.service.StoreService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController("/api/v1/upload")
public class UploadRestController {

    private final StoreService uploadService;

    public UploadRestController(StoreService uploadService) {
        this.uploadService = uploadService;
    }

    @PostMapping("/bucket/{bucketName}")
    public ResponseDTO<Void> handleFileUpload(
            @PathVariable("bucketName") String bucketName,
            @RequestParam(value = "file") MultipartFile[] files
    ) {


        if (files == null || files.length == 0) {
            return new ResponseDTO<>("Parameter file is missing", false, HttpStatus.BAD_REQUEST);
        }
        StringBuilder messages = new StringBuilder();
        List<FileExtraInfo> success = new ArrayList<>();
        for (MultipartFile file : files) {
            FileExtraInfo fileExtraInfo = new FileExtraInfo();
            fileExtraInfo.setFilename(file.getOriginalFilename());
            fileExtraInfo.setFilesize(file.getSize());
            fileExtraInfo.setCreationDate(new java.util.Date());
            try {
                if (bucketName == null || bucketName.isEmpty()) {
                    bucketName = "default";
                }
                uploadService.storeFile(file.getInputStream(), fileExtraInfo, bucketName);
                messages.append(file.getOriginalFilename()).append(",\n ");
                success.add(fileExtraInfo);
            } catch (Exception e) {
                messages.append(e.getMessage()).append(" \n");
                e.printStackTrace();
            }
        }
        return new ResponseDTO<>(messages.toString(), true, HttpStatus.OK);

    }
}
