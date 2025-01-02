package inha.git.mapping.domain.id;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * QuestionLikeId 엔티티는 애플리케이션의 질문 좋아요 매핑 정보의 복합키를 나타냄.
 */
@Embeddable
@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class QuestionLikeId implements Serializable {

    private Integer questionId;
    private Integer userId;

}