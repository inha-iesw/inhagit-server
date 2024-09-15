package inha.git.project.api.service;

import inha.git.project.api.controller.dto.request.CommentLikeRequest;
import inha.git.project.api.controller.dto.request.CreateCommentRequest;
import inha.git.project.api.controller.dto.request.CreateReplyCommentRequest;
import inha.git.project.api.controller.dto.request.UpdateCommentRequest;
import inha.git.project.api.controller.dto.response.CommentResponse;
import inha.git.project.api.controller.dto.response.CommentWithRepliesResponse;
import inha.git.project.api.controller.dto.response.ReplyCommentResponse;
import inha.git.user.domain.User;

import java.util.List;

public interface ProjectCommentService {
    List<CommentWithRepliesResponse> getAllCommentsByProjectIdx(Integer projectIdx);
    CommentResponse createComment(User user, CreateCommentRequest createCommentRequest);
    CommentResponse updateComment(User user, Integer commentIdx, UpdateCommentRequest updateCommentRequest);

    CommentResponse deleteComment(User user, Integer commentIdx);

    ReplyCommentResponse createReply(User user, CreateReplyCommentRequest createReplyCommentRequest);

    ReplyCommentResponse updateReply(User user, Integer replyCommentIdx, UpdateCommentRequest updateCommentRequest);

    ReplyCommentResponse deleteReply(User user, Integer replyCommentIdx);

    String projectCommentLike(User user, CommentLikeRequest commentLikeRequest);
    String projectCommentLikeCancel(User user, CommentLikeRequest commentLikeRequest);

    String projectReplyCommentLike(User user, CommentLikeRequest commentLikeRequest);

    String projectReplyCommentLikeCancel(User user, CommentLikeRequest commentLikeRequest);
}

