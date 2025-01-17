package inha.git.project.api.service.comment.like;

import inha.git.common.exceptions.BaseException;
import inha.git.mapping.domain.repository.ProjectCommentLikeJpaRepository;
import inha.git.mapping.domain.repository.ProjectReplyCommentLikeJpaRepository;
import inha.git.project.api.controller.dto.request.CommentLikeRequest;
import inha.git.project.api.mapper.ProjectMapper;
import inha.git.project.domain.ProjectComment;
import inha.git.project.domain.ProjectReplyComment;
import inha.git.project.domain.repository.ProjectCommentJpaRepository;
import inha.git.project.domain.repository.ProjectReplyCommentJpaRepository;
import inha.git.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static inha.git.common.BaseEntity.State.ACTIVE;
import static inha.git.common.Constant.hasAccessToProject;
import static inha.git.common.code.status.ErrorStatus.ALREADY_LIKE;
import static inha.git.common.code.status.ErrorStatus.ALREADY_RECOMMENDED;
import static inha.git.common.code.status.ErrorStatus.MY_COMMENT_LIKE;
import static inha.git.common.code.status.ErrorStatus.NOT_LIKE;
import static inha.git.common.code.status.ErrorStatus.PROJECT_COMMENT_NOT_FOUND;
import static inha.git.common.code.status.ErrorStatus.PROJECT_COMMENT_REPLY_NOT_FOUND;
import static inha.git.common.code.status.ErrorStatus.PROJECT_NOT_LIKE;
import static inha.git.common.code.status.ErrorStatus.PROJECT_NOT_PUBLIC;
import static inha.git.common.code.status.ErrorStatus.TEMPORARY_UNAVAILABLE;

