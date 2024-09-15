package inha.git.question.api.controller;

import inha.git.common.BaseResponse;
import inha.git.question.api.controller.dto.request.*;
import inha.git.question.api.controller.dto.response.CommentResponse;
import inha.git.question.api.controller.dto.response.ReplyCommentResponse;
import inha.git.question.api.service.QuestionCommentService;
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

@Slf4j
@Tag(name = "question comment controller", description = "question comment 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/questions/comments")
public class QuestionCommentController {

    private final QuestionCommentService questionCommentService;

    /**
     * 특정 질문 댓글 전체 조회 API
     *
     * @param questionIdx 질문 idx
     * @return BaseResponse<List<CommentWithRepliesResponse>>
     */
    @GetMapping
    @Operation(summary = "특정 질문 댓글 전체 조회 API", description = "특정 질문의 모든 댓글과 대댓글을 조회합니다.")
    public BaseResponse<List<CommentWithRepliesResponse>> getAllComments(
            @RequestParam("questionIdx") Integer questionIdx) {
        return BaseResponse.of(QUESTION_COMMENT_SEARCH_OK, questionCommentService.getAllCommentsByQuestionIdx(questionIdx));
    }
    /**
     * 질문 댓글 생성 API
     *
     * @param user                사용자 정보
     * @param createCommentRequest 댓글 생성 요청
     * @return BaseResponse<CreateCommentResponse>
     */
    @PostMapping
    @Operation(summary = "질문 댓글 생성 API", description = "질문 댓글을 생성합니다.")
    public BaseResponse<CommentResponse> createComment(
            @AuthenticationPrincipal User user,
            @Validated @RequestBody CreateCommentRequest createCommentRequest
    ) {
        return BaseResponse.of(QUESTION_COMMENT_CREATE_OK, questionCommentService.createComment(user, createCommentRequest));
    }

    /**
     * 질문 댓글 수정 API
     *
     * @param user                사용자 정보
     * @param commentIdx           댓글 idx
     * @param updateCommentRequest 댓글 수정 요청
     * @return BaseResponse<CommentResponse>
     */
    @PutMapping("/{commentIdx}")
    @Operation(summary = "질문 댓글 수정 API", description = "질문 댓글을 수정합니다.")
    public BaseResponse<CommentResponse> updateComment(
            @AuthenticationPrincipal User user,
            @PathVariable("commentIdx") Integer commentIdx,
            @Validated @RequestBody UpdateCommentRequest updateCommentRequest) {
        return BaseResponse.of(QUESTION_COMMENT_UPDATE_OK, questionCommentService.updateComment(user, commentIdx, updateCommentRequest));
    }

    /**
     * 질문 댓글 삭제 API
     *
     * @param user      사용자 정보
     * @param commentIdx 댓글 idx
     * @return BaseResponse<CommentResponse>
     */
    @DeleteMapping("/{commentIdx}")
    @Operation(summary = "질문 댓글 삭제 API", description = "질문 댓글을 삭제합니다.")
    public BaseResponse<CommentResponse> deleteComment(
            @AuthenticationPrincipal User user,
            @PathVariable("commentIdx") Integer commentIdx) {
        return BaseResponse.of(QUESTION_COMMENT_DELETE_OK, questionCommentService.deleteComment(user, commentIdx));
    }

    /**
     * 질문 댓글 답글 생성 API
     *
     * @param user                사용자 정보
     * @param createReplyCommentRequest 댓글 생성 요청
     * @return BaseResponse<ReplyCommentResponse>
     */
    @PostMapping("/reply")
    @Operation(summary = "질문 댓글 답글 생성 API", description = "질문 댓글에 답글을 생성합니다.")
    public BaseResponse<ReplyCommentResponse> createReplyComment(
            @AuthenticationPrincipal User user,
            @Validated @RequestBody CreateReplyCommentRequest createReplyCommentRequest) {
        return BaseResponse.of(QUESTION_COMMENT_REPLY_CREATE_OK, questionCommentService.createReplyComment(user, createReplyCommentRequest));
    }

