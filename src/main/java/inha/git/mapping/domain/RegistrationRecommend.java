package inha.git.mapping.domain;

import inha.git.mapping.domain.id.RegistrationRecommendId;
import inha.git.project.domain.Project;
import inha.git.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

/**
 * RegistrationRecommend 엔티티는 애플리케이션의 등록 추천 매핑 정보를 나타냄.
 */

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
@Builder
@Entity
@Table(name = "registration_recommend_tb")

public class RegistrationRecommend {

    @EmbeddedId
    private RegistrationRecommendId id;

    @MapsId("projectId")
    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @MapsId("userId")
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
