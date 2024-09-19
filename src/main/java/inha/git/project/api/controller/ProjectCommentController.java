package inha.git.project.api.controller;

import inha.git.common.BaseResponse;
import inha.git.project.api.controller.dto.request.CommentLikeRequest;
import inha.git.project.api.controller.dto.request.CreateCommentRequest;
import inha.git.project.api.controller.dto.request.CreateReplyCommentRequest;
import inha.git.project.api.controller.dto.request.UpdateCommentRequest;
import inha.git.project.api.controller.dto.response.CommentResponse;
import inha.git.project.api.controller.dto.response.CommentWithRepliesResponse;
import inha.git.project.api.controller.dto.response.ReplyCommentResponse;
import inha.git.project.api.service.ProjectCommentService;
import inha.git.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static inha.git.common.code.status.SuccessStatus.*;

/**
 * ProjectController는 project 댓글 관련 엔드포인트를 처리.
 */
@Slf4j
@Tag(name = "project comment controller", description = "project comment 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/projects/comments")
public class ProjectCommentController {

    private final ProjectCommentService projectCommentService;

    @GetMapping
    @Operation(summary = "특정 프로젝트 댓글 전체 조회 API", description = "특정 프로젝트의 모든 댓글과 대댓글을 조회합니다.")
    public BaseResponse<List<CommentWithRepliesResponse>> getAllComments(
            @AuthenticationPrincipal User user,
            @RequestParam("projectIdx") Integer projectIdx) {
        return BaseResponse.of(PROJECT_COMMENT_SEARCH_OK, projectCommentService.getAllCommentsByProjectIdx(user, projectIdx));
    }

    /**
     * 프로젝트 댓글 생성 API
     *
     * @param user                사용자 정보
     * @param createCommentRequest 댓글 생성 요청
     * @return BaseResponse<CreateCommentResponse>
     */
    @PostMapping
    @Operation(summary = "프로젝트 댓글 생성 API", description = "프로젝트 댓글을 생성합니다.")
    public BaseResponse<CommentResponse> createComment(
            @AuthenticationPrincipal User user,
            @Validated @RequestBody CreateCommentRequest createCommentRequest) {
        log.info("프로젝트 댓글 생성 - 사용자: {} 프로젝트 댓글 내용: {}", user.getName(), createCommentRequest.contents());
        return BaseResponse.of(PROJECT_COMMENT_CREATE_OK, projectCommentService.createComment(user, createCommentRequest));
    }

    /**
     * 프로젝트 댓글 수정 API
     *
     * @param user                사용자 정보
     * @param commentIdx          댓글 식별자
     * @param updateCommentRequest 댓글 수정 요청
     * @return BaseResponse<CommentResponse>
     */
    @PutMapping("/{commentIdx}")
    @Operation(summary = "프로젝트 댓글 수정 API", description = "프로젝트 댓글을 수정합니다.")
    public BaseResponse<CommentResponse> updateComment(
            @AuthenticationPrincipal User user,
            @PathVariable("commentIdx") Integer commentIdx,
            @Validated @RequestBody UpdateCommentRequest updateCommentRequest) {
        log.info("프로젝트 댓글 수정 - 사용자: {} 프로젝트 댓글 내용: {}", user.getName(), updateCommentRequest.contents());
        return BaseResponse.of(PROJECT_COMMENT_UPDATE_OK, projectCommentService.updateComment(user, commentIdx, updateCommentRequest));
    }

    /**
     * 프로젝트 댓글 삭제 API
     *
     * @param user       사용자 정보
     * @param commentIdx 댓글 식별자
     * @return BaseResponse<CommentResponse>
     */
    @DeleteMapping("/{commentIdx}")
    @Operation(summary = "프로젝트 댓글 삭제 API", description = "프로젝트 댓글을 삭제합니다.")
    public BaseResponse<CommentResponse> deleteComment(
            @AuthenticationPrincipal User user,
            @PathVariable("commentIdx") Integer commentIdx) {
        log.info("프로젝트 댓글 삭제 - 사용자: {} 프로젝트 댓글 식별자: {}", user.getName(), commentIdx);
        return BaseResponse.of(PROJECT_COMMENT_DELETE_OK, projectCommentService.deleteComment(user, commentIdx));
    }

    /**
     * 프로젝트 댓글 답글 생성 API
     *
     * @param user                     사용자 정보
     * @param createReplyCommentRequest 답글 생성 요청
     * @return BaseResponse<ReplyCommentResponse>
     */
    @PostMapping("/reply")
    @Operation(summary = "프로젝트 댓글 답글 생성 API", description = "프로젝트 댓글에 답글을 생성합니다.")
    public BaseResponse<ReplyCommentResponse> createReply(
            @AuthenticationPrincipal User user,
            @Validated @RequestBody CreateReplyCommentRequest createReplyCommentRequest) {
        log.info("프로젝트 댓글 답글 생성 - 사용자: {} 프로젝트 댓글 답글 내용: {}", user.getName(), createReplyCommentRequest.contents());
        return BaseResponse.of(PROJECT_COMMENT_REPLY_CREATE_OK, projectCommentService.createReply(user, createReplyCommentRequest));
    }

    /**
     * 프로젝트 댓글 답글 수정 API
     *
     * @param user                사용자 정보
     * @param replyCommentIdx     답글 식별자
     * @param updateCommentRequest 답글 수정 요청
     * @return BaseResponse<ReplyCommentResponse>
     */
    @PutMapping("/reply/{replyCommentIdx}")
    @Operation(summary = "프로젝트 댓글 답글 수정 API", description = "프로젝트 댓글에 답글을 수정합니다.")
    public BaseResponse<ReplyCommentResponse> updateReply(
            @AuthenticationPrincipal User user,
            @PathVariable("replyCommentIdx") Integer replyCommentIdx,
            @Validated @RequestBody UpdateCommentRequest updateCommentRequest) {
        log.info("프로젝트 댓글 답글 수정 - 사용자: {} 프로젝트 댓글 답글 내용: {}", user.getName(), updateCommentRequest.contents());
        return BaseResponse.of(PROJECT_COMMENT_REPLY_UPDATE_OK, projectCommentService.updateReply(user, replyCommentIdx, updateCommentRequest));
    }