/**
 * ProjectCommentLikeServiceImpl은 프로젝트 댓글 좋아요 관련 비즈니스 로직을 처리합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProjectCommentLikeServiceImpl implements ProjectCommentLikeService{

    private final ProjectCommentJpaRepository projectCommentJpaRepository;
    private final ProjectReplyCommentJpaRepository projectReplyCommentJpaRepository;
    private final ProjectCommentLikeJpaRepository projectCommentLikeJpaRepository;
    private final ProjectReplyCommentLikeJpaRepository projectReplyCommentLikeJpaRepository;
    private final ProjectMapper projectMapper;

    /**
     * 댓글 좋아요
     *
     * @param user 사용자 정보
     * @param commentLikeRequest 댓글 좋아요 정보
     * @return 댓글 좋아요 완료 메시지
     */
    @Override
    public String projectCommentLike(User user, CommentLikeRequest commentLikeRequest) {
        ProjectComment projectComment = getProjectComment(user, commentLikeRequest);
        try {
            validLike(projectComment, user, projectCommentLikeJpaRepository.existsByUserAndProjectComment(user, projectComment));
            projectCommentLikeJpaRepository.save(projectMapper.createProjectCommentLike(user, projectComment));
            projectComment.setLikeCount(projectComment.getLikeCount() + 1);
            log.info("프로젝트 댓글 좋아요 완료 - 사용자: {} 프로젝트 댓글 식별자: {} 좋아요 개수: {}", user.getName(), commentLikeRequest.idx(), projectComment.getLikeCount());
            return commentLikeRequest.idx() + "번 프로젝트 댓글 좋아요 완료";
        }catch(DataIntegrityViolationException e) {
            log.error("프로젝트 댓글 좋아요 중복 발생 - 사용자: {}, 프로젝트 ID: {}", user.getName(), commentLikeRequest.idx());
            throw new BaseException(ALREADY_RECOMMENDED);
        }
    }

    /**
     * 댓글 좋아요 취소
     *
     * @param user 사용자 정보
     * @param commentLikeRequest 댓글 좋아요 정보
     * @return 댓글 좋아요 취소 완료 메시지
     */
    @Override
    public String projectCommentLikeCancel(User user, CommentLikeRequest commentLikeRequest) {
        ProjectComment projectComment = getProjectComment(user, commentLikeRequest);
        try {
            validLikeCancel(projectComment, user, projectCommentLikeJpaRepository.existsByUserAndProjectComment(user, projectComment));
            projectCommentLikeJpaRepository.deleteByUserAndProjectComment(user, projectComment);
            if (projectComment.getLikeCount() <= 0) {
                projectComment.setLikeCount(0);
            }
            projectComment.setLikeCount(projectComment.getLikeCount() - 1);
            log.info("프로젝트 댓글 좋아요 취소 완료 - 사용자: {} 프로젝트 댓글 식별자: {} 좋아요 개수: {}", user.getName(), commentLikeRequest.idx(), projectComment.getLikeCount());
            return commentLikeRequest.idx() + "번 프로젝트 댓글 좋아요 취소 완료";
        }catch (DataIntegrityViolationException e) {
            log.error("프로젝트 댓글 좋아요 취소 중복 발생 - 사용자: {}, 프로젝트 ID: {}", user.getName(), commentLikeRequest.idx());
            throw new BaseException(PROJECT_NOT_LIKE);
        }
    }

    /**
     * 대댓글 좋아요
     *
     * @param user 사용자 정보
     * @param commentLikeRequest 대댓글 좋아요 정보
     * @return 대댓글 좋아요 완료 메시지
     */
    @Override
    public String projectReplyCommentLike(User user, CommentLikeRequest commentLikeRequest) {
        ProjectReplyComment projectReplyComment = getProjectReplyComment(user, commentLikeRequest);
        try {
            validReplyLike(projectReplyComment, user, projectReplyCommentLikeJpaRepository.existsByUserAndProjectReplyComment(user, projectReplyComment));
            projectReplyCommentLikeJpaRepository.save(projectMapper.createProjectReplyCommentLike(user, projectReplyComment));
            projectReplyComment.setLikeCount(projectReplyComment.getLikeCount() + 1);
            log.info("프로젝트 대댓글 좋아요 완료 - 사용자: {} 프로젝트 대댓글 식별자: {} 좋아요 개수: {}", user.getName(), commentLikeRequest.idx(), projectReplyComment.getLikeCount());
            return commentLikeRequest.idx() + "번 프로젝트 대댓글 좋아요 완료";
        } catch (DataIntegrityViolationException e) {
            log.error("프로젝트 대댓글 좋아요 중복 발생 - 사용자: {}, 프로젝트 ID: {}", user.getName(), commentLikeRequest.idx());
            throw new BaseException(ALREADY_RECOMMENDED);
        }
    }

    /**
     * 대댓글 좋아요 취소
     *
     * @param user 사용자 정보
     * @param commentLikeRequest 대댓글 좋아요 정보
     * @return 대댓글 좋아요 취소 완료 메시지
     */
    @Override
    public String projectReplyCommentLikeCancel(User user, CommentLikeRequest commentLikeRequest) {
        ProjectReplyComment projectReplyComment = getProjectReplyComment(user, commentLikeRequest);
        try{
            validReplyLikeCancel(projectReplyComment, user, projectReplyCommentLikeJpaRepository.existsByUserAndProjectReplyComment(user, projectReplyComment));
            projectReplyCommentLikeJpaRepository.deleteByUserAndProjectReplyComment(user, projectReplyComment);
            if (projectReplyComment.getLikeCount() <= 0) {
                projectReplyComment.setLikeCount(0);
            }
            projectReplyComment.setLikeCount(projectReplyComment.getLikeCount() - 1);
            log.info("프로젝트 대댓글 좋아요 취소 완료 - 사용자: {} 프로젝트 대댓글 식별자: {} 좋아요 개수: {}", user.getName(), commentLikeRequest.idx(), projectReplyComment.getLikeCount());
            return commentLikeRequest.idx() + "번 프로젝트 대댓글 좋아요 취소 완료";
        } catch (DataIntegrityViolationException e) {
            log.error("프로젝트 대댓글 좋아요 취소 중복 발생 - 사용자: {}, 프로젝트 ID: {}", user.getName(), commentLikeRequest.idx());
            throw new BaseException(PROJECT_NOT_LIKE);
        }
    }

    private void validLike(ProjectComment projectComment, User user, boolean commentLikeJpaRepository) {
        if (projectComment.getUser().getId().equals(user.getId())) {
            log.error("프로젝트 댓글 좋아요 실패 - 사용자: {} 자신의 댓글에 좋아요를 할 수 없습니다.", user.getName());
            throw new BaseException(MY_COMMENT_LIKE);
        }
        if (commentLikeJpaRepository) {
            log.error("프로젝트 댓글 좋아요 실패 - 사용자: {} 이미 좋아요를 누른 댓글입니다.", user.getName());
            throw new BaseException(ALREADY_LIKE);
        }
    }

    private void validReplyLike(ProjectReplyComment projectReplyComment, User user, boolean commentLikeJpaRepository) {
        if (projectReplyComment.getUser().getId().equals(user.getId())) {
            log.error("프로젝트 대댓글 좋아요 실패 - 사용자: {} 자신의 대댓글에 좋아요를 할 수 없습니다.", user.getName());
            throw new BaseException(MY_COMMENT_LIKE);
        }
        if (commentLikeJpaRepository) {
            log.error("프로젝트 대댓글 좋아요 실패 - 사용자: {} 이미 좋아요를 누른 대댓글입니다.", user.getName());
            throw new BaseException(ALREADY_LIKE);
        }
    }

    private void validLikeCancel(ProjectComment projectComment, User user, boolean commentLikeJpaRepository) {
        if (projectComment.getUser().getId().equals(user.getId())) {
            log.error("프로젝트 댓글 좋아요 취소 실패 - 사용자: {} 자신의 댓글에 좋아요를 취소할 수 없습니다.", user.getName());
            throw new BaseException(MY_COMMENT_LIKE);
        }
        if (!commentLikeJpaRepository) {
            log.error("프로젝트 댓글 좋아요 취소 실패 - 사용자: {} 좋아요를 누르지 않은 댓글입니다.", user.getName());
            throw new BaseException(NOT_LIKE);
        }
    }

    private void validReplyLikeCancel(ProjectReplyComment projectReplyComment, User user, boolean commentLikeJpaRepository) {
        if (projectReplyComment.getUser().getId().equals(user.getId())) {
            log.error("프로젝트 대댓글 좋아요 취소 실패 - 사용자: {} 자신의 대댓글에 좋아요를 취소할 수 없습니다.", user.getName());
            throw new BaseException(MY_COMMENT_LIKE);
        }
        if (!commentLikeJpaRepository) {
            log.error("프로젝트 대댓글 좋아요 취소 실패 - 사용자: {} 좋아요를 누르지 않은 대댓글입니다.", user.getName());
            throw new BaseException(NOT_LIKE);
        }

    }

    private ProjectComment getProjectComment(User user, CommentLikeRequest commentLikeRequest) {
        ProjectComment projectComment;
        try {
            projectComment = projectCommentJpaRepository.findByIdAndStateWithPessimisticLock(commentLikeRequest.idx(), ACTIVE)
                    .orElseThrow(() -> new BaseException(PROJECT_COMMENT_NOT_FOUND));
        } catch (PessimisticLockingFailureException e) {
            log.error("프로젝트 댓글 추천 락 획득 실패 - 사용자: {}, 프로젝트 ID: {}", user.getName(), commentLikeRequest.idx());
            throw new BaseException(TEMPORARY_UNAVAILABLE);
        }

        if (!hasAccessToProject(projectComment.getProject(), user)) {
            throw new BaseException(PROJECT_NOT_PUBLIC);
        }
        return projectComment;
    }

    private ProjectReplyComment getProjectReplyComment(User user, CommentLikeRequest commentLikeRequest) {
        ProjectReplyComment projectReplyComment;
        try {
            projectReplyComment = projectReplyCommentJpaRepository.findByIdAndStateWithPessimisticLock(commentLikeRequest.idx(), ACTIVE)
                    .orElseThrow(() -> new BaseException(PROJECT_COMMENT_REPLY_NOT_FOUND));
        } catch (PessimisticLockingFailureException e) {
            log.error("프로젝트 대댓글 추천 락 획득 실패 - 사용자: {}, 프로젝트 ID: {}", user.getName(), commentLikeRequest.idx());
            throw new BaseException(TEMPORARY_UNAVAILABLE);
        }
        if (!hasAccessToProject(projectReplyComment.getProjectComment().getProject(), user)) {
            throw new BaseException(PROJECT_NOT_PUBLIC);
        }
        return projectReplyComment;
    }
}
