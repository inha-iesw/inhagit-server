package inha.git.statistics.api.service;

import inha.git.statistics.api.controller.dto.response.ProjectStatisticsResponse;
import inha.git.statistics.api.controller.dto.response.QuestionStatisticsResponse;
import inha.git.user.domain.User;

public interface StatisticsService {

    void increaseCount(User user, Integer type);

    void decreaseCount(User user, Integer type);

    ProjectStatisticsResponse getProjectStatistics(Integer idx);
    QuestionStatisticsResponse getQuestionStatistics(Integer idx);
}
