package inha.git.question.api.service;

import inha.git.question.api.controller.dto.request.CreateQuestionRequest;
import inha.git.question.api.controller.dto.request.UpdateQuestionRequest;
import inha.git.question.api.controller.dto.response.QuestionResponse;
import inha.git.user.domain.User;

public interface QuestionService {
    QuestionResponse createQuestion(User user, CreateQuestionRequest createQuestionRequest);

    QuestionResponse updateQuestion(User user, Integer questionIdx, UpdateQuestionRequest updateQuestionRequest);
}
