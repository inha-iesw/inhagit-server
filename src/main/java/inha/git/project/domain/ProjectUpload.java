package inha.git.project.domain;

import inha.git.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;


/**
 * ProjectComment 엔티티는 애플리케이션의 프로젝트 업로드 정보를 나타냄.
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
@Builder
@Entity
@Table(name = "project_upload_tb")
public class ProjectUpload extends BaseEntity {
    @Id
    @Column(name = "project_upload_id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Setter
    @Column(nullable = false, length = 255, name = "directory_name")
    private String directoryName;

    @Setter
    @Column(nullable = false, length = 255, name = "zip_directory_name")
    private String zipDirectoryName;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

}
