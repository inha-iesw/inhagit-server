package inha.git.team.api.service;

import inha.git.common.exceptions.BaseException;
import inha.git.team.api.controller.dto.request.CreateCommentRequest;
import inha.git.team.api.controller.dto.request.CreateReplyCommentRequest;
import inha.git.team.api.controller.dto.request.UpdateCommentRequest;
import inha.git.team.api.controller.dto.response.TeamCommentResponse;
import inha.git.team.api.controller.dto.response.TeamReplyCommentResponse;
import inha.git.team.api.mapper.TeamMapper;
import inha.git.team.domain.TeamComment;
import inha.git.team.domain.TeamPost;
import inha.git.team.domain.TeamReplyComment;
import inha.git.team.domain.repository.TeamCommentJpaRepository;
import inha.git.team.domain.repository.TeamPostJpaRepository;
import inha.git.team.domain.repository.TeamReplyCommentJpaRepository;
import inha.git.user.domain.User;
import inha.git.user.domain.enums.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static inha.git.common.BaseEntity.State.ACTIVE;
import static inha.git.common.BaseEntity.State.INACTIVE;
import static inha.git.common.code.status.ErrorStatus.*;

/**
 * TeamCommentServiceImpl은 TeamCommentService 인터페이스를 구현하는 클래스.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TeamCommentServiceImpl implements TeamCommentService{

    private final TeamMapper teamMapper;
    private final TeamPostJpaRepository teamPostJpaRepository;
    private final TeamCommentJpaRepository teamCommentJpaRepository;
    private final TeamReplyCommentJpaRepository teamReplyCommentJpaRepository;


    /**
     * 팀 게시글 댓글 생성
     *
     * @param user 사용자 정보
     * @param createCommentRequest 댓글 생성 요청
     * @return TeamCommentResponse
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
     * @param updateCommentRequest 댓글 수정 요청
     * @return TeamCommentResponse
     */
    @Override
    public TeamCommentResponse updateComment(User user, Integer commentIdx, UpdateCommentRequest updateCommentRequest) {
        TeamComment teamComment = teamCommentJpaRepository.findByIdAndState(commentIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(TEAM_COMMENT_NOT_FOUND));
        if (!teamComment.getUser().getId().equals(user.getId())) {
            throw new BaseException(TEAM_COMMENT_UPDATE_NOT_ALLOWED);
        }
        teamMapper.updateTeamCommentRequestToTeamComment(updateCommentRequest, teamComment);
        return teamMapper.toTeamCommentResponse(teamComment);
    }

    /**
     * 팀 게시글 댓글 삭제
     *
     * @param user 사용자 정보
     * @param commentIdx 댓글 식별자
     * @return TeamCommentResponse
     */
    @Override
    public TeamCommentResponse deleteComment(User user, Integer commentIdx) {
        TeamComment teamComment = teamCommentJpaRepository.findByIdAndState(commentIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(TEAM_COMMENT_NOT_FOUND));
        if(!teamComment.getUser().getId().equals(user.getId()) && user.getRole() != Role.ADMIN) {
            throw new BaseException(TEAM_COMMENT_DELETE_NOT_ALLOWED);
        }
        teamComment.setDeletedAt();
        teamComment.setState(INACTIVE);
        teamCommentJpaRepository.save(teamComment);
        return teamMapper.toTeamCommentResponse(teamComment);
    }

    /**
     * 팀 게시글 대댓글 생성
     *
     * @param user 사용자 정보
     * @param createReplyCommentRequest 대댓글 생성 요청
     * @return TeamReplyCommentResponse
     */
    @Override
    public TeamReplyCommentResponse createReplyComment(User user, CreateReplyCommentRequest createReplyCommentRequest) {
        TeamComment teamComment = teamCommentJpaRepository.findByIdAndState(createReplyCommentRequest.commentIdx(), ACTIVE)
                .orElseThrow(() -> new BaseException(TEAM_COMMENT_NOT_FOUND));
        TeamReplyComment teamReplyComment = teamMapper.toTeamReplyComment(createReplyCommentRequest, user, teamComment);
        teamReplyCommentJpaRepository.save(teamReplyComment);
        return teamMapper.toTeamReplyCommentResponse(teamReplyComment);
    }

    /**
     * 팀 게시글 대댓글 수정
     *
     * @param user 사용자 정보
     * @param replyCommentIdx 대댓글 식별자
     * @param updateCommentRequest 대댓글 수정 요청
     * @return TeamReplyCommentResponse
     */
    @Override
    public TeamReplyCommentResponse updateReplyComment(User user, Integer replyCommentIdx, UpdateCommentRequest updateCommentRequest) {
        TeamReplyComment teamReplyComment = teamReplyCommentJpaRepository.findByIdAndState(replyCommentIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(TEAM_REPLY_COMMENT_NOT_FOUND));
        if (!teamReplyComment.getUser().getId().equals(user.getId())) {
            throw new BaseException(TEAM_REPLY_COMMENT_UPDATE_NOT_ALLOWED);
        }
        teamMapper.updateTeamReplyCommentRequestToTeamReplyComment(updateCommentRequest, teamReplyComment);
        teamReplyCommentJpaRepository.save(teamReplyComment);
        return teamMapper.toTeamReplyCommentResponse(teamReplyComment);
    }

    /**
     * 팀 게시글 대댓글 삭제
     *
     * @param user 사용자 정보
     * @param replyCommentIdx 대댓글 식별자
     * @return TeamReplyCommentResponse
     */
    @Override
    public TeamReplyCommentResponse deleteReplyComment(User user, Integer replyCommentIdx) {
        TeamReplyComment teamReplyComment = teamReplyCommentJpaRepository.findByIdAndState(replyCommentIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(TEAM_REPLY_COMMENT_NOT_FOUND));
        if (!teamReplyComment.getUser().getId().equals(user.getId()) && user.getRole() != Role.ADMIN) {
            throw new BaseException(TEAM_REPLY_COMMENT_DELETE_NOT_ALLOWED);
        }
        teamReplyComment.setDeletedAt();
        teamReplyComment.setState(INACTIVE);
        teamReplyCommentJpaRepository.save(teamReplyComment);
        return teamMapper.toTeamReplyCommentResponse(teamReplyComment);
    }
}
