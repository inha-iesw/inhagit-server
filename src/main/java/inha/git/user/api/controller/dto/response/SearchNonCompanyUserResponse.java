package inha.git.user.api.controller.dto.response;

import inha.git.admin.api.controller.dto.response.SearchDepartmentResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record SearchNonCompanyUserResponse(

        @NotNull
        @Schema(description = "유저 인덱스", example = "1")
        Integer idx,

        @NotNull
        @Schema(description = "유저 이메일", example = "test@inha.ac.kr")
        String email,

        @NotNull
        @Schema(description = "유저 이름", example = "홍길동")
        String name,

        @NotNull
        @Schema(description = "유저 포지션", example = "1")
        Integer position,

        @NotNull
        @Schema(description = "유저 학번", example = "12194118")
        String userNumber,

        @NotNull
        @Schema(description = "깃허브 토큰 등록 유무", example = "true")
        Boolean githubTokenState,

        @NotNull
        List<SearchDepartmentResponse> departmentList,

        @NotNull
        @Schema(description = "프로젝트 수", example = "1")
        Integer projectNumber,

        @NotNull
        @Schema(description = "멘토링 참여 횟수", example = "1")
        Integer questionCommentNumber,

        @NotNull
        @Schema(description = "소속 팀 수", example = "1")
        Integer belongTeamNumber
) implements SearchUserResponse{ }
