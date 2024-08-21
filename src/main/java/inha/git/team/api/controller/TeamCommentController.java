package inha.git.team.api.controller;

import inha.git.common.BaseResponse;
import inha.git.team.api.controller.dto.request.CreateCommentRequest;
import inha.git.team.api.controller.dto.request.CreateReplyCommentRequest;
import inha.git.team.api.controller.dto.request.UpdateCommentRequest;
import inha.git.team.api.controller.dto.response.TeamCommentResponse;
import inha.git.team.api.controller.dto.response.TeamReplyCommentResponse;
import inha.git.team.api.service.TeamCommentService;
import inha.git.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static inha.git.common.code.status.SuccessStatus.*;

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
     * @param updateCommentRequest 댓글 수정 요청
     * @return BaseResponse<CommentResponse>
     */
    @PutMapping("/{commentIdx}")
    @Operation(summary = "팀 게시글 댓글 수정 API", description = "팀 게시글 댓글을 수정합니다.")
    public BaseResponse<TeamCommentResponse> updateComment(
            @AuthenticationPrincipal User user,
            @PathVariable("commentIdx") Integer commentIdx,
            @Validated @RequestBody UpdateCommentRequest updateCommentRequest) {
        return BaseResponse.of(TEAM_COMMENT_UPDATE_OK, teamCommentService.updateComment(user, commentIdx, updateCommentRequest));
    }

    /**
     * 팀 게시글 댓글 삭제 API
     *
     * @param user 사용자 정보
     * @param commentIdx 댓글 식별자
     * @return BaseResponse<TeamCommentResponse>
     */
    @DeleteMapping("/{commentIdx}")
    @Operation(summary = "팀 게시글 댓글 삭제 API", description = "팀 게시글 댓글을 삭제합니다.")
    public BaseResponse<TeamCommentResponse> deleteComment(
            @AuthenticationPrincipal User user,
            @PathVariable("commentIdx") Integer commentIdx) {
        return BaseResponse.of(TEAM_COMMENT_DELETE_OK, teamCommentService.deleteComment(user, commentIdx));
    }

    @PostMapping("/reply")
    @Operation(summary = "팀 게시글 대댓글 생성 API", description = "팀 게시글 대댓글을 생성합니다.")
    public BaseResponse<TeamReplyCommentResponse> createReplyComment(
            @AuthenticationPrincipal User user,
            @Validated @RequestBody CreateReplyCommentRequest createReplyCommentRequest) {
        return BaseResponse.of(TEAM_COMMENT_REPLY_CREATE_OK, teamCommentService.createReplyComment(user, createReplyCommentRequest));
    }

    /**
     * 팀 게시글 대댓글 수정 API
     *
     * @param user 사용자 정보
     * @param replyCommentIdx 대댓글 식별자
     * @param updateCommentRequest 대댓글 수정 요청
     * @return BaseResponse<TeamReplyCommentResponse>
     */
    @PutMapping("/reply/{replyCommentIdx}")
    @Operation(summary = "팀 게시글 대댓글 수정 API", description = "팀 게시글 대댓글을 수정합니다.")
    public BaseResponse<TeamReplyCommentResponse> updateReplyComment(
            @AuthenticationPrincipal User user,
            @PathVariable("replyCommentIdx") Integer replyCommentIdx,
            @Validated @RequestBody UpdateCommentRequest updateCommentRequest) {
        return BaseResponse.of(TEAM_COMMENT_REPLY_UPDATE_OK, teamCommentService.updateReplyComment(user, replyCommentIdx, updateCommentRequest));
    }

    /**
     * 팀 게시글 대댓글 삭제 API
     *
     * @param user 사용자 정보
     * @param replyCommentIdx 대댓글 식별자
     * @return BaseResponse<TeamReplyCommentResponse>
     */
    @DeleteMapping("/reply/{replyCommentIdx}")
    @Operation(summary = "팀 게시글 대댓글 삭제 API", description = "팀 게시글 대댓글을 삭제합니다.")
    public BaseResponse<TeamReplyCommentResponse> deleteReplyComment(
            @AuthenticationPrincipal User user,
            @PathVariable("replyCommentIdx") Integer replyCommentIdx) {
        return BaseResponse.of(TEAM_COMMENT_REPLY_DELETE_OK, teamCommentService.deleteReplyComment(user, replyCommentIdx));
    }
}
