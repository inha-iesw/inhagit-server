package inha.git.github.api.service;

import inha.git.github.api.controller.dto.request.GitubTokenResquest;
import inha.git.github.api.controller.dto.response.GithubRepositoryResponse;
import inha.git.project.api.controller.dto.response.SearchFileResponse;
import inha.git.user.domain.User;

import java.util.List;

public interface GithubService {
    String updateGithubToken(User user, GitubTokenResquest gitubTokenResquest);
    List<GithubRepositoryResponse> getGithubRepositories(User user);
    List<SearchFileResponse> getGithubFiles(User user, Integer projectIdx, String path);
}
