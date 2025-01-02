package inha.git.project.api.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;


public record SearchFileDetailResponse(
        @NotNull
        @Schema(description = "파일 이름", example = "README.md")
        String name,

        @NotNull
        @Schema(description = "파일 타입", example = "file")
        String type,

        @Schema(description = "파일 내용")
        String contents
) implements SearchFileResponse {

        @Override
        public String getType() {
                return "file";
        }

        public SearchFileDetailResponse(String name, String contents) {
                this(name, "file", contents);  // type을 "file"로 설정
        }
        @Override
        public String getName() {
                return name;
        }



}