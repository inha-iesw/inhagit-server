package inha.git.project.api.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.List;
public record SearchDirectoryResponse(

        @NotNull
        @Schema(description = "폴더 이름", example = "src")
        String name,

        @NotNull
        @Schema(description = "파일 타입", example = "directory")
        String type,

        @Schema(description = "하위 파일 목록")
        List<SearchFileResponse> fileList
) implements SearchFileResponse {

        public SearchDirectoryResponse(String name, List<SearchFileResponse> fileList) {
                this(name, "directory", fileList);  // type을 "directory"로 설정
        }

        @Override
        public String getName() {
                return name;
        }

        @Override
        public String getType() {
                return "directory";
        }
}
