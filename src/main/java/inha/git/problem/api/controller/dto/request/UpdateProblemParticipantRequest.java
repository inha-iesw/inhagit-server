package inha.git.problem.api.controller.dto.request;

import inha.git.common.validation.annotation.ValidEmail;
import inha.git.common.validation.annotation.ValidName;
import inha.git.common.validation.annotation.ValidParameter;
import inha.git.common.validation.annotation.ValidUserNumber;
import inha.git.problem.domain.enums.Grade;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record UpdateProblemParticipantRequest(

        @NotNull
        @Schema(description = "유저 이름", example = "홍길동 수정")
        @ValidName
        @ValidParameter
        String name,

        @NotNull
        @Schema(description = "팀장 여부", example = "true")
        Boolean isLeader,

        @NotNull
        @ValidUserNumber
        @ValidParameter
        @Schema(description = "학번", example = "12241234")
        String userNumber,

        @Email
        @ValidEmail
        @ValidParameter
        @Schema(description = "유저 이메일", example = "test@gmail.com")
        String email,

        @NotNull
        @Schema(description = "학년", example = "SECOND")
        Grade grade,

        @NotNull
        @Schema(description = "학과 ID", example = "1")
        Integer departmentId
) {
}
