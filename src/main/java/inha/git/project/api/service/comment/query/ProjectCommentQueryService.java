package inha.git.project.api.service.comment.query;

import inha.git.project.api.controller.dto.response.CommentWithRepliesResponse;
import inha.git.user.domain.User;

import java.util.List;

public interface ProjectCommentQueryService {
    List<CommentWithRepliesResponse> getAllCommentsByProjectIdx(User user, Integer projectIdx);
}
