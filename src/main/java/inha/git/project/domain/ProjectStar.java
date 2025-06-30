package inha.git.project.domain;

import inha.git.common.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ProjectStar 엔티티는 애플리케이션의 프로젝트 Star 정보를 나타냄.
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
@Builder
@Entity
@Table(name = "project_star_tb")
public class ProjectStar extends BaseEntity {

    @Id
    @Column(name = "project_star_id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // StarIdx

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="project_id", nullable = false)
    private Project project; // projectIdx

    @Column(name = "accept_at", nullable = true)
    private LocalDateTime acceptAt; // 승인일

    @Schema(description = "상태", example = "ACTIVE")
    BaseEntity.State state;

    public void setAcceptedAt(LocalDateTime now) {
        this.acceptAt = now;
    }
}
