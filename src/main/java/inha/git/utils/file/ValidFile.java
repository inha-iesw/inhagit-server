package inha.git.utils.file;

import inha.git.common.exceptions.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static inha.git.common.code.status.ErrorStatus.*;

/**
 * ValidFile 클래스는 파일 유효성 검사 기능을 제공하는 클래스
 */

@Slf4j
public class ValidFile {

    private static final int MAX_FILES = 100;
    private static final long MAX_SIZE_MB = 200;
    private static final long MAX_SIZE_BYTES = MAX_SIZE_MB * 1024 * 1024;
    private static final Set<String> ALLOWED_CONTENT_TYPES = new HashSet<>();

    static {
        ALLOWED_CONTENT_TYPES.add("application/zip");
        ALLOWED_CONTENT_TYPES.add("application/octet-stream");
        ALLOWED_CONTENT_TYPES.add("multipart/x-zip");
        ALLOWED_CONTENT_TYPES.add("application/zip-compressed");
        ALLOWED_CONTENT_TYPES.add("application/x-zip-compressed");
        ALLOWED_CONTENT_TYPES.add("application/x-zip");
    }

    /**
     * zip 파일 유효성 검사
     *
     * @param file zip 파일
     */
    public static File validateAndProcessZipFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BaseException(FILE_NOT_FOUND);
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new BaseException(FILE_NOT_ZIP);
        }

        File processedZipFile = null;
        try {
            processedZipFile = File.createTempFile("processed_", ".zip");
        } catch (IOException e) {
            log.error("Error creating temporary file: " + e.getMessage(), e);
            throw new BaseException(FILE_PROCESS_ERROR);
        }

        try (ZipFile zipFile = new ZipFile(convertMultiPartToFile(file));
             ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(processedZipFile))) {

            int fileCount = 0;  // 파일 개수를 카운트
            long totalSize = 0;

            for (Enumeration<? extends ZipEntry> entries = zipFile.entries(); entries.hasMoreElements();) {
                ZipEntry entry = entries.nextElement();
                String fileName = entry.getName();

                // 디렉토리인지 확인하고, 디렉토리는 카운트에서 제외
                if (entry.isDirectory()) {
                    continue;
                }

                // 파일명을 처리하고 공백을 언더스코어로 변경
                String processedFileName = processFileName(fileName);

                ZipEntry newEntry = new ZipEntry(processedFileName);
                zos.putNextEntry(newEntry);

                fileCount++;  // 파일 개수 카운트
                long entrySize = copyAndCountBytes(zipFile.getInputStream(entry), zos);
                totalSize += entrySize;
                if (totalSize > MAX_SIZE_BYTES) {
                    throw new BaseException(FILE_MAX_SIZE);
                }
                zos.closeEntry();
            }

            // 최종 파일 개수 로그 출력
            log.info("Total number of files in the zip: {}", fileCount);
        } catch (IOException e) {
            log.error("Error processing ZIP file: " + e.getMessage(), e);
            throw new BaseException(FILE_PROCESS_ERROR);
        }

        return processedZipFile;
    }

    private static File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = File.createTempFile("temp", ".zip");
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
        }
        return convFile;
    }

    private static String processFileName(String fileName) {
        // 공백을 언더스코어로 변경
        String processed = fileName.replaceAll("\\s", "_");

        // 파일 경로에 '..'가 포함되어 있는지 확인 (보안 취약점 방지)
        if (processed.contains("..")) {
            throw new BaseException(FILE_INVALID_NAME);
        }

        return processed;
    }

    private static long copyAndCountBytes(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[8192];
        long count = 0;
        int n;
        while ((n = in.read(buffer)) > 0) {
            out.write(buffer, 0, n);
            count += n;
        }
        return count;
    }



    private static final long MAX_SIZE_BYTES_2 = MAX_SIZE_MB * 1024 * 1024;

    private static final Set<String> ALLOWED_CONTENT_TYPES_2 = new HashSet<>();

    static {
        ALLOWED_CONTENT_TYPES_2.add("application/pdf");
        ALLOWED_CONTENT_TYPES_2.add("image/png");
        ALLOWED_CONTENT_TYPES_2.add("image/jpg");
        ALLOWED_CONTENT_TYPES_2.add("image/jpeg");
        ALLOWED_CONTENT_TYPES_2.add("image/gif");
        ALLOWED_CONTENT_TYPES_2.add("application/zip");
        ALLOWED_CONTENT_TYPES_2.add("application/octet-stream");
        ALLOWED_CONTENT_TYPES_2.add("multipart/x-zip");
        ALLOWED_CONTENT_TYPES_2.add("application/zip-compressed");
        ALLOWED_CONTENT_TYPES_2.add("application/x-zip-compressed");
        ALLOWED_CONTENT_TYPES_2.add("application/x-zip");
    }

    /**
     * JPG, PNG, PDF, ZIP 파일 유효성 검사
     *
     * @param file 업로드할 파일
     */
    public static void validateImagePdfZipFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BaseException(FILE_NOT_FOUND);
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES_2.contains(contentType)) {
            throw new BaseException(FILE_INVALID_TYPE);
        }

        if (file.getSize() > MAX_SIZE_BYTES_2) {
            throw new BaseException(FILE_MAX_SIZE);
        }
    }
}
