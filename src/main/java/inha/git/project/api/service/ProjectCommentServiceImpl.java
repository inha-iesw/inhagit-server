package inha.git.project.api.service;

import inha.git.common.exceptions.BaseException;
import inha.git.project.api.controller.api.request.CreateCommentRequest;
import inha.git.project.api.controller.api.response.CreateCommentResponse;
import inha.git.project.api.mapper.ProjectMapper;
import inha.git.project.domain.Project;
import inha.git.project.domain.ProjectComment;
import inha.git.project.domain.repository.ProjectCommentJpaRepository;
import inha.git.project.domain.repository.ProjectJpaRepository;
import inha.git.project.domain.repository.ProjectReplyCommentJpaRepository;
import inha.git.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static inha.git.common.BaseEntity.State.ACTIVE;
import static inha.git.common.code.status.ErrorStatus.PROJECT_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProjectCommentServiceImpl implements ProjectCommentService {

    private final ProjectJpaRepository projectJpaRepository;
    private final ProjectCommentJpaRepository projectCommentJpaRepository;
    private final ProjectReplyCommentJpaRepository projectReplyCommentJpaRepository;
    private final ProjectMapper projectMapper;
    @Override
    @Transactional
    public CreateCommentResponse createComment(User user, CreateCommentRequest createCommentRequest) {
        Project project = projectJpaRepository.findByIdAndState(createCommentRequest.projectIdx(), ACTIVE)
                .orElseThrow(() -> new BaseException(PROJECT_NOT_FOUND));
        ProjectComment projectComment = projectMapper.toProjectComment(createCommentRequest, user, project);
        projectCommentJpaRepository.save(projectComment);
        return projectMapper.toCreateCommentResponse(projectComment);
    }
}
