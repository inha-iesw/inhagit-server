package inha.git.mapping.domain;

import inha.git.mapping.domain.id.QuestionCommentLikeId;
import inha.git.question.domain.QuestionComment;
import inha.git.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

/**
 * QuestionCommentLike는 QuestionComment 엔티티의 좋아요를 나타냄.
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
@Builder
@Entity
@Table(name = "question_comment_like_tb")
public class QuestionCommentLike {

    @EmbeddedId
    private QuestionCommentLikeId id;

    @MapsId("questionCommentId")
    @ManyToOne
    @JoinColumn(name = "question_comment_id", nullable = false)
    private QuestionComment questionComment;

    @MapsId("userId")
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
