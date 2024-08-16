package inha.git.mapping.domain;

import inha.git.field.domain.Field;
import inha.git.mapping.domain.id.ProjectFieldId;
import inha.git.project.domain.Project;
import jakarta.persistence.*;
import lombok.*;


/**
 * ProjectField 엔티티는 애플리케이션의 프로젝트와 필드 매핑 정보를 나타냄.
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
@Builder
@Entity
@Table(name = "project_field_tb")
public class ProjectField  {

    @EmbeddedId
    private ProjectFieldId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("projectId")
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("fieldId")
    @JoinColumn(name = "field_id")
    private Field field;


    public void setProject(Project project) {
        this.project = project;
    }

    public void setField(Field field) {
        this.field = field;
    }
}
