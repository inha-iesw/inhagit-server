package inha.git.project.domain;

import inha.git.common.BaseEntity;
import inha.git.mapping.domain.ProjectField;
import inha.git.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


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

    @Column(length = 100, name = "repo_name")
    private String repoName;

    @Setter
    @Column(nullable = false, length = 50)
    private String title;

    @Setter
    @Column(nullable = false, length = 255)
    private String contents;

    @Setter
    @Column(nullable = false, length = 50, name = "subject_name")
    private String subjectName;

    @Column(nullable = false, name = "patent_recommend_count")
    private Integer patentRecommendCount = 0;

    @Column(nullable = false, name = "founding_recommend_count")
    private Integer foundingRecommendCount = 0;

    @Column(nullable = false, name = "registration_recommend_count")
    private Integer registrationRecommendCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder.Default
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectField> projectFields = new ArrayList<>();

    @OneToOne(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private ProjectUpload projectUpload;

    public void setFoundRecommendCount(int foundingRecommendCount) {
        this.foundingRecommendCount = foundingRecommendCount;
    }

    public void setPatentRecommendCount(int patentRecommendCount) {
        this.patentRecommendCount = patentRecommendCount;
    }

    public void setRegistrationRecommendCount(int registrationRecommendCount) {
        this.registrationRecommendCount = registrationRecommendCount;
    }
}
