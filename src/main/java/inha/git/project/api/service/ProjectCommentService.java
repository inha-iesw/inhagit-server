package inha.git.project.api.service;

import inha.git.project.api.controller.api.request.CreateCommentRequest;
import inha.git.project.api.controller.api.request.CreateReplyCommentRequest;
import inha.git.project.api.controller.api.request.UpdateCommentRequest;
import inha.git.project.api.controller.api.response.CommentResponse;
import inha.git.project.api.controller.api.response.ReplyCommentResponse;
import inha.git.user.domain.User;

public interface ProjectCommentService {
    CommentResponse createComment(User user, CreateCommentRequest createCommentRequest);
    CommentResponse updateComment(User user, Integer commentIdx, UpdateCommentRequest updateCommentRequest);

    CommentResponse deleteComment(User user, Integer commentIdx);

    ReplyCommentResponse createReply(User user, CreateReplyCommentRequest createReplyCommentRequest);

    ReplyCommentResponse updateReply(User user, Integer replyCommentIdx, UpdateCommentRequest updateCommentRequest);

    ReplyCommentResponse deleteReply(User user, Integer replyCommentIdx);
}

