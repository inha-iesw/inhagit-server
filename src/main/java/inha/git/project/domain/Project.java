package inha.git.project.domain;

import inha.git.common.BaseEntity;
import inha.git.user.domain.User;
import jakarta.persistence.*;
import lombok.*;


/**
 * Project 엔티티는 애플리케이션의 프로젝트 정보를 나타냄.
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
@Builder
@Entity
@Table(name = "project_tb")
public class Project extends BaseEntity {

    @Id
    @Column(name = "project_id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50, name = "repo_name")
    private String repoName;

    @Column(nullable = false, length = 50)
    private String title;

    @Column(nullable = false, length = 255)
    private String contents;

    @Column(nullable = false, length = 50, name = "subject_name")
    private String subjectName;

    @Column(nullable = false, name = "patent_recommend_count")
    private Integer patentRecommendCount;

    @Column(nullable = false, name = "founding_recommend_count")
    private Integer foundingRecommendCount;

    @Column(nullable = false, name = "registration_recommend_count")
    private Integer registrationRecommendCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
