package inha.git.mapping.domain.id;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * ProjectCommentLikeId ProjectCommentLike 엔티티의 복합키를 나타냄.
 */
@Embeddable
@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class ProjectCommentLikeId implements Serializable {

    private Integer projectCommentId;

    private Integer userId;
}
