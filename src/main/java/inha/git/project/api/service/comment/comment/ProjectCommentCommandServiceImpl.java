package inha.git.project.api.service.comment.comment;

import inha.git.common.exceptions.BaseException;
import inha.git.project.api.controller.dto.request.CreateCommentRequest;
import inha.git.project.api.controller.dto.request.UpdateCommentRequest;
import inha.git.project.api.controller.dto.response.CommentResponse;
import inha.git.project.api.mapper.ProjectMapper;
import inha.git.project.domain.Project;
import inha.git.project.domain.ProjectComment;
import inha.git.project.domain.repository.ProjectCommentJpaRepository;
import inha.git.project.domain.repository.ProjectJpaRepository;
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
import static inha.git.common.code.status.ErrorStatus.PROJECT_COMMENT_ALREADY_DELETED;
import static inha.git.common.code.status.ErrorStatus.PROJECT_COMMENT_DELETE_NOT_AUTHORIZED;
import static inha.git.common.code.status.ErrorStatus.PROJECT_COMMENT_NOT_FOUND;
import static inha.git.common.code.status.ErrorStatus.PROJECT_COMMENT_UPDATE_NOT_AUTHORIZED;
import static inha.git.common.code.status.ErrorStatus.PROJECT_NOT_FOUND;
import static inha.git.common.code.status.ErrorStatus.PROJECT_NOT_PUBLIC;

/**
 * ProjectCommentCommandServiceImpl은 프로젝트 댓글 관련 비즈니스 로직을 처리합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProjectCommentCommandServiceImpl implements ProjectCommentCommandService {

    private final ProjectJpaRepository projectJpaRepository;
    private final ProjectCommentJpaRepository projectCommentJpaRepository;
    private final ProjectReplyCommentJpaRepository projectReplyCommentJpaRepository;
    private final ProjectMapper projectMapper;
    private final IdempotentProvider idempotentProvider;

    /**
     * 댓글 생성
     *
     * @param user                사용자 정보
     * @param createCommentRequest 댓글 생성 요청
     * @return CreateCommentResponse
     */
    @Override
    public CommentResponse createComment(User user, CreateCommentRequest createCommentRequest) {

        idempotentProvider.isValidIdempotent(List.of("createCommentRequest", user.getId().toString(), user.getName(), createCommentRequest.contents()));

        Project project = projectJpaRepository.findByIdAndState(createCommentRequest.projectIdx(), ACTIVE)
                .orElseThrow(() -> new BaseException(PROJECT_NOT_FOUND));

        if (!hasAccessToProject(project, user)) {
            throw new BaseException(PROJECT_NOT_PUBLIC);
        }

        ProjectComment projectComment = projectMapper.toProjectComment(createCommentRequest, user, project);
        projectCommentJpaRepository.save(projectComment);
        project.increaseCommentCount();
        log.info("프로젝트 댓글 생성 성공 - 사용자: {} 프로젝트 댓글 내용: {}", user.getName(), createCommentRequest.contents());
        return projectMapper.toCommentResponse(projectComment);
    }

    /**
     * 댓글 수정
     *
     * @param user                사용자 정보
     * @param commentIdx          댓글 식별자
     * @param updateCommentRequest 댓글 수정 요청
     * @return UpdateCommentResponse
     */
    @Override
    public CommentResponse updateComment(User user, Integer commentIdx, UpdateCommentRequest updateCommentRequest) {
        ProjectComment projectComment = projectCommentJpaRepository.findByIdAndState(commentIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(PROJECT_COMMENT_NOT_FOUND));
        if(!projectComment.getUser().getId().equals(user.getId())) {
            log.error("프로젝트 댓글 수정 실패 - 사용자: {} 권한이 없습니다.", user.getName());
            throw new BaseException(PROJECT_COMMENT_UPDATE_NOT_AUTHORIZED);
        }
        projectComment.setContents(updateCommentRequest.contents());
        projectCommentJpaRepository.save(projectComment);
        log.info("프로젝트 댓글 수정 성공 - 사용자: {} 프로젝트 댓글 내용: {}", user.getName(), updateCommentRequest.contents());
        return projectMapper.toCommentResponse(projectComment);
    }

    /**
     * 댓글 삭제
     *
     * @param user       사용자 정보
     * @param commentIdx 댓글 식별자
     * @return DeleteCommentResponse
     */
    @Override
    public CommentResponse deleteComment(User user, Integer commentIdx) {
        ProjectComment projectComment = projectCommentJpaRepository.findByIdAndState(commentIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(PROJECT_COMMENT_NOT_FOUND));
        if(!projectComment.getUser().getId().equals(user.getId()) && user.getRole() != Role.ADMIN) {
            log.error("프로젝트 댓글 삭제 실패 - 사용자: {} 권한이 없습니다.", user.getName());
            throw new BaseException(PROJECT_COMMENT_DELETE_NOT_AUTHORIZED);
        }
        if(projectComment.getDeletedAt() != null) {
            log.error("프로젝트 댓글 삭제 실패 - 사용자: {} 이미 삭제된 댓글입니다.", user.getName());
            throw new BaseException(PROJECT_COMMENT_ALREADY_DELETED);
        }
        projectComment.setDeletedAt();
        if(projectReplyCommentJpaRepository.existsByProjectCommentAndState(projectComment, ACTIVE)) {
            projectComment.setContents("삭제된 댓글입니다.");
        }
        else {
            projectComment.setState(INACTIVE);
        }
        projectCommentJpaRepository.save(projectComment);

        Project project = projectComment.getProject();
        project.decreaseCommentCount();

        log.info("프로젝트 댓글 삭제 성공 - 사용자: {} 프로젝트 댓글 내용: {}", user.getName(), projectComment.getContents());
        return projectMapper.toCommentResponse(projectComment);
    }
}
