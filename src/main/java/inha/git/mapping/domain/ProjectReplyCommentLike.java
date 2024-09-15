package inha.git.mapping.domain;

import inha.git.mapping.domain.id.ProjectReplyCommentLikeId;
import inha.git.project.domain.ProjectReplyComment;
import inha.git.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
@Builder
@Entity
@Table(name = "project_reply_comment_like_tb")
public class ProjectReplyCommentLike {

    @EmbeddedId
    private ProjectReplyCommentLikeId id;

    @MapsId("projectReplyCommentId")
    @ManyToOne
    @JoinColumn(name = "project_reply_comment_id", nullable = false)
    private ProjectReplyComment projectReplyComment;

    @MapsId("userId")
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
