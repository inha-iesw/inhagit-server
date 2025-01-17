package inha.git.project.api.service.recommend;

import inha.git.project.api.controller.dto.request.RecommendRequest;
import inha.git.project.api.controller.dto.request.SearchPatentRequest;
import inha.git.project.api.controller.dto.response.SearchPatentResponse;
import inha.git.user.domain.User;

public interface ProjectRecommendService {
    String createProjectFoundingRecommend(User user, RecommendRequest recommendRequest);
    String createProjectLike(User user, RecommendRequest recommendRequest);
    String createProjectRegistrationRecommend(User user, RecommendRequest recommendRequest);
    String cancelProjectFoundingRecommend(User user, RecommendRequest recommendRequest);
    String cancelProjectLike(User user, RecommendRequest recommendRequest);
    String cancelProjectRegistrationRecommend(User user, RecommendRequest recommendRequest);
}
