package inha.git.utils.file;

import inha.git.common.exceptions.BaseException;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static inha.git.common.code.status.ErrorStatus.FILE_CONVERT;

/**
 * UnZip 클래스는 ZIP 파일을 해제하는 기능을 제공하는 클래스입니다.
 */
public class UnZip {

    /**
     * ZIP 파일을 해제하고, 해제된 파일들이 저장된 경로를 반환하는 메서드
     *
     * @param zipFilePath 압축 파일 경로
     * @param outputDir   압축 해제할 디렉토리
     */
    public static void unzipFile(String zipFilePath, String folderName, String outputDir) {
        // 고유한 폴더명 생성 (타임스탬프 + UUID 6자리)
        String baseDir = System.getProperty("user.dir") + "/source/"; // 현재 작업 디렉토리 + /source/
        Path outputPath = Paths.get(baseDir + outputDir, folderName);

        try {
            // 최상위 폴더 생성
            Files.createDirectories(outputPath);

            try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFilePath), Charset.forName("UTF-8"))) {
                ZipEntry zipEntry;

                // 압축 파일의 각 항목 처리
                while ((zipEntry = zis.getNextEntry()) != null) {
                    Path entryDestination = outputPath.resolve(zipEntry.getName());

                    if (zipEntry.isDirectory()) {
                        Files.createDirectories(entryDestination);
                    } else {
                        Files.createDirectories(entryDestination.getParent());
                        try (FileOutputStream fos = new FileOutputStream(entryDestination.toFile())) {
                            byte[] buffer = new byte[4096];
                            int length;
                            while ((length = zis.read(buffer)) > 0) {
                                fos.write(buffer, 0, length);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new BaseException(FILE_CONVERT);
        }
    }


}