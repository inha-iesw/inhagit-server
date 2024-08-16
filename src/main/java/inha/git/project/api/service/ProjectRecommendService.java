package inha.git.project.api.service;

import inha.git.project.api.controller.api.request.RecommendRequest;
import inha.git.user.domain.User;

public interface ProjectRecommendService {

    String createProjectFoundingRecommend(User user, RecommendRequest recommendRequest);
}
