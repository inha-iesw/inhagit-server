package inha.git.question.api.service;

import inha.git.question.api.controller.dto.request.CreateCommentRequest;
import inha.git.question.api.controller.dto.request.CreateReplyCommentRequest;
import inha.git.question.api.controller.dto.request.UpdateCommentRequest;
import inha.git.question.api.controller.dto.response.CommentResponse;
import inha.git.question.api.controller.dto.response.ReplyCommentResponse;
import inha.git.user.domain.User;

public interface QuestionCommentService {
    CommentResponse createComment(User user, CreateCommentRequest createCommentRequest);

    CommentResponse updateComment(User user, Integer commentIdx, UpdateCommentRequest updateCommentRequest);

    CommentResponse deleteComment(User user, Integer commentIdx);
    ReplyCommentResponse createReplyComment(User user, CreateReplyCommentRequest createReplyCommentRequest);
    ReplyCommentResponse updateReplyComment(User user, Integer replyCommentIdx, UpdateCommentRequest updateCommentRequest);
    ReplyCommentResponse deleteReplyComment(User user, Integer replyCommentIdx);
}
