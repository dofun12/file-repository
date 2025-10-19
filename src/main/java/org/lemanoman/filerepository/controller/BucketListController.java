package org.lemanoman.filerepository.controller;

import org.lemanoman.filerepository.FileExtraInfo;
import org.lemanoman.filerepository.data.BucketConfig;
import org.lemanoman.filerepository.data.BucketDto;
import org.lemanoman.filerepository.data.SimpleBucketDto;
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
@RequestMapping("/buckets")
public class BucketListController {
    static final Logger logger = LoggerFactory.getLogger(BucketListController.class);
    private final StoreService uploadService;
    private final BucketService bucketService;

    public BucketListController(StoreService uploadService, BucketService bucketService) {
        this.uploadService = uploadService;
        this.bucketService = bucketService;
    }

    @PostMapping({"/",""})
    public String createBucket(@RequestParam("bucketName") String bucketName, Model model){
        try {
            BucketConfig bucketConfig = BucketConfig.BuildDefault();
            bucketConfig.setBucketName(bucketName);
            bucketService.addBucketConfig(bucketConfig);
        } catch (Exception e) {
            logger.error("Error creating bucket: {}", bucketName, e);
            model.addAttribute("errorMessage", "Error creating bucket: " + e.getMessage());
            return index(model);
        }
        return "redirect:/buckets";
    }


    @GetMapping({"/",""})
    public String index(Model model) {
        var buckets = bucketService.getListConfigs();
        model.addAttribute("bucketList", buckets);
        model.addAttribute("bucket", new SimpleBucketDto(""));

        return "bucket-list";
    }



}
