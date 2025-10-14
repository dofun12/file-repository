package org.lemanoman.filerepository.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.lemanoman.filerepository.FileExtraInfo;
import org.lemanoman.filerepository.HashResult;
import org.lemanoman.filerepository.HashUtil;
import org.lemanoman.filerepository.StoreResult;
import org.lemanoman.filerepository.data.HashData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class UploadService {
    final Logger logger = LoggerFactory.getLogger(UploadService.class);
    private ExecutorService executor = Executors.newFixedThreadPool(2);

    final LocationService locationService;
    final ObjectMapper objectMapper = new ObjectMapper();
    final BucketService bucketService;

    public UploadService(LocationService locationService, BucketService bucketService) {
        this.locationService = locationService;
        this.bucketService = bucketService;
    }
    public File storeMetadata(FileExtraInfo fileExtraInfo) {
        File metadataDir = locationService.getMetadataFolder();
        if (!metadataDir.exists()) {
            if (!metadataDir.mkdirs()) {
                logger.error("Nao conseguiu criar o diretorio de metadados");
                return null;
            }
        }
        File metadataFile = new File(metadataDir, fileExtraInfo.getId() + ".json");
        try (FileOutputStream fos = new FileOutputStream(metadataFile);
             BufferedOutputStream bos = new BufferedOutputStream(fos)) {
            objectMapper.writeValue(bos, fileExtraInfo);
            return metadataFile;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public StoreResult storeFile(InputStream inputStream, FileExtraInfo fileExtraInfo) {
       return storeFile(inputStream, fileExtraInfo, "default");
    }


    public String getExtensionFromFilename(String filename) {
        if(filename!=null && !filename.isEmpty() && filename.lastIndexOf('.')>-1) {
            return filename.substring(filename.lastIndexOf('.') + 1);
        }
        return null;
    }

    public StoreResult storeFile(InputStream inputStream, FileExtraInfo fileExtraInfo, String bucketName) {
        String randomName = UUID.randomUUID().toString();
        fileExtraInfo.setId(randomName);
        fileExtraInfo.setBucketName(bucketName);

        bucketService.addBucketConfig(fileExtraInfo);

        File baseFolder = locationService.getRootFolder();
        File bucketFolder = new File(baseFolder, bucketName);
        File subFolder = locationService.getSubFolder(bucketFolder, randomName);
        if(fileExtraInfo.getFilename()!=null) {
            fileExtraInfo.setFileExt(getExtensionFromFilename(fileExtraInfo.getFilename()));
        }


        File file1 = new File(subFolder, randomName+"." + fileExtraInfo.getFileExt());
        try (BufferedInputStream bis = new BufferedInputStream(inputStream);
             FileOutputStream fileOutputStream = new FileOutputStream(file1);
             BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream)
        ){
            byte[] buffer = new byte[1048576];
            logger.info("Tentando com buff de " + buffer.length);
            int count;
            while ((count = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, count);
            }
            logger.info("Dando um flush");

            logger.info("Finalizando flush");
            Set<PosixFilePermission> perms = new HashSet<>();
            perms.add(PosixFilePermission.OTHERS_READ);
            perms.add(PosixFilePermission.OTHERS_WRITE);
            perms.add(PosixFilePermission.GROUP_WRITE);
            perms.add(PosixFilePermission.GROUP_READ);
            try {
                Files.setPosixFilePermissions(file1.toPath(), perms);
            } catch (UnsupportedOperationException ex) {
                logger.warn("Nao da para mudar as permissoes, chora ae");
            }
            fileExtraInfo.setCreationDate(new Date());
            fileExtraInfo.setLastModifiedDate(new Date(file1.lastModified()));
            fileExtraInfo.setFilesize(file1.length());
            fileExtraInfo.setPath(file1.getAbsolutePath());
            HashResult hashResult = HashUtil.getPartialMD5(file1);
            fileExtraInfo.setHashSum(hashResult.hash());

            var metaFile = storeMetadata(fileExtraInfo);
            final StoreResult storeResult = new StoreResult(file1, metaFile, hashResult.hash(), randomName);
            executor.submit(() -> optimizeStorage(storeResult));
            return storeResult;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



    public void optimizeStorage(StoreResult storeResult) {
        File hashDataFolder = locationService.getHashDataFolder();
        for (File file : Objects.requireNonNull(hashDataFolder.listFiles())) {
            if (file.getName().startsWith(storeResult.hashValue())) {
                try {
                    Files.delete(storeResult.realFile().toPath());
                    File commonFolder = locationService.getCommonFolder();
                    final var hashData = objectMapper.readValue(file, HashData.class);

                    File metadataFile = new File(hashData.getMetadataPath());
                    var metadata = objectMapper.readValue(metadataFile, FileExtraInfo.class);
                    File targetFile = new File(commonFolder, hashData.getId() + "."+ metadata.getFileExt());
                    if(!targetFile.exists()) {
                        Files.move(Path.of(hashData.getPath()), Path.of(targetFile.getPath()));
                    }
                    metadata.setPath(targetFile.getAbsolutePath());
                    objectMapper.writeValue(metadataFile, metadata);

                    var novoMetadata = objectMapper.readValue(storeResult.metadataFile(), FileExtraInfo.class);
                    novoMetadata.setPath(targetFile.getAbsolutePath());
                    objectMapper.writeValue(storeResult.metadataFile(), novoMetadata);
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        File newHashFile = new File(hashDataFolder, storeResult.hashValue());
        try {
            HashData hashData = new HashData();
            hashData.setPath(storeResult.realFile().getAbsolutePath());
            hashData.setId(storeResult.hashValue());
            hashData.setMetadataPath(storeResult.metadataFile().getAbsolutePath());
            objectMapper.writeValue(newHashFile, hashData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
