package inha.git.admin.api.controller.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import inha.git.common.validation.annotation.ValidName;
import inha.git.user.domain.Company;
import inha.git.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

import static inha.git.common.Constant.mapRoleToPosition;

public record SearchCompanyResponse(
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
        @Schema(description = "교수 계정 생성일", example = "2024-05-31 04:26:56.831000 +00:00")
        LocalDateTime createdAt,
        @NotNull
        @Schema(description = "소속", example = "IESW")
        String affiliation,

        @NotNull
        @Schema(description = "증빙파일", example = "/file/user/8/evidence")
        String evidence,
        @Schema(description = "기업 계정 승인일", example = "2024-05-31 04:26:56.831000 +00:00")
        LocalDateTime acceptedAt
) {
    @QueryProjection
    public SearchCompanyResponse(User user, Company company) {
        this(
                user.getId(),
                user.getEmail(),
                user.getName(),
                mapRoleToPosition(user.getRole()),
                user.getCreatedAt(),
                company.getAffiliation(),
                "/file/user/" + user.getId() + "/evidence",
                company != null ? company.getAcceptedAt() : null

        );
    }



}
