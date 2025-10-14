package org.lemanoman.filerepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class HashUtil {
    static final Logger logger = LoggerFactory.getLogger(HashUtil.class);
    public enum HashType {
        MD5, SHA256
    }
    public enum ALGORITHM {
        SHA256("SHA-256"),
        SHA512("SHA-512");

        private final String algorithm;

        ALGORITHM(String algorithm) {
            this.algorithm = algorithm;
        }

        public String getAlgorithm() {
            return algorithm;
        }
    }
    private static byte[] readPartial(RandomAccessFile raf, long start, long cursorSize) throws IOException {
        byte[] byteCursor = new byte[(int) cursorSize];
        raf.seek(start);
        raf.read(byteCursor);
        return byteCursor;
    }

    public static HashResult generateSHA(File file, ALGORITHM algorithm) throws IOException {
        final String filepath = file.getAbsolutePath();
        final long length = file.length();

        int buff = 16384;
        if(length<=1024*1024){
            buff = (int) length;
        }
        if(length>1024*1024){
            buff = 1024*1024;
            // logger.info("Using 1MB buffer");
        }
        if(length>1024*1024*20){
            buff = 1024*1024*20;
            // logger.info("Using 20MB buffer");
        }


        try (
                RandomAccessFile randomAccessFile = new RandomAccessFile(filepath, "r");
        ){


            MessageDigest hashSum = MessageDigest.getInstance(algorithm.getAlgorithm());

            byte[] buffer = new byte[buff];
            byte[] partialHash = null;

            long read = 0;

            // calculate the hash of the hole file for the test
            long offset = randomAccessFile.length();
            int unitsize;
            while (read < offset) {
                unitsize = (int) (((offset - read) >= buff) ? buff : (offset - read));
                randomAccessFile.read(buffer, 0, unitsize);
                hashSum.update(buffer, 0, unitsize);
                read += unitsize;
            }

            partialHash = hashSum.digest();
            byte[] hashBytes = partialHash;
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }


            final String hash = sb.toString();
            return new HashResult(new File(filepath), hash, algorithm.algorithm, length, 0);
        } catch (FileNotFoundException e) {
            // logger.error("File not found", e);
        } catch (NoSuchAlgorithmException e) {
            //  logger.error("Algorithm not found", e);
        } catch (IOException e) {
            //  logger.error("IO Exception", e);
        }
        return null;
    }
    public static HashResult getFullSHA512(File file) throws IOException {
        return generateSHA(file, ALGORITHM.SHA512);
    }

    public static HashResult getFullSHA256(File file) throws IOException {
        return generateSHA(file, ALGORITHM.SHA256);
    }

    public static HashResult getPartialSHA256(File file) {
        return getPartialHash(file, HashType.SHA256, 4, 1024);
    }

    public static HashResult getPartialMD5(File file) {
        return getPartialHash(file, HashType.MD5, 100, 1024);
    }

    public static HashResult getPartialHash(File file, HashType hashType, final long fragments, final long maxPartSize) {
        List<byte[]> parts = new ArrayList<>((int) fragments);
        long size = file.length();

        long portion = size/fragments;
        logger.debug("Portion: " + portion);
        long part = portion;
        if(portion>=maxPartSize){
            logger.debug("Portion is greater than 1mb");
            portion = maxPartSize;
        }
        try (RandomAccessFile raf = new RandomAccessFile(file, "r");){

            for(int i=0;i<fragments;i++){
                final byte[] partBytes = readPartial(raf,i*part,portion);
                parts.add(partBytes);
            }
            raf.close();
            logger.debug("Part size: " + parts.size());
            long totalPartSize = 0;
            for(byte[] partBytes:parts){
                totalPartSize+=partBytes.length;
            }
            byte[] finalBytes = new byte[(int)totalPartSize];
            for (int i = 0; i < parts.size(); i++) {
                byte[] partBytes = parts.get(i);
                //HashUtil.getSHA256Hash(partBytes);
                System.arraycopy(partBytes, 0, finalBytes, i * partBytes.length, partBytes.length);
            }
            long start = System.currentTimeMillis();
            String hash = "";
            if(hashType== HashType.MD5){
                hash = HashUtil.getMD5Hash(finalBytes);
            } else {
                hash = HashUtil.getSHA256Hash(finalBytes);
            }
            //final String md5 = HashUtil.getMD5Hash(finalBytes);
            return new HashResult(file, hash, hashType.toString(), finalBytes.length, System.currentTimeMillis()-start);
        } catch (FileNotFoundException e) {
            logger.debug("File not found", e);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return null;


    }

    public static HashResult getFullMD5Sum(File file){
        final long start = System.currentTimeMillis();
        try (InputStream is = Files.newInputStream(Paths.get(file.getAbsolutePath()))){
            return new HashResult(file,org.apache.commons.codec.digest.DigestUtils.md5Hex(is), HashType.MD5.toString(), file.length(), System.currentTimeMillis()-start);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    public static String getMD5Hash(byte[] input) {
        long start = System.currentTimeMillis();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(input);
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            long end = System.currentTimeMillis();
            logger.debug("MD5: " + sb.toString());
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    public static String getSHA256Hash(byte[] input) {
        long start = System.currentTimeMillis();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(input);
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            long end = System.currentTimeMillis();
            System.out.println("SHA256: " + (end - start) + "ms");
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage());
        }
        return null;
    }
}