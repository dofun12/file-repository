package org.lemanoman.filerepository.data;

public class HashData {
    private String path;
    private String id;
    private String metadataPath;

    public String getPath() {
        return path;
    }

    public String getMetadataPath() {
        return metadataPath;
    }

    public void setMetadataPath(String metadataPath) {
        this.metadataPath = metadataPath;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
