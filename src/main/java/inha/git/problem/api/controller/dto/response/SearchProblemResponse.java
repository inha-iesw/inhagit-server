package inha.git.problem.api.controller.dto.response;

import inha.git.problem.domain.enums.ProblemStatus;
import inha.git.project.api.controller.dto.response.SearchFieldResponse;
import inha.git.project.api.controller.dto.response.SearchUserResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record SearchProblemResponse(

        @NotNull
        @Schema(description = "문제 아이디", example = "1")
        int idx,

        @NotNull
        @Schema(description = "제목", example = "공지사항 제목")
        String title,

        @NotNull
        @Schema(description = "내용", example = "공지사항 내용")
        String contents,

        @NotNull
        @Schema(description = "작성일", example = "2021-08-01T00:00:00")
        LocalDateTime createdAt,

        @NotNull
        @Schema(description = "문제 참여자 수", example = "1")
        int participantCount,

        @NotNull
        @Schema(description = "문제 상태", example = "PROGRESS")
        ProblemStatus status,

        @NotNull
        @Schema(description = "첨부파일 여부", example = "true")
        boolean hasAttachment,

        @NotNull
        @Schema(description = "문제 제출 기한", example = "2025-08-31")
        String duration,

        @Schema(description = "문제 신청 인덱스", example = "1")
        Integer problemRequestIdx,

        @NotNull
        SearchUserResponse author,

        List<SearchFieldResponse> fieldList,

        List<SearchProblemAttachmentResponse> attachments
) {
}
