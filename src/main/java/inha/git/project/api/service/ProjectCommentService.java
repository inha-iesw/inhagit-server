package inha.git.project.api.service;

import inha.git.project.api.controller.api.dto.request.CreateCommentRequest;
import inha.git.project.api.controller.api.dto.request.CreateReplyCommentRequest;
import inha.git.project.api.controller.api.dto.request.UpdateCommentRequest;
import inha.git.project.api.controller.api.dto.response.CommentResponse;
import inha.git.project.api.controller.api.dto.response.CommentWithRepliesResponse;
import inha.git.project.api.controller.api.dto.response.ReplyCommentResponse;
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
}

