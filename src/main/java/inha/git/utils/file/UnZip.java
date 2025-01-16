package inha.git.utils.file;

import inha.git.common.exceptions.BaseException;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import static inha.git.common.code.status.ErrorStatus.FILE_UNZIP_ERROR;

/**
 * UnZip 클래스는 ZIP 파일을 해제하는 기능을 제공하는 클래스입니다.
 */
@Slf4j

public class UnZip {

    public static void unzipFile(String zipFilePath, String destDirectory) {
        log.info("Starting to unzip file: {}", zipFilePath);
        log.info("Destination directory: {}", destDirectory);

        File destDir = new File(destDirectory);
        if (!destDir.exists()) {
            log.info("Destination directory does not exist. Creating directory: {}", destDirectory);
            destDir.mkdirs();
        }

        try (ZipFile zipFile = createZipFileWithFallback(zipFilePath)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                String filePath = destDirectory + File.separator + entry.getName();
                log.info("Processing entry: {}", entry.getName());

                // 디렉토리인지 확인
                if (entry.isDirectory()) {
                    log.info("Entry is a directory. Creating directory: {}", filePath);
                    File dir = new File(filePath);
                    dir.mkdirs();
                } else {
                    // 파일을 처리 (DEFLATED 및 STORED 지원)
                    if (entry.getMethod() == ZipEntry.DEFLATED || entry.getMethod() == ZipEntry.STORED) {
                        log.info("Entry is a file. Extracting to: {}", filePath);
                        File outputFile = new File(filePath);
                        if (!outputFile.getParentFile().exists()) {
                            log.info("Creating parent directory for file: {}", outputFile.getParentFile().getAbsolutePath());
                            outputFile.getParentFile().mkdirs();
                        }

                        try (InputStream inputStream = zipFile.getInputStream(entry)) {
                            Files.copy(inputStream, Path.of(filePath));
                            log.info("Successfully extracted file: {}", filePath);
                        } catch (IOException e) {
                            log.error("Error extracting file: {}", filePath, e);
                            throw new BaseException(FILE_UNZIP_ERROR);
                        }
                    } else {
                        log.warn("Unsupported compression method for entry: {}", entry.getName());
                        throw new BaseException(FILE_UNZIP_ERROR);
                    }
                }
            }
        } catch (IOException e) {
            log.error("Error unzipping file: {}", zipFilePath, e);
            throw new BaseException(FILE_UNZIP_ERROR);
        }
    }

    // 윈도우에서 압축된 파일은 CP-949로 처리, UTF-8 실패 시 CP-949로 다시 시도
    private static ZipFile createZipFileWithFallback(String zipFilePath) throws IOException {
        try {
            return new ZipFile(zipFilePath, StandardCharsets.UTF_8);
        } catch (ZipException e) {
            log.warn("Failed to unzip with UTF-8 encoding. Retrying with CP-949...");
            return new ZipFile(zipFilePath, Charset.forName("CP949"));
        }
    }
}