    /**
     * 질문 댓글 답글 수정 API
     *
     * @param user                사용자 정보
     * @param replyCommentIdx           댓글 idx
     * @param updateCommentRequest 댓글 수정 요청
     * @return BaseResponse<CommentResponse>
     */
    @PutMapping("/reply/{replyCommentIdx}")
    @Operation(summary = "질문 댓글 답글 수정 API", description = "질문 댓글에 답글을 수정합니다.")
    public BaseResponse<ReplyCommentResponse> updateReplyComment(
            @AuthenticationPrincipal User user,
            @PathVariable("replyCommentIdx") Integer replyCommentIdx,
            @Validated @RequestBody UpdateCommentRequest updateCommentRequest) {
        return BaseResponse.of(QUESTION_COMMENT_REPLY_UPDATE_OK, questionCommentService.updateReplyComment(user, replyCommentIdx, updateCommentRequest));
    }

    /**
     * 질문 댓글 답글 삭제 API
     *
     * @param user      사용자 정보
     * @param replyCommentIdx 댓글 idx
     * @return BaseResponse<CommentResponse>
     */
    @DeleteMapping("/reply/{replyCommentIdx}")
    @Operation(summary = "질문 댓글 답글 삭제 API", description = "질문 댓글에 답글을 삭제합니다.")
    public BaseResponse<ReplyCommentResponse> deleteReplyComment(
            @AuthenticationPrincipal User user,
            @PathVariable("replyCommentIdx") Integer replyCommentIdx) {
        return BaseResponse.of(QUESTION_COMMENT_REPLY_DELETE_OK, questionCommentService.deleteReplyComment(user, replyCommentIdx));
    }


    /**
     * 질문 댓글 좋아요 API
     *
     * @param user 사용자 정보
     * @param commentLikeRequest 댓글 좋아요 요청
     * @return BaseResponse<String>
     */
    @PostMapping("/like")
    @Operation(summary = "질문 댓글 좋아요 API", description = "특정 질문 댓글에 좋아요를 합니다.")
    public BaseResponse<String> questionCommentLike(@AuthenticationPrincipal User user,
                                                   @RequestBody @Valid CommentLikeRequest commentLikeRequest) {
        return BaseResponse.of(LIKE_SUCCESS, questionCommentService.questionCommentLike(user,commentLikeRequest));
    }

    /**
     * 질문 댓글 좋아요 취소 API
     *
     * @param user 사용자 정보
     * @param commentLikeRequest 댓글 좋아요 취소 요청
     * @return BaseResponse<String>
     */
    @DeleteMapping("/like")
    @Operation(summary = "질문 댓글 좋아요 취소 API", description = "특정 질문 댓글에 좋아요를 취소합니다.")
    public BaseResponse<String> questionCommentLikeCancel(@AuthenticationPrincipal User user,
                                                         @RequestBody @Valid CommentLikeRequest commentLikeRequest) {
        return BaseResponse.of(LIKE_CANCEL_SUCCESS, questionCommentService.questionCommentLikeCancel(user, commentLikeRequest));
    }

    /**
     * 질문 대댓글 좋아요 API
     *
     * @param user 사용자 정보
     * @param commentLikeRequest 댓글 좋아요 요청
     * @return BaseResponse<String>
     */
    @PostMapping("/reply/like")
    @Operation(summary = "질문 대댓글 좋아요 API", description = "특정 질문 대댓글에 좋아요를 합니다.")
    public BaseResponse<String> questionReplyCommentLike(@AuthenticationPrincipal User user,
                                                        @RequestBody @Valid CommentLikeRequest commentLikeRequest) {
        return BaseResponse.of(LIKE_SUCCESS, questionCommentService.questionReplyCommentLike(user,commentLikeRequest));
    }

    /**
     * 질문 대댓글 좋아요 취소 API
     *
     * @param user 사용자 정보
     * @param commentLikeRequest 댓글 좋아요 취소 요청
     * @return BaseResponse<String>
     */
    @DeleteMapping("/reply/like")
    @Operation(summary = "질문 대댓글 좋아요 취소 API", description = "특정 질문 대댓글에 좋아요를 취소합니다.")
    public BaseResponse<String> questionReplyCommentLikeCancel(@AuthenticationPrincipal User user,
                                                              @RequestBody @Valid CommentLikeRequest commentLikeRequest) {
        return BaseResponse.of(LIKE_CANCEL_SUCCESS, questionCommentService.questionReplyCommentLikeCancel(user,commentLikeRequest));
    }
}
