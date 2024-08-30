package inha.git.project.api.service;

import inha.git.project.api.controller.dto.request.RecommendRequest;
import inha.git.user.domain.User;

public interface ProjectRecommendService {

    String createProjectFoundingRecommend(User user, RecommendRequest recommendRequest);

    String createProjectPatentRecommend(User user, RecommendRequest recommendRequest);

    String createProjectRegistrationRecommend(User user, RecommendRequest recommendRequest);

    String cancelProjectFoundingRecommend(User user, RecommendRequest recommendRequest);

    String cancelProjectPatentRecommend(User user, RecommendRequest recommendRequest);

    String cancelProjectRegistrationRecommend(User user, RecommendRequest recommendRequest);

    Object getPatent(User user, String number);
}
