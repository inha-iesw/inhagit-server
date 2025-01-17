package inha.git.project.api.service.comment.query;

import inha.git.common.exceptions.BaseException;
import inha.git.mapping.domain.repository.ProjectCommentLikeJpaRepository;
import inha.git.mapping.domain.repository.ProjectReplyCommentLikeJpaRepository;
import inha.git.project.api.controller.dto.response.CommentWithRepliesResponse;
import inha.git.project.api.controller.dto.response.SearchReplyCommentResponse;
import inha.git.project.api.mapper.ProjectMapper;
import inha.git.project.domain.Project;
import inha.git.project.domain.ProjectComment;
import inha.git.project.domain.repository.ProjectCommentJpaRepository;
import inha.git.project.domain.repository.ProjectJpaRepository;
import inha.git.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static inha.git.common.BaseEntity.State.ACTIVE;
import static inha.git.common.Constant.hasAccessToProject;
import static inha.git.common.code.status.ErrorStatus.PROJECT_NOT_FOUND;
import static inha.git.common.code.status.ErrorStatus.PROJECT_NOT_PUBLIC;

/**
 * ProjectCommentQueryServiceImpl은 프로젝트 댓글 조회 관련 비즈니스 로직을 처리합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProjectCommentQueryServiceImpl implements ProjectCommentQueryService {

    private final ProjectJpaRepository projectJpaRepository;
    private final ProjectCommentJpaRepository projectCommentJpaRepository;
    private final ProjectCommentLikeJpaRepository projectCommentLikeJpaRepository;
    private final ProjectReplyCommentLikeJpaRepository projectReplyCommentLikeJpaRepository;
    private final ProjectMapper projectMapper;

    /**
     * 특정 프로젝트 댓글 전체 조회
     *
     * @param projectIdx 프로젝트 식별자
     * @return List<CommentWithRepliesResponse>
     */
    @Override
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
}
