package inha.git.project.api.service.github;

import inha.git.project.api.controller.dto.request.CreateGithubProjectRequest;
import inha.git.project.api.controller.dto.response.ProjectResponse;
import inha.git.user.domain.User;

public interface GithubProjectService {
    ProjectResponse createGithubProject(User user, CreateGithubProjectRequest createGithubProjectRequest);
}
