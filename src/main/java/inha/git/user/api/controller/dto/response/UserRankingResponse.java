package inha.git.user.api.controller.dto.response;

import inha.git.user.domain.UserRanking;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record UserRankingResponse(
        @NotNull
        @Schema(description = "유저 인덱스", example = "1")
        Integer userId,

        @NotNull
        @Schema(description = "유저 이름", example = "홍길동")
        String name,

        @Schema(description = "학과", example = "컴퓨터공학과")
        String departmentName,

        @Schema(description = "관리자 점수", example = "100")
        Integer adminScore
) {
        public static UserRankingResponse from(UserRanking ranking) {
                return new UserRankingResponse(
                        ranking.getUser().getId(),
                        ranking.getUser().getName(),
                        ranking.getUser().getMajor(),
                        ranking.getAdminScore()
                );
        }
}