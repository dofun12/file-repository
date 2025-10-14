package org.lemanoman.filerepository;

import java.io.File;

public record HashResult(
        File file, String hash, String hashType, long size, long timeTaken
) {
}
