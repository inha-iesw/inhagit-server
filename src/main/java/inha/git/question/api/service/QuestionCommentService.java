package inha.git.question.api.service;

import inha.git.question.api.controller.dto.request.CreateCommentRequest;
import inha.git.question.api.controller.dto.request.UpdateCommentRequest;
import inha.git.question.api.controller.dto.response.CommentResponse;
import inha.git.user.domain.User;

public interface QuestionCommentService {
    CommentResponse createComment(User user, CreateCommentRequest createCommentRequest);

    CommentResponse updateComment(User user, Integer commentIdx, UpdateCommentRequest updateCommentRequest);

    CommentResponse deleteComment(User user, Integer commentIdx);
}
