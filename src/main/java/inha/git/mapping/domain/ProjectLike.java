package inha.git.mapping.domain;

import inha.git.mapping.domain.id.ProjectLikeId;
import inha.git.project.domain.Project;
import inha.git.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

/**
 * PatentRecommend 엔티티는 애플리케이션의 특허 추천 매핑 정보를 나타냄.
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
@Builder
@Entity
@Table(name = "project_like_tb")
public class ProjectLike {

    @EmbeddedId
    private ProjectLikeId id;

    @MapsId("projectId")
    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @MapsId("userId")
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
