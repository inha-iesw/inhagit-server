package inha.git.search.api.controller.dto.response;

import inha.git.project.api.controller.dto.response.SearchFieldResponse;
import inha.git.project.api.controller.dto.response.SearchUserResponse;
import inha.git.search.domain.enums.TableType;
import inha.git.semester.controller.dto.response.SearchSemesterResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record SearchResponse(

        @NotNull
        @Schema(description = "검색 결과 인덱스", example = "1")
        Integer idx,

        @NotNull
        @Schema(description = "검색 결과 제목", example = "검색 결과 제목")
        String title,

        @NotNull
        @Schema(description = "게시글 생성 날짜", example = "2021-07-01T00:00:00")
        LocalDateTime createdAt,

        @NotNull
        SearchUserResponse author,

        SearchSemesterResponse semester,

        @Schema(description = "과목", example = "클라우드컴퓨팅")
        String subject,

        @Schema(description = "좋아요 수", example = "1")
        Integer likeCount,

        List<SearchFieldResponse> fieldList,

        @NotNull
        @Schema(description = "게시글 타입", example = "팀")
        String tableName,

        @Schema(description = "깃허브 유무", example = "true")
        Boolean isRepo
) {
}
