package inha.git.team.api.service;

import inha.git.mapping.domain.repository.TeamUserJpaRepository;
import inha.git.team.api.controller.dto.request.CreateTeamRequest;
import inha.git.team.api.controller.dto.response.TeamResponse;
import inha.git.team.api.mapper.TeamMapper;
import inha.git.team.domain.Team;
import inha.git.team.domain.repository.TeamJpaRepository;
import inha.git.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TeamServiceImpl implements TeamService {

    private final TeamJpaRepository teamJpaRepository;
    private final TeamUserJpaRepository teamUserJpaRepository;
    private final TeamMapper teamMapper;

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
        return teamMapper.teamToTeamResponse(team);
    }
}
