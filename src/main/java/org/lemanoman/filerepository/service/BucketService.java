package org.lemanoman.filerepository.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.lemanoman.filerepository.FileExtraInfo;
import org.lemanoman.filerepository.data.BucketConfig;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class BucketService {
    public final LocationService locationService;
    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    final ObjectMapper objectMapper = new ObjectMapper();

    public BucketService(LocationService locationService) {
        this.locationService = locationService;
    }

    public List<BucketConfig> getListConfigs() {
        File bucketConfigFile = locationService.getBucketFile();
        if(!bucketConfigFile.exists()) {
            return new ArrayList<>();
        }
        BucketConfig[] bucketConfigs = {};
        try {
            bucketConfigs = objectMapper.readValue(bucketConfigFile, BucketConfig[].class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Arrays.asList(bucketConfigs);
    }

    public void changeBucketConfig(final String bucketName, BucketConfig bucketConfig) {
        var list = getListConfigs();
        var newList = new ArrayList<>(list.stream().map(b -> {
            if(b.getBucketName().equals(bucketName)) {
                return bucketConfig;
            }
            return b;
        }).toList());
        persistBucketConfig(newList);
    }

    private void persistBucketConfig(List<BucketConfig> bucketConfigs) {
        File bucketConfigFile = locationService.getBucketFile();
        try (FileOutputStream fos = new FileOutputStream(bucketConfigFile);
             BufferedOutputStream bos = new BufferedOutputStream(fos)
        ) {
            objectMapper.writeValue(bos, bucketConfigs);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addBucketConfig(BucketConfig bucketConfig) {
        var lastList = new ArrayList<>(getListConfigs());
        if(lastList.stream().noneMatch(b -> b.getBucketName().equals(bucketConfig.getBucketName()))) {
            lastList.add(bucketConfig);
            persistBucketConfig(lastList);
            return;
        }
        changeBucketConfig(bucketConfig.getBucketName(), bucketConfig);

    }

    public void addBucketConfig(FileExtraInfo fileExtraInfo) {
            String bucketName = fileExtraInfo.getBucketName();
            var lastList = new ArrayList<>(getListConfigs());
            if(lastList.stream().noneMatch(b -> b.getBucketName().equals(bucketName))) {
                BucketConfig bucketConfig = new BucketConfig();
                bucketConfig.setBucketName(bucketName);
                bucketConfig.setDateCreated(new SimpleDateFormat(DATE_FORMAT).format(new Date()));
                bucketConfig.setTotalFiles(1L);
                bucketConfig.setTotalSize(fileExtraInfo.getFilesize());
                lastList.add(bucketConfig);
                persistBucketConfig(lastList);
                return;
            }
            BucketConfig bucketConfig = lastList.stream().filter(e -> e.getBucketName().equals(bucketName)).findFirst().get();
            bucketConfig.setBucketName(bucketName);
            bucketConfig.setDateCreated(new SimpleDateFormat(DATE_FORMAT).format(new Date()));
            bucketConfig.setLastUpdate(new SimpleDateFormat(DATE_FORMAT).format(new Date()));
            bucketConfig.setTotalFiles(bucketConfig.getTotalFiles() + 1L);
            bucketConfig.setTotalSize(bucketConfig.getTotalSize()+ fileExtraInfo.getFilesize());
            changeBucketConfig(bucketConfig.getBucketName(), bucketConfig);
    }
}
