package inha.git.mapping.domain.id;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * PatentRecommedId PatentRecommed 엔티티의 복합키를 나타냄.
 */
@Embeddable
@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class PatentRecommedId implements Serializable {

    private Long projectId;
    private Long userId;

}