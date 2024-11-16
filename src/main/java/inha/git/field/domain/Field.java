package inha.git.field.domain;

import inha.git.common.BaseEntity;
import inha.git.mapping.domain.ProjectField;
import inha.git.mapping.domain.id.ProjectFieldId;
import inha.git.project.domain.Project;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;


/**
 * Field 엔티티는 애플리케이션의 필드 정보를 나타냄.
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
@Builder
@Entity
@Table(name = "field_tb")
public class Field extends BaseEntity {

    @Id
    @Column(name = "field_id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 20)
    private String name;

    public void setName(String name) {
        this.name = name;
    }

    @OneToMany(mappedBy = "field", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProjectField> projectFields = new HashSet<>();


    public void addProject(Project project) {
        ProjectField projectField = new ProjectField(new ProjectFieldId(project.getId(), this.id), project, this);
        projectFields.add(projectField);
        if (!project.getProjectFields().contains(projectField)) {
            project.getProjectFields().add(projectField);  // 양방향 연관관계 설정
        }
    }

}
