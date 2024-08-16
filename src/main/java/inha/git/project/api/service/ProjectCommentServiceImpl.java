package inha.git.project.api.service;

import inha.git.common.exceptions.BaseException;
import inha.git.project.api.controller.api.request.CreateCommentRequest;
import inha.git.project.api.controller.api.request.CreateReplyCommentRequest;
import inha.git.project.api.controller.api.request.UpdateCommentRequest;
import inha.git.project.api.controller.api.response.*;
import inha.git.project.api.mapper.ProjectMapper;
import inha.git.project.domain.Project;
import inha.git.project.domain.ProjectComment;
import inha.git.project.domain.ProjectReplyComment;
import inha.git.project.domain.repository.ProjectCommentJpaRepository;
import inha.git.project.domain.repository.ProjectJpaRepository;
import inha.git.project.domain.repository.ProjectReplyCommentJpaRepository;
import inha.git.user.domain.User;
import inha.git.user.domain.enums.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static inha.git.common.BaseEntity.State.ACTIVE;
import static inha.git.common.BaseEntity.State.INACTIVE;
import static inha.git.common.code.status.ErrorStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProjectCommentServiceImpl implements ProjectCommentService {

    private final ProjectJpaRepository projectJpaRepository;
    private final ProjectCommentJpaRepository projectCommentJpaRepository;
    private final ProjectReplyCommentJpaRepository projectReplyCommentJpaRepository;
    private final ProjectMapper projectMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CommentWithRepliesResponse> getAllCommentsByProjectIdx(Integer projectIdx) {
        Project project = projectJpaRepository.findByIdAndState(projectIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(PROJECT_NOT_FOUND));
        List<ProjectComment> comments = projectCommentJpaRepository.findAllByProjectAndStateOrderByIdAsc(project, ACTIVE);
        return projectMapper.toCommentWithRepliesResponseList(comments);
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
        Project project = projectJpaRepository.findByIdAndState(createCommentRequest.projectIdx(), ACTIVE)
                .orElseThrow(() -> new BaseException(PROJECT_NOT_FOUND));
        ProjectComment projectComment = projectMapper.toProjectComment(createCommentRequest, user, project);
        projectCommentJpaRepository.save(projectComment);
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
            throw new BaseException(PROJECT_COMMENT_UPDATE_NOT_AUTHORIZED);
        }
        projectComment.setContents(updateCommentRequest.contents());
        projectCommentJpaRepository.save(projectComment);
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
            throw new BaseException(PROJECT_COMMENT_DELETE_NOT_AUTHORIZED);
        }
        projectComment.setDeletedAt();
        projectComment.setState(INACTIVE);
        projectCommentJpaRepository.save(projectComment);
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
        ProjectComment projectComment = projectCommentJpaRepository.findByIdAndState(createReplyCommentRequest.commentIdx(), ACTIVE)
                .orElseThrow(() -> new BaseException(PROJECT_COMMENT_NOT_FOUND));
        ProjectReplyComment projectReplyComment = projectMapper.toProjectReplyComment(createReplyCommentRequest, user, projectComment);
        projectReplyCommentJpaRepository.save(projectReplyComment);
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
            throw new BaseException(PROJECT_COMMENT_REPLY_UPDATE_NOT_AUTHORIZED);
        }
        projectReplyComment.setContents(updateCommentRequest.contents());
        projectReplyCommentJpaRepository.save(projectReplyComment);
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
            throw new BaseException(PROJECT_COMMENT_REPLY_UPDATE_NOT_AUTHORIZED);
        }
        projectReplyComment.setDeletedAt();
        projectReplyComment.setState(INACTIVE);
        projectReplyCommentJpaRepository.save(projectReplyComment);
        return projectMapper.toReplyCommentResponse(projectReplyComment);
    }
}
