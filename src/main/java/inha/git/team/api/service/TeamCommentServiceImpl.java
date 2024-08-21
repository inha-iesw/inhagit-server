package inha.git.team.api.service;

import inha.git.common.exceptions.BaseException;
import inha.git.question.api.controller.dto.response.CommentResponse;
import inha.git.team.api.controller.dto.request.CreateCommentRequest;
import inha.git.team.api.controller.dto.request.CreateTeamPostRequest;
import inha.git.team.api.controller.dto.request.UpdateTeamPostRequest;
import inha.git.team.api.controller.dto.response.SearchTeamPostResponse;
import inha.git.team.api.controller.dto.response.SearchTeamPostsResponse;
import inha.git.team.api.controller.dto.response.TeamPostResponse;
import inha.git.team.api.mapper.TeamMapper;
import inha.git.team.domain.Team;
import inha.git.team.domain.TeamComment;
import inha.git.team.domain.TeamPost;
import inha.git.team.domain.repository.TeamCommentJpaRepository;
import inha.git.team.domain.repository.TeamJpaRepository;
import inha.git.team.domain.repository.TeamPostJpaRepository;
import inha.git.team.domain.repository.TeamPostQueryRepository;
import inha.git.user.domain.User;
import inha.git.user.domain.enums.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static inha.git.common.BaseEntity.State.ACTIVE;
import static inha.git.common.BaseEntity.State.INACTIVE;
import static inha.git.common.Constant.CREATE_AT;
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
    public CommentResponse createComment(User user, CreateCommentRequest createCommentRequest) {
        TeamPost teamPost = teamPostJpaRepository.findByIdAndState(createCommentRequest.postIdx(), ACTIVE)
                .orElseThrow(() -> new BaseException(TEAM_POST_NOT_FOUND));
        TeamComment teamComment = teamMapper.toTeamComment(createCommentRequest, user, teamPost);
        teamCommentJpaRepository.save(teamComment);
        return teamMapper.toCommentResponse(teamComment);
    }
}
