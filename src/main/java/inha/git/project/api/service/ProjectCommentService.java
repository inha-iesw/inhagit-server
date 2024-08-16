package inha.git.project.api.service;

import inha.git.project.api.controller.api.request.CreateCommentRequest;
import inha.git.project.api.controller.api.request.UpdateCommentRequest;
import inha.git.project.api.controller.api.response.CreateCommentResponse;
import inha.git.project.api.controller.api.response.UpdateCommentResponse;
import inha.git.user.domain.User;

public interface ProjectCommentService {
    CreateCommentResponse createComment(User user, CreateCommentRequest createCommentRequest);
    UpdateCommentResponse updateComment(User user, Integer commentIdx, UpdateCommentRequest updateCommentRequest);
}

