package inha.git.project.domain;

import inha.git.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * ProjectPatent 엔티티는 애플리케이션의 프로젝트 특허 정보를 나타냄.
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
@Builder
@Entity
@Table(name = "project_patent_tb")
public class ProjectPatent extends BaseEntity {

    @Id
    @Column(name = "project_patent_id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "registered_at")
    private LocalDateTime registeredAt;

    @Column(name = "registered_number")
    private String registeredNumber;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;


}
