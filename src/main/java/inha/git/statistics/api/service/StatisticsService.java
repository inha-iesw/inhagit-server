package inha.git.statistics.api.service;

import inha.git.statistics.api.controller.dto.response.*;
import inha.git.user.domain.User;

import java.util.List;

public interface StatisticsService {

    void increaseCount(User user, Integer type);

    void decreaseCount(User user, Integer type);

    List<HomeStatisticsResponse> getStatistics();
    ProjectStatisticsResponse getProjectStatistics(Integer idx);
    QuestionStatisticsResponse getQuestionStatistics(Integer idx);
    TeamStatisticsResponse getTeamStatistics(Integer idx);
    ProblemStatisticsResponse getProblemStatistics(Integer idx);


}
