package inha.git.team.api.service;

import inha.git.common.exceptions.BaseException;
import inha.git.mapping.domain.TeamUser;
import inha.git.mapping.domain.repository.TeamUserJpaRepository;
import inha.git.project.api.controller.dto.response.SearchUserResponse;
import inha.git.statistics.api.service.StatisticsService;
import inha.git.team.api.controller.dto.request.ApproveRequestTeamRequest;
import inha.git.team.api.controller.dto.request.CreateTeamRequest;
import inha.git.team.api.controller.dto.request.RequestTeamRequest;
import inha.git.team.api.controller.dto.request.UpdateTeamRequest;
import inha.git.team.api.controller.dto.response.*;
import inha.git.team.api.mapper.TeamMapper;
import inha.git.team.domain.Team;
import inha.git.team.domain.repository.TeamJpaRepository;
import inha.git.user.domain.User;
import inha.git.user.domain.enums.Role;
import inha.git.user.domain.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
public class TeamServiceImpl implements TeamService {

    private final TeamJpaRepository teamJpaRepository;
    private final TeamUserJpaRepository teamUserJpaRepository;
    private final TeamMapper teamMapper;
    private final UserJpaRepository userJpaRepository;
    private final StatisticsService statisticsService;

    /**
     * 내가 생성한 팀 목록 가져오기
     *
     * @param user User
     * @return List<SearchTeamsResponse>
     */
    @Override
    @Transactional(readOnly = true)
    public List<SearchTeamsResponse> getMyTeams(User user) {
        return teamMapper.teamsToSearchTeamsResponse(teamJpaRepository.findByUserAndStateOrderByCreatedAtDesc(user, ACTIVE));
    }

    /**
     * 팀 가져오기
     *
     * @param teamIdx Integer
     * @return SearchTeamResponse
     */
    @Override
    @Transactional(readOnly = true)
    public SearchTeamResponse getTeam(Integer teamIdx) {
        Team team = teamJpaRepository.findByIdAndState(teamIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(TEAM_NOT_FOUND));
        SearchUserResponse searchUserResponse = teamMapper.userToSearchUserResponse(team.getUser());
        List<SearchTeamUserResponse> list = team.getTeamUsers().stream()
                .filter(tu -> tu.getAcceptedAt() != null) // acceptAt이 null이 아닌 것만 필터링
                .map(tu -> new SearchTeamUserResponse(tu.getUser().getId(), tu.getUser().getName(), tu.getUser().getEmail(), tu.getAcceptedAt()))
                .toList();
        return new SearchTeamResponse(team.getId(), team.getName(), team.getMaxMemberNumber(), team.getCurrtentMemberNumber(), team.getCreatedAt(), searchUserResponse, list);
    }

    /**
     * 팀 생성
     *
     * @param user User
     * @param createTeamRequest CreateTeamRequest
     * @return TeamResponse
     */
    @Override
    public TeamResponse createTeam(User user, CreateTeamRequest createTeamRequest) {
        Team team = teamMapper.createTeamRequestToTeam(createTeamRequest, user);
        teamJpaRepository.save(team);
        teamUserJpaRepository.save(teamMapper.createTeamUser(user, team));
        statisticsService.increaseCount(user, 3);
        return teamMapper.teamToTeamResponse(team);
    }

    /**
     * 팀 수정
     *
     * @param user User
     * @param teamIdx Integer
     * @param updateTeamRequest UpdateTeamRequest
     * @return TeamResponse
     */
    @Override
    public TeamResponse updateTeam(User user, Integer teamIdx, UpdateTeamRequest updateTeamRequest) {
        Team team = teamJpaRepository.findByIdAndState(teamIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(TEAM_NOT_FOUND));
        if(!team.getUser().getId().equals(user.getId())) {
            throw new BaseException(TEAM_NOT_AUTHORIZED);
        }
        if(team.getCurrtentMemberNumber() > updateTeamRequest.maxMember()) {
            throw new BaseException(TEAM_MAX_MEMBER);
        }
        teamMapper.updateTeamRequestToTeam(updateTeamRequest, team);
        return teamMapper.teamToTeamResponse(team);
    }

    /**
     * 팀 삭제
     *
     * @param user User
     * @param teamIdx Integer
     * @return TeamResponse
     */
    @Override
    public TeamResponse deleteTeam(User user, Integer teamIdx) {
        Team team = teamJpaRepository.findByIdAndState(teamIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(TEAM_NOT_FOUND));
        if(!team.getUser().getId().equals(user.getId()) && user.getRole() != Role.ADMIN) {
            throw new BaseException(TEAM_DELETE_NOT_AUTHORIZED);
        }
        team.setState(INACTIVE);
        team.setDeletedAt();
        teamJpaRepository.save(team);
        teamUserJpaRepository.findByTeam(team)
                .forEach(teamUser -> statisticsService.decreaseCount(teamUser.getUser(), 3));
        return teamMapper.teamToTeamResponse(team);
    }

