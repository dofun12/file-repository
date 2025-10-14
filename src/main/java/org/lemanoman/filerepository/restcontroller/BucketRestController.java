package org.lemanoman.filerepository.restcontroller;

import org.lemanoman.filerepository.data.BucketConfig;
import org.lemanoman.filerepository.data.BucketDto;
import org.lemanoman.filerepository.service.BucketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/v1/bucket")
public class BucketRestController {

    private static Logger log = LoggerFactory.getLogger(BucketRestController.class);

    private final BucketService bucketService;

    public BucketRestController(BucketService bucketService) {
        this.bucketService = bucketService;
    }

    @GetMapping("/")
    public List<BucketConfig> getListOfBuckets() {
        return bucketService.getListConfigs();
    }

    @PostMapping("/")
    public ResponseEntity<Void> createBucket(@RequestBody BucketDto bucketDto) {
        try {
            BucketConfig bucketConfig = new BucketConfig();
            bucketConfig.setBucketName(bucketDto.getBucketName());
            bucketConfig.setLastUpdate(new SimpleDateFormat(BucketService.DATE_FORMAT).format(new Date()));
            bucketConfig.setDateCreated(new SimpleDateFormat(BucketService.DATE_FORMAT).format(new Date()));
            bucketConfig.setTotalFiles(0L);
            bucketConfig.setTotalSize(0L);
            bucketConfig.setTimeToLive(-1L);
            bucketService.addBucketConfig(bucketConfig);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error creating bucket: {}", bucketDto.getBucketName(), e);
            return ResponseEntity.status(500).build();
        }
    }
}
