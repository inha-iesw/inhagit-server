package inha.git.user.api.service;


import inha.git.project.api.controller.dto.response.SearchProjectsResponse;
import inha.git.question.api.controller.dto.response.SearchQuestionsResponse;
import inha.git.user.api.controller.dto.response.SearchUserResponse;
import inha.git.user.domain.User;
import org.springframework.data.domain.Page;

public interface UserService {
    SearchUserResponse getUser(User user);
    Page<SearchProjectsResponse> getUserProjects(User user, Integer page);
    Page<SearchQuestionsResponse> getUserQuestions(User user, Integer page);
}
