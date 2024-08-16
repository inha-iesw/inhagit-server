package inha.git.project.api.controller.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateProjectRequest(
        @NotNull(message = "제목을 입력해주세요.")
        @Size(min = 1, max = 32, message = "제목은 1자 이상 32자 이하로 입력해주세요.")
        @Schema(description = "프로젝트 제목", example = "프로젝트 제목")
        String title,
        @NotNull(message = "내용을 입력해주세요.")
        @Size(min = 1, message = "내용은 1자 이상 입력해주세요.")
        @Schema(description = "프로젝트 내용", example = "프로젝트 내용")
        String contents,
        @NotNull(message = "프로젝트 주제를 작성해주세요.")
        @Size(min = 1, max = 32, message = "프로젝트 주제는 1자 이상 32자 이하로 입력해주세요.")
        @Schema(description = "프로젝트 주제", example = "프로젝트 주제")
        String subject,
        @NotNull(message = "분야를 선택해주세요.")
        @Schema(description = "분야 인덱스 리스트", example = "[1, 2]")
        List<Integer>fieldIdxList
) {
}
