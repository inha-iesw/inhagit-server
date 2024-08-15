package inha.git.utils.file;

import inha.git.common.exceptions.BaseException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static inha.git.common.code.status.ErrorStatus.*;

/**
 * ValidFile 클래스는 파일 유효성 검사 기능을 제공하는 클래스
 */
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
    public static void validateZipFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BaseException(FILE_NOT_FOUND);
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new BaseException(FILE_NOT_ZIP);
        }

        try (ZipInputStream zis = new ZipInputStream(file.getInputStream())) {
            ZipEntry entry;
            int fileCount = 0;
            long totalSize = 0;

            while ((entry = zis.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    fileCount++;
                    totalSize += entry.getSize();

                    if (fileCount > MAX_FILES) {
                        throw new BaseException(FILE_MAX_FILES);
                    }

                    if (totalSize > MAX_SIZE_BYTES) {
                        throw new BaseException(FILE_MAX_SIZE);
                    }
                }
            }
        } catch (IOException e) {
            throw new BaseException(FILE_CONVERT);
        }
    }
}
