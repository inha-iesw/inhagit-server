package inha.git.utils.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import inha.git.common.exceptions.BaseException;
import inha.git.utils.s3.dto.S3UploadRequest;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import static inha.git.common.code.status.ErrorStatus.FILE_CONVERT;
import static inha.git.common.code.status.ErrorStatus.S3_UPLOAD;


/**
 * S3Provider는 AWS S3에 파일을 업로드하는 기능을 제공.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class S3Provider {
    private final AmazonS3 amazonS3Client;
    private TransferManager transferManager;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    /**
     * TransferManager 초기화 메서드.
     * 이 메서드는 S3 클라이언트를 사용하여 TransferManager를 초기화.
     */
    @PostConstruct
    public void init() {
        this.transferManager = TransferManagerBuilder.standard()
                .withS3Client(amazonS3Client)
                .build();
    }

    /**
     * TransferManager 종료 메서드.
     * 애플리케이션 종료 시 TransferManager를 안전하게 종료.
     */
    @PreDestroy
    public void shutdown() {
        if (this.transferManager != null) {
            this.transferManager.shutdownNow();
        }
    }

    /**
     * MultipartFile을 S3에 업로드하는 메서드.
     *
     * @param file 업로드할 파일
     * @param request 업로드 요청 정보
     * @return 업로드된 파일의 S3 URL
     * @throws BaseException 파일 변환 또는 업로드 실패 시 발생
     */
    public String multipartFileUpload(MultipartFile file, S3UploadRequest request) {
        String fileName = request.getUserId() + "/" + request.getDirName() + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

        try {
            InputStream is = file.getInputStream();
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(file.getContentType());
            objectMetadata.setContentLength(file.getSize());
            Upload upload = transferManager.upload(bucket, fileName, is, objectMetadata);
            try {
                upload.waitForCompletion();
            } catch (InterruptedException e) {
                log.error("S3 multipartFileUpload error", e);
                throw new BaseException(S3_UPLOAD);
            }
        } catch (IOException e) {
            log.error("File convert error", e);
            throw new BaseException(FILE_CONVERT);
        }
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    /**
     * InputStream을 S3에 업로드하는 메서드.
     *
     * @param inputStream 업로드할 InputStream
     * @param fileName 파일 이름
     * @param contentLength 파일 크기
     * @param contentType 파일 콘텐츠 타입
     * @param request 업로드 요청 정보
     * @return 업로드된 파일의 S3 URL
     * @throws BaseException 업로드 실패 시 발생
     */
    public String inputStreamUpload(InputStream inputStream, String fileName, long contentLength, String contentType, S3UploadRequest request) {
        String s3FileName = request.getUserId() + "/" + request.getDirName() + "/" + UUID.randomUUID() + "_" + fileName;
        try {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(contentType);
            objectMetadata.setContentLength(contentLength);
            Upload upload = transferManager.upload(bucket, s3FileName, inputStream, objectMetadata);
            upload.waitForCompletion();
        } catch (InterruptedException e) {
            log.error("S3 inputStreamUpload error", e);
            throw new BaseException(S3_UPLOAD);
        }
        return amazonS3Client.getUrl(bucket, s3FileName).toString();
    }
}