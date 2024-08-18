package inha.git.question.domain;

import inha.git.common.BaseEntity;
import inha.git.mapping.domain.QuestionField;
import inha.git.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;


/**
 * Question 엔티티는 애플리케이션의 질문 정보를 나타냄.
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
@Builder
@Entity
@Table(name = "question_tb")
public class Question extends BaseEntity {

    @Id
    @Column(name = "question_id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Setter
    @Column(nullable = false, length = 50)
    private String title;

    @Setter
    @Column(nullable = false, length = 255)
    private String contents;

    @Setter
    @Column(nullable = false, length = 50, name = "subject_name")
    private String subjectName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuestionField> questionFields;

}
