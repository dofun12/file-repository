package org.lemanoman.filerepository.controller;

import org.lemanoman.filerepository.FileExtraInfo;
import org.lemanoman.filerepository.data.BucketConfig;
import org.lemanoman.filerepository.service.BucketService;
import org.lemanoman.filerepository.service.StoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/files")
public class FilesController {
    static final Logger logger = LoggerFactory.getLogger(FilesController.class);
    private final StoreService storeService;
    private final BucketService bucketService;
    private final StoreService uploadService;

    public FilesController(StoreService storeService, BucketService bucketService, StoreService uploadService) {
        this.storeService = storeService;
        this.bucketService = bucketService;
        this.uploadService = uploadService;
    }


    @GetMapping({"/",""})
    public String index(Model model) {
        List<FileExtraInfo> metadataList = storeService.getListAllMetadata();
        model.addAttribute("bucketName", "All Buckets");
        model.addAttribute("metadataList", metadataList);
        return "files-list";
    }

    @GetMapping({"/bucket/{bucketName}"})
    public String index(@PathVariable String bucketName, Model model) {
        List<FileExtraInfo> metadataList = storeService.getListAllMetadataGroupByBucket().get(bucketName);
        model.addAttribute("bucketName", bucketName);
        model.addAttribute("metadataList", metadataList);
        return "files-list";
    }

    @PostMapping("/bucket/{bucketName}")
    public String handleFileUpload(
            @PathVariable("bucketName") String bucketName,
            @RequestParam(value = "file") MultipartFile[] files,
            Model model
    ) {


        if (files == null || files.length == 0) {
            model.addAttribute("message", "Please select a file to upload");
            return "redirect:/files/bucket/" + bucketName;
        }
        StringBuilder messages = new StringBuilder();
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
            } catch (IOException e) {
                messages.append(e.getMessage()).append(" \n");
                e.printStackTrace();
            }
        }
        model.addAttribute("message", messages.toString());
        return "redirect:/files/bucket/" + bucketName;
    }

}
