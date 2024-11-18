package inha.git.user.api.service;


import inha.git.problem.api.controller.dto.response.SearchProblemsResponse;
import inha.git.project.api.controller.dto.response.SearchProjectsResponse;
import inha.git.question.api.controller.dto.response.SearchQuestionsResponse;
import inha.git.report.api.controller.dto.response.SearchReportResponse;
import inha.git.team.api.controller.dto.response.SearchMyTeamsResponse;
import inha.git.user.api.controller.dto.request.UpdatePwRequest;
import inha.git.user.api.controller.dto.response.SearchUserResponse;
import inha.git.user.api.controller.dto.response.UserResponse;
import inha.git.user.domain.User;
import org.springframework.data.domain.Page;

public interface UserService {
    SearchUserResponse getUser(Integer userIdx);
    Page<SearchProjectsResponse> getUserProjects(User user, Integer userIdx, Integer page);
    Page<SearchQuestionsResponse> getUserQuestions(User user, Integer userIdx, Integer page);
    Page<SearchMyTeamsResponse> getUserTeams(User user, Integer userIdx, Integer page);
    Page<SearchProblemsResponse> getUserProblems(User user, Integer userIdx, Integer page);
    Page<SearchReportResponse> getUserReports(User user, Integer userIdx, Integer page);
    UserResponse changePassword(Integer id, UpdatePwRequest updatePwRequest);



}
