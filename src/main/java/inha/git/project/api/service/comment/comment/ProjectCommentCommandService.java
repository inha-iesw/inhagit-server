package inha.git.project.api.service.comment.comment;

import inha.git.project.api.controller.dto.request.CreateCommentRequest;
import inha.git.project.api.controller.dto.request.UpdateCommentRequest;
import inha.git.project.api.controller.dto.response.CommentResponse;
import inha.git.user.domain.User;

public interface ProjectCommentCommandService {
    CommentResponse createComment(User user, CreateCommentRequest createCommentRequest);
    CommentResponse updateComment(User user, Integer commentIdx, UpdateCommentRequest updateCommentRequest);
    CommentResponse deleteComment(User user, Integer commentIdx);
}
