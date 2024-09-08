package inha.git.semester.domain;

import inha.git.common.BaseEntity;
import inha.git.problem.domain.Problem;
import inha.git.project.domain.Project;
import inha.git.question.domain.Question;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


/**
 * Field 엔티티는 애플리케이션의 필드 정보를 나타냄.
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
@Builder
@Entity
@Table(name = "semester_tb")
public class Semester extends BaseEntity {

    @Id
    @Column(name = "semester_id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 20)
    private String name;


    @OneToMany(mappedBy = "semester", fetch = FetchType.LAZY)
    private List<Project> projects = new ArrayList<>();

    @OneToMany(mappedBy = "semester", fetch = FetchType.LAZY)
    private List<Question> questions = new ArrayList<>();


    public void setName(String name) {
        this.name = name;
    }
}
