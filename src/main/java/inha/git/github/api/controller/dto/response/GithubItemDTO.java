package inha.git.github.api.controller.dto.response;

import lombok.Data;

@Data
public class GithubItemDTO {
    private String name;
    private String path;
    private String type;  // "file" or "dir"
    private String download_url;

    // Getter, Setter
}