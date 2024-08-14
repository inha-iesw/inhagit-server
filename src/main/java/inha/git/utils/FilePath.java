package inha.git.utils;

import inha.git.common.exceptions.BaseException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import static inha.git.common.code.status.ErrorStatus.FILE_CONVERT;
import static inha.git.common.code.status.ErrorStatus.FILE_NOT_FOUND;

/**
 * FilePath는 파일을 저장하고 저장된 파일의 경로를 반환하는 클래스.
 */
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
            String baseDir = System.getProperty("user.dir") + "/source/"; // 현재 작업 디렉토리 + /source/
            Path filePath = Paths.get(baseDir + uploadDir, fileName);
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

    // 파일 확장자를 가져오는 헬퍼 메서드
    private static String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }
}