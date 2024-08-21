package inha.git.team.api.controller;

import inha.git.common.BaseResponse;
import inha.git.team.api.controller.dto.request.CreateCommentRequest;
import inha.git.team.api.controller.dto.response.TeamCommentResponse;
import inha.git.team.api.service.TeamCommentService;
import inha.git.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static inha.git.common.code.status.SuccessStatus.TEAM_COMMENT_CREATE_OK;
import static inha.git.common.code.status.SuccessStatus.TEAM_COMMENT_UPDATE_OK;

@Slf4j
@Tag(name = "team comment controller", description = "team comment 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/teams/comments")
public class TeamCommentController {

    private final TeamCommentService teamCommentService;

    /**
     * 팀 게시글 댓글 생성 API
     *
     * @param user 사용자 정보
     * @param createCommentRequest 댓글 생성 요청
     * @return BaseResponse<CommentResponse>
     */
    @PostMapping
    @Operation(summary = "팀 게시글 댓글 생성 API", description = "팀 게시글 댓글을 생성합니다.")
    public BaseResponse<TeamCommentResponse> createComment(
            @AuthenticationPrincipal User user,
            @Validated @RequestBody CreateCommentRequest createCommentRequest) {
        return BaseResponse.of(TEAM_COMMENT_CREATE_OK, teamCommentService.createComment(user, createCommentRequest));
    }

    /**
     * 팀 게시글 댓글 수정 API
     *
     * @param user 사용자 정보
     * @param commentIdx 댓글 식별자
     * @param createCommentRequest 댓글 수정 요청
     * @return BaseResponse<CommentResponse>
     */
    @PutMapping("/{commentIdx}")
    @Operation(summary = "팀 게시글 댓글 수정 API", description = "팀 게시글 댓글을 수정합니다.")
    public BaseResponse<TeamCommentResponse> updateComment(
            @AuthenticationPrincipal User user,
            @PathVariable("commentIdx") Integer commentIdx,
            @Validated @RequestBody CreateCommentRequest createCommentRequest) {
        return BaseResponse.of(TEAM_COMMENT_UPDATE_OK, teamCommentService.updateComment(user, commentIdx, createCommentRequest));
    }

}