    /**
     * 팀 가입 요청
     *
     * @param user User
     * @param requestTeamRequest RequestTeamRequest
     * @return TeamResponse
     */
    @Override
    public TeamResponse requestTeam(User user, RequestTeamRequest requestTeamRequest) {
        Team team = teamJpaRepository.findByIdAndState(requestTeamRequest.teamIdx(), ACTIVE)
                .orElseThrow(() -> new BaseException(TEAM_NOT_FOUND));
        if (team.getCurrtentMemberNumber() >= team.getMaxMemberNumber()) {
            throw new BaseException(TEAM_RECRUITMENT_CLOSED);
        }
        teamUserJpaRepository.findByUserAndTeam(user, team)
                .ifPresent(teamUser -> {
                    if (teamUser.getAcceptedAt() != null) {
                        throw new BaseException(TEAM_ALREADY_JOINED);
                    } else {
                        throw new BaseException(TEAM_ALREADY_JOINED_REQUEST);
                    }
                });
        TeamUser teamUser = teamMapper.createRequestTeamUser(user, team);
        teamUserJpaRepository.save(teamUser);
        return teamMapper.teamToTeamResponse(team);
    }

    /**
     * 팀 가입 요청 승인
     *
     * @param user User
     * @param approveRequestTeamRequest ApproveRequestTeamRequest
     * @return TeamResponse
     */
    @Override
    public TeamResponse approveRequestTeam(User user, ApproveRequestTeamRequest approveRequestTeamRequest) {
        Team team = teamJpaRepository.findByIdAndState(approveRequestTeamRequest.teamIdx(), ACTIVE)
                .orElseThrow(() -> new BaseException(TEAM_NOT_FOUND));
        if (!team.getUser().getId().equals(user.getId())) {
            throw new BaseException(TEAM_NOT_LEADER);
        }
        User requestUser = userJpaRepository.findByIdAndState(approveRequestTeamRequest.userIdx(), ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_FIND_USER));
        TeamUser teamUser = teamUserJpaRepository.findByUserAndTeam(requestUser, team)
                .orElseThrow(() -> new BaseException(TEAM_NOT_REQUESTED));
        if (teamUser.getAcceptedAt() != null) {
            throw new BaseException(TEAM_ALREADY_JOINED);
        }
        if(team.getCurrtentMemberNumber() >= team.getMaxMemberNumber()) {
            throw new BaseException(TEAM_MAX_MEMBER);
        }
        teamUser.setAcceptedAt();
        teamUserJpaRepository.save(teamUser);
        team.increaseCurrentMemberNumber();
        statisticsService.increaseCount(requestUser, 4);
        return teamMapper.teamToTeamResponse(team);
    }

    /**
     * 팀 탈퇴
     *
     * @param user User
     * @param teamIdx Integer
     * @return TeamResponse
     */
    @Override
    public TeamResponse exitTeam(User user, Integer teamIdx) {
        Team team = teamJpaRepository.findByIdAndState(teamIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(TEAM_NOT_FOUND));
        if(user.getId().equals(team.getUser().getId())) {
            throw new BaseException(TEAM_LEADER_CANNOT_EXIT);
        }
        TeamUser teamUser = teamUserJpaRepository.findByUserAndTeam(user, team)
                .orElseThrow(() -> new BaseException(TEAM_NOT_JOINED));
        if(teamUser.getAcceptedAt() == null) {
            throw new BaseException(TEAM_NOT_JOINED);
        }
        teamUserJpaRepository.delete(teamUser);
        team.decreaseCurrentMemberNumber();
        statisticsService.decreaseCount(user, 3);
        return teamMapper.teamToTeamResponse(team);
    }

    /**
     * 팀 가입 요청 목록 가져오기
     *
     * @param user User
     * @param teamIdx Integer
     * @param page Integer
     * @return Page<SearchTeamUserResponse>
     */
    @Override
    public Page<SearchRequestResponse> getRequestTeams(User user, Integer teamIdx, Integer page) {
        Team team = teamJpaRepository.findByIdAndState(teamIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(TEAM_NOT_FOUND));
        if (!team.getUser().getId().equals(user.getId())) {
            throw new BaseException(TEAM_NOT_LEADER);
        }
        Pageable pageable = PageRequest.of(page, 10);
        Page<TeamUser> teamUsers = teamUserJpaRepository.findByTeamAndAcceptedAtIsNull(team, pageable);
        return teamUsers.map(tu -> new SearchRequestResponse(
                tu.getUser().getId(),
                tu.getUser().getName(),
                tu.getUser().getEmail(),
                tu.getCreatedAt()
        ));
    }
}
