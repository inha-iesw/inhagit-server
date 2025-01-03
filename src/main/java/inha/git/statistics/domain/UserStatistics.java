package inha.git.statistics.domain;

import inha.git.category.domain.Category;
import inha.git.field.domain.Field;
import inha.git.mapping.domain.id.FoundingRecommendId;
import inha.git.project.domain.Project;
import inha.git.semester.domain.Semester;
import inha.git.statistics.domain.id.UserStatisticsId;
import inha.git.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

/**
 * UserStatistics 엔티티는 애플리케이션의 유저별 통계 정보를 나타냄.
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
@Builder
@Entity
@Table(name = "user_statistics_tb")
public class UserStatistics {

    @EmbeddedId
    private UserStatisticsId id;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @MapsId("semesterId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semester_id", nullable = false)
    private Semester semester;

    @MapsId("fieldId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "field_id", nullable = false)
    private Field field;

    @MapsId("categoryId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "project_count", nullable = false)
    private Integer projectCount = 0;

    @Column(name = "github_project_count", nullable = false)
    private Integer githubProjectCount = 0;

    @Column(name = "question_count", nullable = false)
    private Integer questionCount = 0;

    @Column(name = "team_count", nullable = false)
    private Integer teamCount = 0;

    @Column(name = "patent_count", nullable = false)
    private Integer patentCount = 0;

    @Column(name = "problem_count", nullable = false)
    private Integer problemCount = 0;





    public void increaseProjectCount() {
        projectCount++;
    }

    public void increaseQuestionCount() {
        questionCount++;
    }

    public void decreaseProjectCount() {
        if (projectCount > 0) {
            projectCount--;
        }
    }

    public void decreaseQuestionCount() {
        if (questionCount > 0) {
            questionCount--;
        }
    }

    public void increaseTeamCount() {
        teamCount++;
    }

    public void decreaseTeamCount() {
        if (teamCount > 0) {
            teamCount--;
        }
    }

    public void increasePatentCount() {
        patentCount++;
    }

    public void decreasePatentCount() {
        if (patentCount > 0) {
            patentCount--;
        }
    }

    public void increaseProblemCount() {
        problemCount++;
    }

    public void decreaseProblemCount() {
        if (problemCount > 0) {
            problemCount--;
        }
    }

    public void increaseGithubProjectCount() {
        githubProjectCount++;
    }

    public void decreaseGithubProjectCount() {
        if (githubProjectCount > 0) {
            githubProjectCount--;
        }
    }




}
