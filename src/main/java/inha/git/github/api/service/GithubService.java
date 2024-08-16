package inha.git.github.api.service;

import inha.git.github.api.controller.dto.request.GitubTokenResquest;
import inha.git.user.domain.User;

public interface GithubService {
    String updateGithubToken(User user, GitubTokenResquest gitubTokenResquest);
}
