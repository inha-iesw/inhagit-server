package inha.git.project.api.service;

import inha.git.project.api.controller.api.request.CreateCommentRequest;
import inha.git.project.api.controller.api.response.CreateCommentResponse;
import inha.git.user.domain.User;

public interface ProjectCommentService {
    CreateCommentResponse createComment(User user, CreateCommentRequest createCommentRequest);
}

