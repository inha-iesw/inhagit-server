package inha.git.project.api.controller;

import inha.git.common.BaseResponse;
import inha.git.project.api.controller.api.request.CreateCommentRequest;
import inha.git.project.api.controller.api.response.CreateCommentResponse;
import inha.git.project.api.service.ProjectCommentService;
import inha.git.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static inha.git.common.code.status.SuccessStatus.PROJECT_COMMENT_CREATE_OK;

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

    @PostMapping
    @Operation(summary = "프로젝트 댓글 생성 API", description = "프로젝트 댓글을 생성합니다.")
    public BaseResponse<CreateCommentResponse> createComment(
            @AuthenticationPrincipal User user,
            @Validated @RequestBody CreateCommentRequest createCommentRequest
    ) {
        return BaseResponse.of(PROJECT_COMMENT_CREATE_OK, projectCommentService.createComment(user, createCommentRequest));
    }

}
