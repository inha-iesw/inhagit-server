package inha.git.admin.api.controller.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import inha.git.common.validation.annotation.ValidName;
import inha.git.user.domain.Professor;
import inha.git.user.domain.User;
import inha.git.utils.EmailMapperUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

import static inha.git.common.Constant.mapRoleToPosition;

public record SearchProfessorResponse(
        @NotNull
        @Schema(description = "유저 아이디", example = "1")
        Integer idx,

        @NotNull
        @Schema(description = "유저 이메일", example = "ghkdrbgur13@inha.edu")
        String email,

        @NotEmpty
        @ValidName
        @Schema(description = "이름", example = "홍길동")
        String name,

        @NotNull
        @Schema(description = "직책", example = "2")
        Integer position,
        @NotNull

        @Schema(description = "차단 유무", example = "false")
        Boolean isBlocked,
        @NotNull
        @Schema(description = "교수 계정 생성일", example = "2024-05-31 04:26:56.831000 +00:00")
        LocalDateTime createdAt,
        @NotNull
        @Schema(description = "학과 목록", example = "[{\"departmentId\":1,\"departmentName\":\"컴퓨터공학과\"}]")
        List<SearchDepartmentResponse> departmentList,

        @Schema(description = "교수 계정 승인일", example = "2024-05-31 04:26:56.831000 +00:00")
        LocalDateTime acceptedAt,

        @Schema(description = "신고당한 횟수", example = "0")
        Integer reportCount
) {
    @QueryProjection
    public SearchProfessorResponse(User user, Professor professor) {
        this(
                user.getId(),
                EmailMapperUtil.maskEmail(user.getEmail()),
                user.getName(),
                mapRoleToPosition(user.getRole()),
                user.getBlockedAt() != null,
                user.getCreatedAt(),
                user.getUserDepartments().stream()
                        .map(ud -> new SearchDepartmentResponse(ud.getDepartment().getId(), ud.getDepartment().getName()))
                        .toList(),
                professor != null ? professor.getAcceptedAt() : null,
                user.getReportCount()
        );
    }



}
