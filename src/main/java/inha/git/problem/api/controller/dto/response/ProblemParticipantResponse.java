package inha.git.problem.api.controller.dto.response;

import inha.git.admin.api.controller.dto.response.SearchDepartmentResponse;
import inha.git.problem.domain.enums.Grade;
import io.swagger.v3.oas.annotations.media.Schema;

public record ProblemParticipantResponse(

        @Schema(description = "문제 참여자 인덱스", example = "1")
        int idx,

        @Schema(description = "문제 참여자 이름", example = "홍길동")
        String name,

        @Schema(description = "팀장 여부", example = "true")
        boolean isLeader,

        @Schema(description = "학번", example = "20210000")
        String userNumber,

        @Schema(description = "이메일", example = "test@gmail.com")
        String email,

        @Schema(description = "학년", example = "FIRST")
        Grade grade,

        SearchDepartmentResponse department
) {
}
