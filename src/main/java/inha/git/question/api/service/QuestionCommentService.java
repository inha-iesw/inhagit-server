package inha.git.question.api.service;

import inha.git.question.api.controller.dto.request.*;
import inha.git.question.api.controller.dto.response.CommentResponse;
import inha.git.question.api.controller.dto.response.ReplyCommentResponse;
import inha.git.user.domain.User;

import java.util.List;

public interface QuestionCommentService {
    List<CommentWithRepliesResponse> getAllCommentsByQuestionIdx(User user, Integer questionIdx);
    CommentResponse createComment(User user, CreateCommentRequest createCommentRequest);

    CommentResponse updateComment(User user, Integer commentIdx, UpdateCommentRequest updateCommentRequest);

    CommentResponse deleteComment(User user, Integer commentIdx);
    ReplyCommentResponse createReplyComment(User user, CreateReplyCommentRequest createReplyCommentRequest);
    ReplyCommentResponse updateReplyComment(User user, Integer replyCommentIdx, UpdateCommentRequest updateCommentRequest);
    ReplyCommentResponse deleteReplyComment(User user, Integer replyCommentIdx);

    String questionCommentLike(User user, CommentLikeRequest commentLikeRequest);
    String questionCommentLikeCancel(User user, CommentLikeRequest commentLikeRequest);
    String questionReplyCommentLike(User user, CommentLikeRequest commentLikeRequest);
    String questionReplyCommentLikeCancel(User user, CommentLikeRequest commentLikeRequest);
}
