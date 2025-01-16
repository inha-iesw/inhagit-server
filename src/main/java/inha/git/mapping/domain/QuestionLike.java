package inha.git.mapping.domain;

import inha.git.mapping.domain.id.QuestionLikeId;
import inha.git.question.domain.Question;
import inha.git.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

/**
 * QuestionLike 엔티티는 애플리케이션의 질문 좋아요 매핑 정보를 나타냄.
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
@Builder
@Entity
@Table(name = "question_like_tb")
public class QuestionLike {

    @EmbeddedId
    private QuestionLikeId id;

    @MapsId("questionId")
    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @MapsId("userId")
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
