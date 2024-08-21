package inha.git.team.api.service;

import inha.git.common.exceptions.BaseException;
import inha.git.team.api.controller.dto.request.CreateCommentRequest;
import inha.git.team.api.controller.dto.response.TeamCommentResponse;
import inha.git.team.api.mapper.TeamMapper;
import inha.git.team.domain.TeamComment;
import inha.git.team.domain.TeamPost;
import inha.git.team.domain.repository.TeamCommentJpaRepository;
import inha.git.team.domain.repository.TeamPostJpaRepository;
import inha.git.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static inha.git.common.BaseEntity.State.ACTIVE;
import static inha.git.common.code.status.ErrorStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TeamCommentServiceImpl implements TeamCommentService{

    private final TeamMapper teamMapper;
    private final TeamPostJpaRepository teamPostJpaRepository;
    private final TeamCommentJpaRepository teamCommentJpaRepository;


    /**
     * 팀 게시글 댓글 생성
     *
     * @param user 사용자 정보
     * @param createCommentRequest 댓글 생성 요청
     * @return CommentResponse
     */
    @Override
    public TeamCommentResponse createComment(User user, CreateCommentRequest createCommentRequest) {
        TeamPost teamPost = teamPostJpaRepository.findByIdAndState(createCommentRequest.postIdx(), ACTIVE)
                .orElseThrow(() -> new BaseException(TEAM_POST_NOT_FOUND));
        TeamComment teamComment = teamMapper.toTeamComment(createCommentRequest, user, teamPost);
        teamCommentJpaRepository.save(teamComment);
        return teamMapper.toTeamCommentResponse(teamComment);
    }

    /**
     * 팀 게시글 댓글 수정
     *
     * @param user 사용자 정보
     * @param commentIdx 댓글 식별자
     * @param createCommentRequest 댓글 수정 요청
     * @return CommentResponse
     */
    @Override
    public TeamCommentResponse updateComment(User user, Integer commentIdx, CreateCommentRequest createCommentRequest) {
        TeamComment teamComment = teamCommentJpaRepository.findByIdAndState(commentIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(TEAM_COMMENT_NOT_FOUND));
        if (!teamComment.getUser().getId().equals(user.getId())) {
            throw new BaseException(TEAM_COMMENT_UPDATE_NOT_ALLOWED);
        }
        teamMapper.updateTeamCommentRequestToTeamComment(createCommentRequest, teamComment);
        return teamMapper.toTeamCommentResponse(teamComment);
    }
}
