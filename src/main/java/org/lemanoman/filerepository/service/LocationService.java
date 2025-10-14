package org.lemanoman.filerepository.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class LocationService {

    @Value("${filerepository.root.path}")
    private String rootPath;

    final Logger logger = LoggerFactory.getLogger(LocationService.class);

    public File getSubFolder(File rootFolder, String id) {

        File subFolder = new File(rootFolder, id.substring(0,4));
        if (subFolder.exists()) {
            return subFolder;
        }
        if(subFolder.mkdirs()) {
            return subFolder;
        }

        return null;
    }
    public File getHashDataFolder() {
        File hashDataDir = new File(rootPath, ".hashdata");
        if (!hashDataDir.exists()) {
            if (!hashDataDir.mkdirs()) {
                logger.error("Nao conseguiu criar o diretorio de hashdata");
                return null;
            }
        }
        return hashDataDir;
    }
    public File getCommonFolder() {
        File commonDir = new File(rootPath, "common");
        if (!commonDir.exists()) {
            if (!commonDir.mkdirs()) {
                logger.error("Nao conseguiu criar o diretorio de common");
                return null;
            }
        }
        return commonDir;
    }

    public File getHashFile() {
        File hashDataDir = getHashDataFolder();
        if (!hashDataDir.exists()) {
            if (!hashDataDir.mkdirs()) {
                logger.error("Nao conseguiu criar o diretorio de hashdata");
                return null;
            }
        }
        return new File(hashDataDir, ".hash.json");
    }

    public File getBucketFile() {
        File metadataDir = getMetadataFolder();
        if (!metadataDir.exists()) {
            if (!metadataDir.mkdirs()) {
                logger.error("Nao conseguiu criar o diretorio de metadados");
                return null;
            }
        }
        return new File(metadataDir, ".bucket.json");
    }

    public File getRootFolder() {
        File rootFolder = new File(rootPath);
        if (!rootFolder.exists()) {
            if (!rootFolder.mkdirs()) {
                return null;
            }
        }
        return rootFolder;
    }

    public String getBaseLocation() {
        return rootPath;
    }

    public File getMetadataFolder() {
        File metadataDir = new File(rootPath, ".metadata");
        if (!metadataDir.exists()) {
            if (!metadataDir.mkdirs()) {
                return null;
            }
        }
        return metadataDir;
    }
}
