package inha.git.statistics.domain;

import inha.git.statistics.domain.enums.StatisticsType;
import jakarta.persistence.*;
import lombok.*;

/**
 * Statistics 엔티티는 애플리케이션의 통계 정보를 나타냄.
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
@Builder
@Entity
@Table(name = "statistics_tb")
public class Statistics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "statistics_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "statistics_type", nullable = false)
    private StatisticsType statisticsType;


    @Column(name = "target_id")
    private Integer targetId;

    @Column(name = "semester_id", nullable = false)
    private Integer semesterId;

    @Column(name = "field_id", nullable = false)
    private Integer fieldId;

    @Column(name = "category_id", nullable = false)
    private Integer categoryId;

    @Column(name = "local_project_count", nullable = false)
    private Integer localProjectCount = 0;

    @Column(name = "github_project_count", nullable = false)
    private Integer githubProjectCount = 0;

    @Column(name = "question_count", nullable = false)
    private Integer questionCount = 0;

    @Column(name = "project_participation_count", nullable = false)
    private Integer projectParticipationCount = 0;

    @Column(name = "question_participation_count", nullable = false)
    private Integer questionParticipationCount = 0;



    public void incrementLocalProjectCount() {
        this.localProjectCount++;
    }

    public void incrementGithubProjectCount() {
        this.githubProjectCount++;
    }

    public void incrementQuestionCount() {
        this.questionCount++;
    }

    public void incrementProjectParticipation() {
        this.projectParticipationCount++;
    }

    public void incrementQuestionParticipation() {
        this.questionParticipationCount++;
    }

    public void decrementLocalProjectCount() {
        if (localProjectCount > 0) {
            localProjectCount--;
        }
    }

    public void decrementGithubProjectCount() {
        if (githubProjectCount > 0) {
            githubProjectCount--;
        }
    }

    public void decrementQuestionCount() {
        if (questionCount > 0) {
            questionCount--;
        }
    }

    public void decrementProjectParticipation() {
        if (projectParticipationCount > 0) {
            projectParticipationCount--;
        }
    }

    public void decrementQuestionParticipation() {
        if (questionParticipationCount > 0) {
            questionParticipationCount--;
        }
    }

    public Integer getTotalProjectCount() {
        return localProjectCount + githubProjectCount;
    }

    public void setProjectParticipationCount(int size) {
        this.projectParticipationCount = size;
    }
}