package org.lemanoman.filerepository.data;

import org.lemanoman.filerepository.service.BucketService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class BucketConfig {
    private String bucketName;
    private String dateCreated;
    private String lastUpdate;
    private Long totalFiles=0L;
    private Long totalSize=0L;
    private Long timeToLive=0L;
    static final SimpleDateFormat SPDF = new SimpleDateFormat(BucketService.DATE_FORMAT);

    public static BucketConfig BuildDefault() {
        BucketConfig bucketConfig = new BucketConfig();
        bucketConfig.setBucketName("default");
        bucketConfig.setDateCreated(SPDF.format(new Date()));
        bucketConfig.setLastUpdate(SPDF.format(new Date()));
        bucketConfig.setTotalFiles(0L);
        bucketConfig.setTotalSize(0L);
        bucketConfig.setTimeToLive(-1L);
        return bucketConfig;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BucketConfig that = (BucketConfig) o;
        return Objects.equals(bucketName, that.bucketName) && Objects.equals(dateCreated, that.dateCreated) && Objects.equals(lastUpdate, that.lastUpdate) && Objects.equals(totalFiles, that.totalFiles) && Objects.equals(totalSize, that.totalSize) && Objects.equals(timeToLive, that.timeToLive);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bucketName, dateCreated, lastUpdate, totalFiles, totalSize, timeToLive);
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Long getTotalFiles() {
        return totalFiles;
    }

    public void setTotalFiles(Long totalFiles) {
        this.totalFiles = totalFiles;
    }

    public Long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(Long totalSize) {
        this.totalSize = totalSize;
    }

    public Long getTimeToLive() {
        return timeToLive;
    }

    public void setTimeToLive(Long timeToLive) {
        this.timeToLive = timeToLive;
    }
}
