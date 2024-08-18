package inha.git.team.api.service;

import inha.git.team.api.controller.dto.request.CreateTeamRequest;
import inha.git.team.api.controller.dto.request.UpdateTeamRequest;
import inha.git.team.api.controller.dto.response.TeamResponse;
import inha.git.user.domain.User;

public interface TeamService {
    TeamResponse createTeam(User user, CreateTeamRequest createTeamRequest);
    TeamResponse updateTeam(User user, Integer teamIdx, UpdateTeamRequest updateTeamRequest);
}
