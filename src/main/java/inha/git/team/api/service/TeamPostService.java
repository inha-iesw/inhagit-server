package inha.git.team.api.service;

import inha.git.team.api.controller.dto.request.CreateTeamPostRequest;
import inha.git.team.api.controller.dto.response.TeamPostResponse;
import inha.git.user.domain.User;

public interface TeamPostService {
    TeamPostResponse createTeamPost(User user, CreateTeamPostRequest createTeamPostRequest);
}
