package org.lemanoman.filerepository;

import java.io.File;

public record StoreResult(File realFile, File metadataFile, String hashValue, String idFile) {
}
