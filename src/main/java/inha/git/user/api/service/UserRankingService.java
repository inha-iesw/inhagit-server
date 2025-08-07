package inha.git.user.api.service;

import inha.git.user.api.controller.dto.response.UserRankingResponse;
import inha.git.user.domain.User;

import java.util.List;

public interface UserRankingService {
    void updateAllTotalScores(); // 모든 유저의 totalScore 계산

    UserRankingResponse getUserRanking (User user);

    void updateAdminScore(User user, int newAdminScore);

    List<UserRankingResponse> getUserRankings();
}
