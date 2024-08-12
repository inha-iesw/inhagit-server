package inha.git.utils.s3.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@AllArgsConstructor
@Builder
public class S3UploadRequest {

    private Long userId;
    private String dirName;
}
