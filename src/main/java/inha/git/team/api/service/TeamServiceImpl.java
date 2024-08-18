package inha.git.team.api.service;

import inha.git.common.exceptions.BaseException;
import inha.git.mapping.domain.repository.TeamUserJpaRepository;
import inha.git.team.api.controller.dto.request.CreateTeamRequest;
import inha.git.team.api.controller.dto.request.UpdateTeamRequest;
import inha.git.team.api.controller.dto.response.SearchTeamsResponse;
import inha.git.team.api.controller.dto.response.TeamResponse;
import inha.git.team.api.mapper.TeamMapper;
import inha.git.team.domain.Team;
import inha.git.team.domain.repository.TeamJpaRepository;
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
public class TeamServiceImpl implements TeamService {

    private final TeamJpaRepository teamJpaRepository;
    private final TeamUserJpaRepository teamUserJpaRepository;
    private final TeamMapper teamMapper;

    @Override
    public List<SearchTeamsResponse> getMyTeams(User user) {
        return teamMapper.teamsToSearchTeamsResponse(teamJpaRepository.findByUserAndStateOrderByCreatedAtDesc(user, ACTIVE));
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
        return teamMapper.teamToTeamResponse(team);
    }
}
