package org.lemanoman.filerepository.controller;

import org.lemanoman.filerepository.FileExtraInfo;
import org.lemanoman.filerepository.data.BucketConfig;
import org.lemanoman.filerepository.service.BucketService;
import org.lemanoman.filerepository.service.UploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/")
public class UploadController {
    static final Logger logger = LoggerFactory.getLogger(UploadController.class);
    private final UploadService uploadService;
    private final BucketService bucketService;

    public UploadController(UploadService uploadService, BucketService bucketService) {
        this.uploadService = uploadService;
        this.bucketService = bucketService;
    }


    @GetMapping
    public String index() {

        return "upload";
    }

    @PostMapping("/upload")
    public String handleFileUpload(
            @RequestParam(value = "file") MultipartFile file,
            @RequestParam(value = "bucketName",required = false) String bucketName,
            Model model
    ) {
        if(file.isEmpty()) {
            model.addAttribute("message", "Please select a file to upload");
            return "upload";
        }
        model.addAttribute("bucketList", bucketService.getListConfigs().stream().map(BucketConfig::getBucketName));

        FileExtraInfo fileExtraInfo = new FileExtraInfo();
        fileExtraInfo.setFilename(file.getOriginalFilename());
        fileExtraInfo.setFilesize(file.getSize());
        fileExtraInfo.setCreationDate(new java.util.Date());
        try {
            if(bucketName == null || bucketName.isEmpty()) {
                bucketName = "default";
            }
            uploadService.storeFile(file.getInputStream(), fileExtraInfo, bucketName);
            model.addAttribute("message", "File uploaded successfully: " + file.getOriginalFilename());
        } catch (IOException e) {
            model.addAttribute("message", e.getMessage());
            e.printStackTrace();
        }
        return "upload";
    }
}
