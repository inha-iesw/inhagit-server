package inha.git.project.api.service.comment.reply;

import inha.git.project.api.controller.dto.request.CreateReplyCommentRequest;
import inha.git.project.api.controller.dto.request.UpdateCommentRequest;
import inha.git.project.api.controller.dto.response.ReplyCommentResponse;
import inha.git.user.domain.User;

public interface ProjectReplyCommentCommandService {
    ReplyCommentResponse createReply(User user, CreateReplyCommentRequest createReplyCommentRequest);
    ReplyCommentResponse updateReply(User user, Integer replyCommentIdx, UpdateCommentRequest updateCommentRequest);
    ReplyCommentResponse deleteReply(User user, Integer replyCommentIdx);
}
