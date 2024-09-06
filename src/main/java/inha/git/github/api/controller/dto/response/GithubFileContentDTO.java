package inha.git.github.api.controller.dto.response;

import lombok.Data;

@Data
public class GithubFileContentDTO {
    private String name;
    private String path;
    private String content;
    private String encoding;  // encoding 필드 추가

    // Getter와 Setter

    // 다른 필드에 대한 Getter와 Setter도 추가
}