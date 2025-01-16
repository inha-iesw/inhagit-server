package inha.git.team.api.service;

import inha.git.team.api.controller.dto.request.*;
import inha.git.team.api.controller.dto.response.*;
import inha.git.user.domain.User;

import java.util.List;

public interface TeamCommentService {
    List<CommentWithRepliesResponse> getAllCommentsByTeamPostIdx(Integer postIdx);
    TeamCommentResponse createComment(User user, CreateCommentRequest createCommentRequest);
    TeamCommentResponse updateComment(User user, Integer commentIdx, UpdateCommentRequest updateCommentRequest);
    TeamCommentResponse deleteComment(User user, Integer commentIdx);
    TeamReplyCommentResponse createReplyComment(User user, CreateReplyCommentRequest createReplyCommentRequest);
    TeamReplyCommentResponse updateReplyComment(User user, Integer replyCommentIdx, UpdateCommentRequest updateCommentRequest);
    TeamReplyCommentResponse deleteReplyComment(User user, Integer replyCommentIdx);
}
