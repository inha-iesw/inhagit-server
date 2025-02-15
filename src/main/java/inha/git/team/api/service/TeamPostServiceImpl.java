package inha.git.team.api.service;

import inha.git.common.exceptions.BaseException;
import inha.git.team.api.controller.dto.request.CreateTeamPostRequest;
import inha.git.team.api.controller.dto.request.UpdateTeamPostRequest;
import inha.git.team.api.controller.dto.response.SearchTeamPostResponse;
import inha.git.team.api.controller.dto.response.SearchTeamPostsResponse;
import inha.git.team.api.controller.dto.response.TeamPostResponse;
import inha.git.team.api.mapper.TeamMapper;
import inha.git.team.domain.Team;
import inha.git.team.domain.TeamPost;
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
public class TeamPostServiceImpl implements TeamPostService{

    private final TeamJpaRepository teamJpaRepository;
    private final TeamMapper teamMapper;
    private final TeamPostJpaRepository teamPostJpaRepository;
    private final TeamPostQueryRepository teamPostQueryRepository;

    /**
     * 팀 게시글 전체 조회
     *
     * @param page Integer
     * @return Page<SearchTeamPostsResponse>
     */
    @Override
    @Transactional(readOnly = true)
    public Page<SearchTeamPostsResponse> getTeamPosts(Integer page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, CREATE_AT));
        return teamPostQueryRepository.getTeamPosts(pageable);
    }

    /**
     * 팀 게시글 상세 조회
     *
     * @param postIdx Integer
     * @return SearchTeamPostResponse
     */
    @Override
    @Transactional(readOnly = true)
    public SearchTeamPostResponse getTeamPost(Integer postIdx) {
        TeamPost teamPost = teamPostJpaRepository.findByIdAndState(postIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(TEAM_POST_NOT_FOUND));
        return teamMapper.teamPostToSearchTeamPostResponse(teamPost, teamPost.getTeam(), teamPost.getTeam().getUser());
    }

    /**
     * 팀 게시글을 생성
     *
     * @param user User
     * @param createTeamPostRequest CreateTeamPostRequest
     * @return TeamPostResponse
     */
    @Override
    public TeamPostResponse createTeamPost(User user, CreateTeamPostRequest createTeamPostRequest) {
        Team team = teamJpaRepository.findByIdAndState(createTeamPostRequest.teamIdx(), ACTIVE)
                .orElseThrow(() -> new BaseException(TEAM_NOT_FOUND));
        if(!team.getUser().getId().equals(user.getId())) {
            throw new BaseException(TEAM_POST_NOT_AUTHORIZED);
        }
        TeamPost teamPost = teamMapper.createTeamPost(team, createTeamPostRequest);
        teamPostJpaRepository.save(teamPost);
        return teamMapper.teamPostToTeamPostResponse(teamPost);
    }

    /**
     * 팀 게시글을 수정
     *
     * @param user User
     * @param postIdx Integer
     * @param updateTeamPostRequest UpdateTeamPostRequest
     * @return TeamPostResponse
     */
    @Override
    public TeamPostResponse updateTeamPost(User user, Integer postIdx, UpdateTeamPostRequest updateTeamPostRequest) {
        TeamPost teamPost = teamPostJpaRepository.findByIdAndState(postIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(TEAM_POST_NOT_FOUND));
        if(!teamPost.getTeam().getUser().getId().equals(user.getId())) {
            throw new BaseException(TEAM_POST_UPDATE_NOT_AUTHORIZED);
        }
        teamMapper.updateTeamPostRequestToTeamPost(updateTeamPostRequest, teamPost);
        return teamMapper.teamPostToTeamPostResponse(teamPost);
    }

    /**
     * 팀 게시글을 삭제
     *
     * @param user User
     * @param postIdx Integer
     * @return TeamPostResponse
     */
    @Override
    public TeamPostResponse deleteTeamPost(User user, Integer postIdx) {
        TeamPost teamPost = teamPostJpaRepository.findByIdAndState(postIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(TEAM_POST_NOT_FOUND));
        if(!teamPost.getTeam().getUser().getId().equals(user.getId()) && !user.getRole().equals(Role.ADMIN)) {
            throw new BaseException(TEAM_POST_DELETE_NOT_AUTHORIZED);
        }
        teamPost.setState(INACTIVE);
        teamPost.setDeletedAt();
        teamPostJpaRepository.save(teamPost);
        return teamMapper.teamPostToTeamPostResponse(teamPost);
    }
}
