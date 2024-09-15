package inha.git.mapping.domain;

import inha.git.mapping.domain.id.ProjectCommentLikeId;
import inha.git.project.domain.ProjectComment;
import inha.git.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
@Builder
@Entity
@Table(name = "project_comment_like_tb")
public class ProjectCommentLike {

    @EmbeddedId
    private ProjectCommentLikeId id;

    @MapsId("projectCommentId")
    @ManyToOne
    @JoinColumn(name = "project_comment_id", nullable = false)
    private ProjectComment projectComment;

    @MapsId("userId")
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
