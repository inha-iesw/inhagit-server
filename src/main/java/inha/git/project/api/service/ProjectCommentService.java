package inha.git.project.api.service;

import inha.git.project.api.controller.api.request.CreateCommentRequest;
import inha.git.project.api.controller.api.request.UpdateCommentRequest;
import inha.git.project.api.controller.api.response.CommentResponse;
import inha.git.user.domain.User;

public interface ProjectCommentService {
    CommentResponse createComment(User user, CreateCommentRequest createCommentRequest);
    CommentResponse updateComment(User user, Integer commentIdx, UpdateCommentRequest updateCommentRequest);

    CommentResponse deleteComment(User user, Integer commentIdx);
}

