package inha.git.mapping.domain.id;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * QuestionReplyCommentLikeId는 QuestionReplyCommentLike 엔티티의 복합키를 나타냄.
 */
@Embeddable
@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class QuestionReplyCommentLikeId implements Serializable {

    private Integer questionReplyCommentId;

    private Integer userId;
}
