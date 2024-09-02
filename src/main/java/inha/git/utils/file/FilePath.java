package inha.git.utils.file;

import inha.git.common.exceptions.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static inha.git.common.Constant.*;
import static inha.git.common.code.status.ErrorStatus.*;

/**
 * FilePath는 파일을 저장하고 저장된 파일의 경로를 반환하는 클래스.
 */
@Slf4j
public class FilePath {

    /**
     * 파일을 저장하고 저장된 파일의 경로를 반환하는 메서드
     *
     * @param file      저장할 파일
     * @param uploadDir 저장할 디렉토리
     * @return 저장된 파일의 경로
     */

    public static String storeFile(MultipartFile file, String uploadDir) {
        if (file.isEmpty()) {
            throw new BaseException(FILE_NOT_FOUND);
        }
        try {
            // 고유한 파일명 생성 (타임스탬프 + UUID 6자리)
            String fileName = System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 6) +
                    getFileExtension(file.getOriginalFilename());
            // 파일 저장 경로 설정
            Path filePath = Paths.get(BASE_DIR_SOURCE + uploadDir, fileName);
            // 디렉토리가 존재하지 않으면 생성
            Files.createDirectories(filePath.getParent());
            // 파일 저장
            file.transferTo(filePath.toFile());
            // 저장된 파일의 경로 반환 (예: /evidence/파일명)
            return "/" + uploadDir + "/" + fileName;
        } catch (IOException e) {
            throw new BaseException(FILE_CONVERT);
        }
    }

    /**
     * 파일 삭제
     *
     * @param filePath 삭제할 파일 경로
     * @return 파일 삭제 여부
     */
    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }

    /**
     * 디렉토리 삭제
     *
     * @param dirPath 삭제할 디렉토리 경로
     * @return 디렉토리 삭제 여부
     */
    public static boolean deleteDirectory(String dirPath) {
        File directory = new File(dirPath);
        if (!directory.exists()) {
            log.error("Directory does not exist: " + dirPath);
            return false;
        }

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file.getAbsolutePath());
                } else {
                    boolean deleted = file.delete();
                    if (!deleted) {
                        log.error("Failed to delete file: " + file.getAbsolutePath());
                    }
                }
            }
        }

        boolean isDirDeleted = directory.delete();
        if (!isDirDeleted) {
            log.error("Failed to delete directory: " + dirPath);
        }

        return isDirDeleted;
    }

    /**
     * 프로젝트 경로 생성
     *
     * @param folderName 폴더명
     * @return 프로젝트 경로
     */
    public static Path generateProjectPath(String folderName) {
        String projectRelativePath = PROJECT + '/' + folderName;
        return Paths.get(BASE_DIR_SOURCE_2, projectRelativePath);
    }

    /**
     * 폴더명 생성
     *
     * @return 폴더명
     */
    public static String generateFolderName() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String uniqueSuffix = UUID.randomUUID().toString().substring(0, 6);
        return timestamp + "-" + uniqueSuffix;
    }

    public static void zipDirectory(Path sourceDirPath, Path zipFilePath) {
        try (FileOutputStream fos = new FileOutputStream(zipFilePath.toFile());
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            Files.walk(sourceDirPath)
                    .filter(path -> !Files.isDirectory(path))
                    .forEach(path -> {
                        ZipEntry zipEntry = new ZipEntry(sourceDirPath.relativize(path).toString());
                        try {
                            zos.putNextEntry(zipEntry);
                            Files.copy(path, zos);
                            zos.closeEntry();
                        } catch (IOException e) {
                            throw new BaseException(FILE_COMPRESS_FAIL);
                        }
                    });
        } catch (IOException e) {
            throw new BaseException(FILE_COMPRESS_FAIL);
        }
    }
    /**
     * 파일 확장자 반환
     *
     * @param fileName 파일명
     * @return 파일 확장자
     */
    private static String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }
}