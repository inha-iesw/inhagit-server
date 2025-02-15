package inha.git.project.api.service;

import inha.git.common.exceptions.BaseException;
import inha.git.mapping.domain.repository.ProjectCommentLikeJpaRepository;
import inha.git.mapping.domain.repository.ProjectReplyCommentLikeJpaRepository;
import inha.git.project.api.controller.dto.request.CommentLikeRequest;
import inha.git.project.api.controller.dto.request.CreateCommentRequest;
import inha.git.project.api.controller.dto.request.CreateReplyCommentRequest;
import inha.git.project.api.controller.dto.request.UpdateCommentRequest;
import inha.git.project.api.controller.dto.response.CommentResponse;
import inha.git.project.api.controller.dto.response.CommentWithRepliesResponse;
import inha.git.project.api.controller.dto.response.ReplyCommentResponse;
import inha.git.project.api.controller.dto.response.SearchReplyCommentResponse;
import inha.git.project.api.mapper.ProjectMapper;
import inha.git.project.domain.Project;
import inha.git.project.domain.ProjectComment;
import inha.git.project.domain.ProjectReplyComment;
import inha.git.project.domain.repository.ProjectCommentJpaRepository;
import inha.git.project.domain.repository.ProjectJpaRepository;
import inha.git.project.domain.repository.ProjectReplyCommentJpaRepository;
import inha.git.user.domain.User;
import inha.git.user.domain.enums.Role;
import inha.git.utils.IdempotentProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static inha.git.common.BaseEntity.State.ACTIVE;
import static inha.git.common.BaseEntity.State.INACTIVE;
import static inha.git.common.Constant.hasAccessToProject;
import static inha.git.common.code.status.ErrorStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProjectCommentServiceImpl implements ProjectCommentService {

    private final ProjectJpaRepository projectJpaRepository;
    private final ProjectCommentJpaRepository projectCommentJpaRepository;
    private final ProjectReplyCommentJpaRepository projectReplyCommentJpaRepository;
    private final ProjectCommentLikeJpaRepository projectCommentLikeJpaRepository;
    private final ProjectReplyCommentLikeJpaRepository projectReplyCommentLikeJpaRepository;
    private final ProjectMapper projectMapper;
    private final IdempotentProvider idempotentProvider;

    /**
     * 특정 프로젝트 댓글 전체 조회
     *
     * @param projectIdx 프로젝트 식별자
     * @return List<CommentWithRepliesResponse>
     */
    @Override
    @Transactional(readOnly = true)
    public List<CommentWithRepliesResponse> getAllCommentsByProjectIdx(User user, Integer projectIdx) {
        Project project = projectJpaRepository.findByIdAndState(projectIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(PROJECT_NOT_FOUND));

        if (!hasAccessToProject(project, user)) {
            throw new BaseException(PROJECT_NOT_PUBLIC);
        }

        List<ProjectComment> comments = projectCommentJpaRepository.findAllByProjectAndStateOrderByIdAsc(project, ACTIVE);
        return comments.stream()
                .map(comment -> {
                    // 댓글에 대한 likeState를 확인
                    boolean commentLikeState = projectCommentLikeJpaRepository.existsByUserAndProjectComment(user, comment);
                    // 대댓글에 대한 likeState를 확인하여 변환
                    List<SearchReplyCommentResponse> replies = comment.getReplies().stream()
                            .filter(reply -> reply.getState().equals(ACTIVE))
                            .map(reply -> {
                                boolean replyLikeState = projectReplyCommentLikeJpaRepository.existsByUserAndProjectReplyComment(user, reply);
                                return projectMapper.toSearchReplyCommentResponse(reply, replyLikeState);
                            })
                            .toList();
                    // 댓글과 대댓글 리스트를 포함하여 CommentWithRepliesResponse로 변환
                    return projectMapper.toCommentWithRepliesResponse(comment, commentLikeState, replies);
                })
                .toList();
    }

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
