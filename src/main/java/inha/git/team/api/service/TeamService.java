package inha.git.team.api.service;

import inha.git.team.api.controller.dto.request.ApproveRequestTeamRequest;
import inha.git.team.api.controller.dto.request.CreateTeamRequest;
import inha.git.team.api.controller.dto.request.RequestTeamRequest;
import inha.git.team.api.controller.dto.request.UpdateTeamRequest;
import inha.git.team.api.controller.dto.response.*;
import inha.git.user.domain.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface TeamService {
    List<SearchTeamsResponse> getMyTeams(User user);
    SearchTeamResponse getTeam(Integer teamIdx);
    TeamResponse createTeam(User user, CreateTeamRequest createTeamRequest);
    TeamResponse updateTeam(User user, Integer teamIdx, UpdateTeamRequest updateTeamRequest);
    TeamResponse deleteTeam(User user, Integer teamIdx);
    TeamResponse requestTeam(User user, RequestTeamRequest requestTeamRequest);
    TeamResponse approveRequestTeam(User user, ApproveRequestTeamRequest approveRequestTeamRequest);
    TeamResponse exitTeam(User user, Integer teamIdx);
    Page<SearchRequestResponse> getRequestTeams(User user, Integer teamIdx, Integer page);

}
