package inha.git.mapping.domain;

import inha.git.field.domain.Field;
import inha.git.mapping.domain.id.QuestionFieldId;
import inha.git.question.domain.Question;
import jakarta.persistence.*;
import lombok.*;


/**
 * QuestionField 엔티티는 애플리케이션의 질문과 필드 매핑 정보를 나타냄.
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
@Builder
@Entity
@Table(name = "question_field_tb")
public class QuestionField {

    @EmbeddedId
    private QuestionFieldId id;

    @MapsId("questionId")
    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @MapsId("fieldId")
    @ManyToOne
    @JoinColumn(name = "field_id", nullable = false)
    private Field field;
}
