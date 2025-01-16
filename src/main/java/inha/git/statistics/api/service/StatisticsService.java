package inha.git.statistics.api.service;

import inha.git.category.domain.Category;
import inha.git.field.domain.Field;
import inha.git.semester.domain.Semester;
import inha.git.statistics.api.controller.dto.request.SearchCond;
import inha.git.statistics.api.controller.dto.response.*;
import inha.git.user.domain.User;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

public interface StatisticsService {
    void adjustCount(User user, List<Field> fields, Semester semester, Category category, Integer type, boolean isIncrease);
    ProjectStatisticsResponse getProjectStatistics(SearchCond searchCond);
    QuestionStatisticsResponse getQuestionStatistics(SearchCond searchCond);
    List<BatchCollegeStatisticsResponse> getBatchStatistics();
}
