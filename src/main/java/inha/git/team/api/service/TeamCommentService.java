package inha.git.team.api.service;

import inha.git.question.api.controller.dto.response.CommentResponse;
import inha.git.team.api.controller.dto.request.CreateCommentRequest;
import inha.git.team.api.controller.dto.request.CreateTeamPostRequest;
import inha.git.team.api.controller.dto.request.UpdateTeamPostRequest;
import inha.git.team.api.controller.dto.response.SearchTeamPostResponse;
import inha.git.team.api.controller.dto.response.SearchTeamPostsResponse;
import inha.git.team.api.controller.dto.response.TeamPostResponse;
import inha.git.user.domain.User;
import org.springframework.data.domain.Page;

public interface TeamCommentService {
    CommentResponse createComment(User user, CreateCommentRequest createCommentRequest);
}
