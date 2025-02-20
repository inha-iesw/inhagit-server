package inha.git.project.api.service.comment.like;

import inha.git.project.api.controller.dto.request.CommentLikeRequest;
import inha.git.user.domain.User;

public interface ProjectCommentLikeService {
    String projectCommentLike(User user, CommentLikeRequest commentLikeRequest);
    String projectCommentLikeCancel(User user, CommentLikeRequest commentLikeRequest);
    String projectReplyCommentLike(User user, CommentLikeRequest commentLikeRequest);
    String projectReplyCommentLikeCancel(User user, CommentLikeRequest commentLikeRequest);
}
