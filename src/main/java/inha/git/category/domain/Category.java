package inha.git.category.domain;

import inha.git.common.BaseEntity;
import inha.git.project.domain.Project;
import inha.git.question.domain.Question;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


/**
 * Category 엔티티는 애플리케이션의 교과 / 비교과 / 기타 정보를 나타냄.
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
@Builder
@Entity
@Table(name = "category_tb")
public class Category extends BaseEntity {

    @Id
    @Column(name = "category_id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 20)
    private String name;


    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<Project> projects = new ArrayList<>();

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<Question> questions = new ArrayList<>();


    public void setName(String name) {
        this.name = name;
    }
}
