package inha.git.team.api.service;

import inha.git.team.api.controller.dto.request.CreateTeamPostRequest;
import inha.git.team.api.controller.dto.request.UpdateTeamPostRequest;
import inha.git.team.api.controller.dto.response.SearchTeamPostsResponse;
import inha.git.team.api.controller.dto.response.TeamPostResponse;
import inha.git.user.domain.User;
import org.springframework.data.domain.Page;

public interface TeamPostService {
    Page<SearchTeamPostsResponse> getTeamPosts(Integer page);
    TeamPostResponse createTeamPost(User user, CreateTeamPostRequest createTeamPostRequest);
    TeamPostResponse updateTeamPost(User user, Integer postIdx, UpdateTeamPostRequest updateTeamPostRequest);
    TeamPostResponse deleteTeamPost(User user, Integer postIdx);


}
