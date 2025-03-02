package inha.git.utils.file;

import inha.git.common.exceptions.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import static inha.git.common.Constant.BASE_DIR_SOURCE;
import static inha.git.common.code.status.ErrorStatus.FILE_CONVERT;
import static inha.git.common.code.status.ErrorStatus.FILE_NOT_FOUND;

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
            log.error("File not found");
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
            log.info("File stored: " + filePath);
            return "/" + uploadDir + "/" + fileName;
        } catch (IOException e) {
            log.error("Failed to convert file");
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

    private static String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }
}
