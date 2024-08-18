package inha.git.team.api.service;

import inha.git.team.api.controller.dto.request.CreateTeamRequest;
import inha.git.team.api.controller.dto.request.UpdateTeamRequest;
import inha.git.team.api.controller.dto.response.SearchTeamResponse;
import inha.git.team.api.controller.dto.response.SearchTeamsResponse;
import inha.git.team.api.controller.dto.response.TeamResponse;
import inha.git.user.domain.User;

import java.util.List;

public interface TeamService {
    List<SearchTeamsResponse> getMyTeams(User user);
    SearchTeamResponse getTeam(Integer teamIdx);
    TeamResponse createTeam(User user, CreateTeamRequest createTeamRequest);
    TeamResponse updateTeam(User user, Integer teamIdx, UpdateTeamRequest updateTeamRequest);
    TeamResponse deleteTeam(User user, Integer teamIdx);


}
