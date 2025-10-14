package org.lemanoman.filerepository;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class FileUtils {
    //Scan files recursively

    public static String bytesToHumanReadable(long bytes) {
        int unit = 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = "KMGTPE".charAt(exp - 1) + "i";
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public static String bytesToHumanReadable(double bytes) {
        int unit = 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = "KMGTPE".charAt(exp - 1) + "i";
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public static List<File> listAllSubDir(File someFile) {
        List<File> files = new ArrayList<>();
        if (someFile == null) {
            return files;
        }

        if (!someFile.isDirectory()) {
            return files;
        }
        final var list = someFile.listFiles();
        if (list == null) {
            return files;
        }
        for (File f : list) {
            if (f.isDirectory()) {
                files.add(f);
                files.addAll(listAllSubDir(f));
            }
        }
        return files;

    }

    public static String fromBase64(String base64Path) {
        return new String(Base64.getDecoder().decode(base64Path));
    }

    public  static boolean isFileEquals(File sourceFile, File targetFile) {
        if (sourceFile == null || targetFile == null) {
            return false;
        }
        if (!sourceFile.exists() || !targetFile.exists()) {
            return false;
        }
        if (sourceFile.isDirectory() && targetFile.isDirectory()) {
            return true;
        }
        if (sourceFile.length() != targetFile.length()) {
            return false;
        }
        var hashA = HashUtil.getPartialMD5(sourceFile).hash();
        var hashB = HashUtil.getPartialMD5(targetFile).hash();
        if (hashA == null || hashB == null) {
            return false;
        }
        return hashA.equals(hashB);
    }

    public static List<File> listFiles(File someFile) {
        List<File> files = new ArrayList<>();
        if (someFile == null) {
            return files;
        }

        if (!someFile.isDirectory()) {
            files.add(someFile);
            return files;
        }
        final var list = someFile.listFiles();
        if (list == null) {
            return files;
        }
        for (File f : list) {
            if (f.isDirectory()) {
                files.addAll(listFiles(f));
            } else {
                files.add(f);
            }
        }
        return files;
    }

    public static void copyFileUsingStream(File source, File dest) throws IOException {
        try (
                InputStream is = new FileInputStream(source);
                OutputStream os = new FileOutputStream(dest);
        ) {
            byte[] buffer;
            long oneMb = 1024L * 1024L;
            if(source.length() < oneMb) {
                buffer = new byte[(int)source.length()];
            }else {
                buffer = new byte[(int) oneMb];
            }
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        }
    }


    public static void copyFileUsingChannel(File source, File dest) throws IOException {

        try (
                FileInputStream fis = new FileInputStream(source);
                FileOutputStream fos = new FileOutputStream(dest);
                FileChannel sourceChannel = fis.getChannel();
                FileChannel destChannel = fos.getChannel();
        ) {
            destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
        }
    }


    public interface OnFileFound {
        public void onFileFound(File file);
    }

    public static void scanFiles(File someFile, OnFileFound onFileFound) {
        if (someFile == null) {
            return;
        }

        if (!someFile.isDirectory()) {
            onFileFound.onFileFound(someFile);
            return;
        }
        final var list = someFile.listFiles();
        if (list == null) {
            return;
        }
        for (File f : list) {
            if (f.isDirectory()) {
                scanFiles(f, onFileFound);
            } else {
                onFileFound.onFileFound(f);
            }
        }
    }
}

