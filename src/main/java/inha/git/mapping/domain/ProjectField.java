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

    @MapsId("projectId")
    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @MapsId("fieldId")
    @ManyToOne
    @JoinColumn(name = "field_id", nullable = false)
    private Field field;
}