    /**
     * 프로젝트 댓글 답글 삭제 API
     *
     * @param user            사용자 정보
     * @param replyCommentIdx 답글 식별자
     * @return BaseResponse<ReplyCommentResponse>
     */
    @DeleteMapping("/reply/{replyCommentIdx}")
    @Operation(summary = "프로젝트 댓글 답글 삭제 API", description = "프로젝트 댓글에 답글을 삭제합니다.")
    public BaseResponse<ReplyCommentResponse> deleteReply(
            @AuthenticationPrincipal User user,
            @PathVariable("replyCommentIdx") Integer replyCommentIdx) {
        log.info("프로젝트 댓글 답글 삭제 - 사용자: {} 프로젝트 댓글 답글 식별자: {}", user.getName(), replyCommentIdx);
        return BaseResponse.of(PROJECT_COMMENT_REPLY_DELETE_OK, projectCommentService.deleteReply(user, replyCommentIdx));
    }

    /**
     * 프로젝트 댓글 좋아요 API
     *
     * <p>특정 프로젝트 댓글에 좋아요를 합니다.</p>
     *
     * @param user 로그인한 사용자 정보
     * @param commentLikeRequest 좋아요할 프로젝트 댓글 정보
     * @return 좋아요 성공 메시지를 포함하는 BaseResponse<String>
     */
    @PostMapping("/like")
    @Operation(summary = "프로젝트 댓글 좋아요 API", description = "특정 프로젝트 댓글에 좋아요를 합니다.")
    public BaseResponse<String> projectCommentLike(@AuthenticationPrincipal User user,
                                                @RequestBody @Valid CommentLikeRequest commentLikeRequest) {
        log.info("프로젝트 댓글 좋아요 - 사용자: {} 프로젝트 댓글 식별자: {}", user.getName(), commentLikeRequest.idx());
        return BaseResponse.of(LIKE_SUCCESS, projectCommentService.projectCommentLike(user,commentLikeRequest));
    }


    /**
     * 프로젝트 댓글 좋아요 취소 API
     *
     * <p>특정 프로젝트 댓글에 좋아요를 취소합니다.</p>
     *
     * @param user 로그인한 사용자 정보
     * @param commentLikeRequest 좋아요할 프로젝트 댓글 정보
     * @return 좋아요 취소 성공 메시지를 포함하는 BaseResponse<String>
     */
    @DeleteMapping("/like")
    @Operation(summary = "프로젝트 댓글 좋아요 취소 API", description = "특정 프로젝트 댓글에 좋아요를 취소합니다.")
    public BaseResponse<String> projectCommentLikeCancel(@AuthenticationPrincipal User user,
                                                      @RequestBody @Valid CommentLikeRequest commentLikeRequest) {
        log.info("프로젝트 댓글 좋아요 취소 - 사용자: {} 프로젝트 댓글 식별자: {}", user.getName(), commentLikeRequest.idx());
        return BaseResponse.of(LIKE_CANCEL_SUCCESS, projectCommentService.projectCommentLikeCancel(user,commentLikeRequest));
    }

    /**
     * 프로젝트 대댓글 좋아요 API
     *
     * <p>특정 프로젝트 대댓글에 좋아요를 합니다.</p>
     *
     * @param user 로그인한 사용자 정보
     * @param commentLikeRequest 좋아요할 프로젝트 대댓글 정보
     * @return 좋아요 성공 메시지를 포함하는 BaseResponse<String>
     */
    @PostMapping("/reply/like")
    @Operation(summary = "프로젝트 대댓글 좋아요 API", description = "특정 프로젝트 대댓글에 좋아요를 합니다.")
    public BaseResponse<String> projectReplyCommentLike(@AuthenticationPrincipal User user,
                                                   @RequestBody @Valid CommentLikeRequest commentLikeRequest) {
        log.info("프로젝트 대댓글 좋아요 - 사용자: {} 프로젝트 대댓글 식별자: {}", user.getName(), commentLikeRequest.idx());
        return BaseResponse.of(LIKE_SUCCESS, projectCommentService.projectReplyCommentLike(user,commentLikeRequest));
    }

    /**
     * 프로젝트 대댓글 좋아요 취소 API
     *
     * <p>특정 프로젝트 대댓글에 좋아요를 취소합니다.</p>
     *
     * @param user 로그인한 사용자 정보
     * @param commentLikeRequest 좋아요할 프로젝트 대댓글 정보
     * @return 좋아요 취소 성공 메시지를 포함하는 BaseResponse<String>
     */
    @DeleteMapping("/reply/like")
    @Operation(summary = "프로젝트 대댓글 좋아요 취소 API", description = "특정 프로젝트 대댓글에 좋아요를 취소합니다.")
    public BaseResponse<String> projectReplyCommentLikeCancel(@AuthenticationPrincipal User user,
                                                         @RequestBody @Valid CommentLikeRequest commentLikeRequest) {
        log.info("프로젝트 대댓글 좋아요 취소 - 사용자: {} 프로젝트 대댓글 식별자: {}", user.getName(), commentLikeRequest.idx());
        return BaseResponse.of(LIKE_CANCEL_SUCCESS, projectCommentService.projectReplyCommentLikeCancel(user,commentLikeRequest));
    }
}
