package inha.git.project.api.service.comment.reply;

import inha.git.common.exceptions.BaseException;
import inha.git.project.api.controller.dto.request.CreateReplyCommentRequest;
import inha.git.project.api.controller.dto.request.UpdateCommentRequest;
import inha.git.project.api.controller.dto.response.ReplyCommentResponse;
import inha.git.project.api.mapper.ProjectMapper;
import inha.git.project.domain.Project;
import inha.git.project.domain.ProjectComment;
import inha.git.project.domain.ProjectReplyComment;
import inha.git.project.domain.repository.ProjectCommentJpaRepository;
import inha.git.project.domain.repository.ProjectReplyCommentJpaRepository;
import inha.git.user.domain.User;
import inha.git.user.domain.enums.Role;
import inha.git.utils.IdempotentProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static inha.git.common.BaseEntity.State.ACTIVE;
import static inha.git.common.BaseEntity.State.INACTIVE;
import static inha.git.common.Constant.hasAccessToProject;
import static inha.git.common.code.status.ErrorStatus.PROJECT_COMMENT_NOT_FOUND;
import static inha.git.common.code.status.ErrorStatus.PROJECT_COMMENT_REPLY_NOT_FOUND;
import static inha.git.common.code.status.ErrorStatus.PROJECT_COMMENT_REPLY_UPDATE_NOT_AUTHORIZED;
import static inha.git.common.code.status.ErrorStatus.PROJECT_NOT_PUBLIC;

/**
 * ProjectReplyCommentCommandServiceImpl은 프로젝트 대댓글 관련 비즈니스 로직을 처리합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProjectReplyCommentCommandServiceImpl implements ProjectReplyCommentCommandService {

    private final ProjectCommentJpaRepository projectCommentJpaRepository;
    private final ProjectReplyCommentJpaRepository projectReplyCommentJpaRepository;
    private final ProjectMapper projectMapper;
    private final IdempotentProvider idempotentProvider;

    /**
     * 답글 생성
     *
     * @param user                     사용자 정보
     * @param createReplyCommentRequest 답글 생성 요청
     * @return CreateReplyCommentResponse
     */
    @Override
    public ReplyCommentResponse createReply(User user, CreateReplyCommentRequest createReplyCommentRequest) {
        idempotentProvider.isValidIdempotent(List.of("createReplyCommentRequest", user.getId().toString(), user.getName(), createReplyCommentRequest.contents()));

        ProjectComment projectComment = projectCommentJpaRepository.findByIdAndState(createReplyCommentRequest.commentIdx(), ACTIVE)
                .orElseThrow(() -> new BaseException(PROJECT_COMMENT_NOT_FOUND));
        Project project = projectComment.getProject();

        if (!hasAccessToProject(project, user)) {
            throw new BaseException(PROJECT_NOT_PUBLIC);
        }

        ProjectReplyComment projectReplyComment = projectMapper.toProjectReplyComment(createReplyCommentRequest, user, projectComment);
        projectReplyCommentJpaRepository.save(projectReplyComment);

        project.increaseCommentCount();

        log.info("프로젝트 대댓글 생성 성공 - 사용자: {} 프로젝트 대댓글 내용: {}", user.getName(), createReplyCommentRequest.contents());
        return projectMapper.toReplyCommentResponse(projectReplyComment);
    }

    /**
     * 답글 수정
     *
     * @param user                사용자 정보
     * @param replyCommentIdx      답글 식별자
     * @param updateCommentRequest 답글 수정 요청
     * @return UpdateCommentResponse
     */
    @Override
    public ReplyCommentResponse updateReply(User user, Integer replyCommentIdx, UpdateCommentRequest updateCommentRequest) {
        ProjectReplyComment projectReplyComment = projectReplyCommentJpaRepository.findByIdAndState(replyCommentIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(PROJECT_COMMENT_REPLY_NOT_FOUND));
        if(!projectReplyComment.getUser().getId().equals(user.getId())) {
            log.error("프로젝트 대댓글 수정 실패 - 사용자: {} 권한이 없습니다.", user.getName());
            throw new BaseException(PROJECT_COMMENT_REPLY_UPDATE_NOT_AUTHORIZED);
        }
        projectReplyComment.setContents(updateCommentRequest.contents());
        projectReplyCommentJpaRepository.save(projectReplyComment);
        log.info("프로젝트 대댓글 수정 성공 - 사용자: {} 프로젝트 대댓글 내용: {}", user.getName(), updateCommentRequest.contents());
        return projectMapper.toReplyCommentResponse(projectReplyComment);
    }

    /**
     * 답글 삭제
     *
     * @param user            사용자 정보
     * @param replyCommentIdx 답글 식별자
     * @return DeleteCommentResponse
     */
    @Override
    public ReplyCommentResponse deleteReply(User user, Integer replyCommentIdx) {
        ProjectReplyComment projectReplyComment = projectReplyCommentJpaRepository.findByIdAndState(replyCommentIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(PROJECT_COMMENT_REPLY_NOT_FOUND));
        if(!projectReplyComment.getUser().getId().equals(user.getId()) && user.getRole() != Role.ADMIN) {
            log.error("프로젝트 대댓글 삭제 실패 - 사용자: {} 권한이 없습니다.", user.getName());
            throw new BaseException(PROJECT_COMMENT_REPLY_UPDATE_NOT_AUTHORIZED);
        }
        projectReplyComment.setDeletedAt();
        projectReplyComment.setState(INACTIVE);
        projectReplyCommentJpaRepository.save(projectReplyComment);

        Project project = projectReplyComment.getProjectComment().getProject();
        project.decreaseCommentCount();

        log.info("프로젝트 대댓글 삭제 성공 - 사용자: {} 프로젝트 대댓글 내용: {}", user.getName(), projectReplyComment.getContents());
        return projectMapper.toReplyCommentResponse(projectReplyComment);
    }
}
