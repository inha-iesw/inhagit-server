package inha.git.admin.api.service;

import inha.git.admin.api.controller.dto.request.UserRankingScoreRequest;
import inha.git.user.api.controller.dto.response.UserRankingResponse;
import inha.git.user.domain.User;

public interface AdminScoreService {

    UserRankingResponse updateScore(UserRankingScoreRequest userRankingScoreRequest);

}
