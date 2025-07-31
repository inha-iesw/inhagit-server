package inha.git.project.api.controller.dto.request;

import inha.git.common.validation.annotation.ValidEmail;
import inha.git.common.validation.annotation.ValidUserNumber;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
public record CreateProjectTeamMemberRequest (
    @NotNull
    @Schema(description = "팀원 이름", example = "팀원 이름")
    String name,

    @NotNull
    @Email
    @ValidEmail
    @Schema(description = "팀원 이메일", example = "test@gmail.com")
    String email,

    @ValidUserNumber
    @Schema(description = "팀원 학번", example = "2018000000", nullable = true)
    String userNumber,

    @Schema(description = "팀원 학과 이름", example = "컴퓨터공학과", nullable = true)
    String departmentName,

    @Schema(description = "팀원 단과대 이름", example = "소프트웨어융합대학", nullable = true)
    String collegeName,

    @Schema(description = "팀원 학과 Idx", example = "1", nullable = true)
    Integer departmentIdx,

    @Schema(description = "팀원 단과대 Idx", example = "1", nullable = true)
    Integer collegeIdx
){

}
