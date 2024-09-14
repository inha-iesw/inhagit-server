package inha.git.question.api.service;

import inha.git.question.api.controller.dto.request.CreateQuestionRequest;
import inha.git.question.api.controller.dto.request.SearchQuestionCond;
import inha.git.question.api.controller.dto.response.SearchQuestionResponse;
import inha.git.question.api.controller.dto.response.SearchQuestionsResponse;
import inha.git.question.api.controller.dto.request.UpdateQuestionRequest;
import inha.git.question.api.controller.dto.response.QuestionResponse;
import inha.git.user.domain.User;
import org.springframework.data.domain.Page;

public interface QuestionService {
    Page<SearchQuestionsResponse> getQuestions(Integer page);
    Page<SearchQuestionsResponse> getCondQuestions(SearchQuestionCond searchQuestionCond, Integer page);
    SearchQuestionResponse getQuestion(Integer questionIdx);
    QuestionResponse createQuestion(User user, CreateQuestionRequest createQuestionRequest);
    QuestionResponse updateQuestion(User user, Integer questionIdx, UpdateQuestionRequest updateQuestionRequest);
    QuestionResponse deleteQuestion(User user, Integer questionIdx);


}
