package inha.git.question.api.controller;

import inha.git.common.BaseResponse;
import inha.git.question.api.controller.dto.request.CreateCommentRequest;
import inha.git.question.api.controller.dto.request.CreateReplyCommentRequest;
import inha.git.question.api.controller.dto.request.UpdateCommentRequest;
import inha.git.question.api.controller.dto.response.CommentResponse;
import inha.git.question.api.controller.dto.response.ReplyCommentResponse;
import inha.git.question.api.service.QuestionCommentService;
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
@Tag(name = "question comment controller", description = "question comment 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/questions/comments")
public class QuestionCommentController {

    private final QuestionCommentService questionCommentService;

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
     * @param commentIdx           댓글 idx
     * @param updateCommentRequest 댓글 수정 요청
     * @return BaseResponse<CommentResponse>
     */
    @PutMapping("/reply/{commentIdx}")
    @Operation(summary = "질문 댓글 답글 수정 API", description = "질문 댓글에 답글을 수정합니다.")
    public BaseResponse<ReplyCommentResponse> updateReplyComment(
            @AuthenticationPrincipal User user,
            @PathVariable("commentIdx") Integer commentIdx,
            @Validated @RequestBody UpdateCommentRequest updateCommentRequest) {
        return BaseResponse.of(QUESTION_COMMENT_REPLY_UPDATE_OK, questionCommentService.updateReplyComment(user, commentIdx, updateCommentRequest));
    }
}
