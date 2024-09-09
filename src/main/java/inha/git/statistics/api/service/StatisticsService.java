package inha.git.statistics.api.service;

import inha.git.field.domain.Field;
import inha.git.semester.domain.Semester;
import inha.git.statistics.api.controller.dto.request.ProjectSearchCond;
import inha.git.statistics.api.controller.dto.response.*;
import inha.git.user.domain.User;

import java.util.List;

public interface StatisticsService {

    void increaseCount(User user, List<Field> fields, Semester semester, Integer type);

    void decreaseCount(User user, List<Field> fields, Semester semester, Integer type);

    List<HomeStatisticsResponse> getStatistics();
    ProjectStatisticsResponse getProjectStatistics(ProjectSearchCond projectSearchCond);
    QuestionStatisticsResponse getQuestionStatistics(Integer idx);
    TeamStatisticsResponse getTeamStatistics(Integer idx);
    ProblemStatisticsResponse getProblemStatistics(Integer idx);


}
