package inha.git.project.api.service;

import inha.git.common.exceptions.BaseException;
import inha.git.project.api.controller.api.request.CreateCommentRequest;
import inha.git.project.api.controller.api.request.UpdateCommentRequest;
import inha.git.project.api.controller.api.response.CreateCommentResponse;
import inha.git.project.api.controller.api.response.DeleteCommentResponse;
import inha.git.project.api.controller.api.response.UpdateCommentResponse;
import inha.git.project.api.mapper.ProjectMapper;
import inha.git.project.domain.Project;
import inha.git.project.domain.ProjectComment;
import inha.git.project.domain.repository.ProjectCommentJpaRepository;
import inha.git.project.domain.repository.ProjectJpaRepository;
import inha.git.project.domain.repository.ProjectReplyCommentJpaRepository;
import inha.git.user.domain.User;
import inha.git.user.domain.enums.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static inha.git.common.BaseEntity.State.ACTIVE;
import static inha.git.common.BaseEntity.State.INACTIVE;
import static inha.git.common.code.status.ErrorStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProjectCommentServiceImpl implements ProjectCommentService {

    private final ProjectJpaRepository projectJpaRepository;
    private final ProjectCommentJpaRepository projectCommentJpaRepository;
    private final ProjectReplyCommentJpaRepository projectReplyCommentJpaRepository;
    private final ProjectMapper projectMapper;

    /**
     * 댓글 생성
     *
     * @param user                사용자 정보
     * @param createCommentRequest 댓글 생성 요청
     * @return CreateCommentResponse
     */
    @Override
    @Transactional
    public CreateCommentResponse createComment(User user, CreateCommentRequest createCommentRequest) {
        Project project = projectJpaRepository.findByIdAndState(createCommentRequest.projectIdx(), ACTIVE)
                .orElseThrow(() -> new BaseException(PROJECT_NOT_FOUND));
        ProjectComment projectComment = projectMapper.toProjectComment(createCommentRequest, user, project);
        projectCommentJpaRepository.save(projectComment);
        return projectMapper.toCreateCommentResponse(projectComment);
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
    @Transactional
    public UpdateCommentResponse updateComment(User user, Integer commentIdx, UpdateCommentRequest updateCommentRequest) {
        ProjectComment projectComment = projectCommentJpaRepository.findByIdAndState(commentIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(PROJECT_COMMENT_NOT_FOUND));
        if(!projectComment.getUser().getId().equals(user.getId())) {
            throw new BaseException(PROJECT_COMMENT_UPDATE_NOT_AUTHORIZED);
        }
        projectComment.setContents(updateCommentRequest.contents());
        projectCommentJpaRepository.save(projectComment);
        return projectMapper.toUpdateCommentResponse(projectComment);
    }

    /**
     * 댓글 삭제
     *
     * @param user       사용자 정보
     * @param commentIdx 댓글 식별자
     * @return DeleteCommentResponse
     */
    @Override
    @Transactional
    public DeleteCommentResponse deleteComment(User user, Integer commentIdx) {
        ProjectComment projectComment = projectCommentJpaRepository.findByIdAndState(commentIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(PROJECT_COMMENT_NOT_FOUND));
        if(!projectComment.getUser().getId().equals(user.getId()) && user.getRole() != Role.ADMIN) {
            throw new BaseException(PROJECT_COMMENT_DELETE_NOT_AUTHORIZED);
        }
        projectComment.setDeletedAt();
        projectComment.setState(INACTIVE);
        projectCommentJpaRepository.save(projectComment);
        return projectMapper.toDeleteCommentResponse(projectComment);
    }
}
