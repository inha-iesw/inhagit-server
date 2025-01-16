package inha.git.team.api.controller.dto.request;

import inha.git.common.validation.annotation.TeamNameLanguage;
import inha.git.common.validation.annotation.UniqueTeamName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
public record CreateTeamRequest(

        @NotNull
        @UniqueTeamName
        @TeamNameLanguage
        @Schema(description = "팀 이름", example = "팀 이름")
        String name,

        @NotNull
        @Min(value = 1, message = "팀 멤버 수는 1명 이상이어야 합니다.")
        @Max(value = 128, message = "팀 멤버 수는 128 이하로 입력해주세요.")
        @Schema(description = "팀 멤버 수", example = "10")
        Integer maxMember
) {
}
