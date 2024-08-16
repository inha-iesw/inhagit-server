package inha.git.project.api.controller;

import inha.git.common.BaseResponse;
import inha.git.project.api.controller.api.request.CreateCommentRequest;
import inha.git.project.api.controller.api.request.UpdateCommentRequest;
import inha.git.project.api.controller.api.response.CreateCommentResponse;
import inha.git.project.api.controller.api.response.DeleteCommentResponse;
import inha.git.project.api.controller.api.response.UpdateCommentResponse;
import inha.git.project.api.service.ProjectCommentService;
import inha.git.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 프로젝트 댓글 생성 API
     *
     * @param user                사용자 정보
     * @param createCommentRequest 댓글 생성 요청
     * @return BaseResponse<CreateCommentResponse>
     */
    @PostMapping
    @Operation(summary = "프로젝트 댓글 생성 API", description = "프로젝트 댓글을 생성합니다.")
    public BaseResponse<CreateCommentResponse> createComment(
            @AuthenticationPrincipal User user,
            @Validated @RequestBody CreateCommentRequest createCommentRequest
    ) {
        return BaseResponse.of(PROJECT_COMMENT_CREATE_OK, projectCommentService.createComment(user, createCommentRequest));
    }

    /**
     * 프로젝트 댓글 수정 API
     *
     * @param user                사용자 정보
     * @param commentIdx          댓글 식별자
     * @param updateCommentRequest 댓글 수정 요청
     * @return BaseResponse<UpdateCommentResponse>
     */
    @PutMapping("/{commentIdx}")
    @Operation(summary = "프로젝트 댓글 수정 API", description = "프로젝트 댓글을 수정합니다.")
    public BaseResponse<UpdateCommentResponse> updateComment(
            @AuthenticationPrincipal User user,
            @PathVariable("commentIdx") Integer commentIdx,
            @Validated @RequestBody UpdateCommentRequest updateCommentRequest
    ) {
        return BaseResponse.of(PROJECT_COMMENT_UPDATE_OK, projectCommentService.updateComment(user, commentIdx, updateCommentRequest));
    }

    /**
     * 프로젝트 댓글 삭제 API
     *
     * @param user       사용자 정보
     * @param commentIdx 댓글 식별자
     * @return BaseResponse<DeleteCommentResponse>
     */
    @DeleteMapping("/{commentIdx}")
    @Operation(summary = "프로젝트 댓글 삭제 API", description = "프로젝트 댓글을 삭제합니다.")
    public BaseResponse<DeleteCommentResponse> deleteComment(
            @AuthenticationPrincipal User user,
            @PathVariable("commentIdx") Integer commentIdx) {
        return BaseResponse.of(PROJECT_COMMENT_DELETE_OK, projectCommentService.deleteComment(user, commentIdx));
    }
}
