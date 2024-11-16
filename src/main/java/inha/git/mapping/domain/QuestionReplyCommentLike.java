package inha.git.mapping.domain;

import inha.git.mapping.domain.id.QuestionReplyCommentLikeId;
import inha.git.question.domain.QuestionReplyComment;
import inha.git.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
@Builder
@Entity
@Table(name = "question_reply_comment_like_tb")
public class QuestionReplyCommentLike {

    @EmbeddedId
    private QuestionReplyCommentLikeId id;

    @MapsId("questionReplyCommentId")
    @ManyToOne
    @JoinColumn(name = "question_reply_comment_id", nullable = false)
    private QuestionReplyComment questionReplyComment;

    @MapsId("userId")
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
