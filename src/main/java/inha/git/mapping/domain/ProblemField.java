package inha.git.mapping.domain;

import inha.git.field.domain.Field;
import inha.git.mapping.domain.id.ProblemFieldId;
import inha.git.problem.domain.Problem;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
@Builder
@Entity
@Table(name = "problem_field_tb")
public class ProblemField {

    @EmbeddedId
    private ProblemFieldId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("problemId")
    @JoinColumn(name = "problem_id")
    private Problem problem;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("fieldId")
    @JoinColumn(name = "field_id")
    private Field field;

    public void setProblem(Problem problem) {
        this.problem = problem;
        problem.getProblemFields().add(this);
    }
}
