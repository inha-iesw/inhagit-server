package inha.git.statistics.domain;

import inha.git.category.domain.Category;
import inha.git.field.domain.Field;
import inha.git.semester.domain.Semester;
import inha.git.statistics.domain.id.UserCountStatisticsId;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
@Builder
@Entity
@Table(name = "user_count_statistics_tb")
public class UserCountStatistics {

    @EmbeddedId
    private UserCountStatisticsId id;

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

    @Column(name = "user_project_count", nullable = false, columnDefinition = "int default 0")
    private Integer userProjectCount = 0;

    @Column(name = "user_question_count", nullable = false, columnDefinition = "int default 0")
    private Integer userQuestionCount = 0;

    @Column(name = "user_problem_count", nullable = false, columnDefinition = "int default 0")
    private Integer userProblemCount = 0;

    @Column(name = "user_team_count", nullable = false, columnDefinition = "int default 0")
    private Integer userTeamCount = 0;

    @Column(name = "user_patent_count", nullable = false, columnDefinition = "int default 0")
    private Integer userPatentCount = 0;

    @Column(name = "total_project_count", nullable = false, columnDefinition = "int default 0")
    private Integer totalProjectCount = 0;

    @Column(name = "total_github_project_count", nullable = false, columnDefinition = "int default 0")
    private Integer totalGithubProjectCount = 0;

    @Column(name = "total_question_count", nullable = false, columnDefinition = "int default 0")
    private Integer totalQuestionCount = 0;

    @Column(name = "total_problem_count", nullable = false, columnDefinition = "int default 0")
    private Integer totalProblemCount = 0;

    @Column(name = "total_team_count", nullable = false, columnDefinition = "int default 0")
    private Integer totalTeamCount = 0;

    @Column(name = "total_patent_count", nullable = false, columnDefinition = "int default 0")
    private Integer totalPatentCount = 0;




    public void increaseUserProjectCount() {
        userProjectCount++;
    }
    public void increaseUserQuestionCount() {
        userQuestionCount++;
    }

    public void increaseUserProblemCount() {
        userProblemCount++;
    }

    public void increaseUserTeamCount() {
        userTeamCount++;
    }

    public void increaseUserPatentCount() {
        userPatentCount++;
    }

    public void decreaseUserProjectCount() {
        if (userProjectCount > 0) {
            userProjectCount--;
        }
    }

    public void decreaseUserQuestionCount() {
        if (userQuestionCount > 0) {
            userQuestionCount--;
        }
    }

    public void decreaseUserProblemCount() {
        if (userProblemCount > 0) {
            userProblemCount--;
        }
    }

    public void decreaseUserTeamCount() {
        if (userTeamCount > 0) {
            userTeamCount--;
        }
    }

    public void decreaseUserPatentCount() {
        if (userPatentCount > 0) {
            userPatentCount--;
        }
    }

    public void increaseTotalProjectCount() {
        totalProjectCount++;
    }

    public void increaseTotalQuestionCount() {
        totalQuestionCount++;
    }

    public void increaseTotalProblemCount() {
        totalProblemCount++;
    }

    public void increaseTotalTeamCount() {
        totalTeamCount++;
    }

    public void increaseTotalPatentCount() {
        totalPatentCount++;
    }

    public void decreaseTotalProjectCount() {
        if (totalProjectCount > 0) {
            totalProjectCount--;
        }
    }

    public void decreaseTotalQuestionCount() {
        if (totalQuestionCount > 0) {
            totalQuestionCount--;
        }
    }

    public void decreaseTotalProblemCount() {
        if (totalProblemCount > 0) {
            totalProblemCount--;
        }
    }

    public void decreaseTotalTeamCount() {
        if (totalTeamCount > 0) {
            totalTeamCount--;
        }
    }

    public void decreaseTotalPatentCount() {
        if (totalPatentCount > 0) {
            totalPatentCount--;
        }
    }

    public void increaseTotalGithubProjectCount() {
        totalGithubProjectCount++;
    }

    public void decreaseTotalGithubProjectCount() {
        if (totalGithubProjectCount > 0) {
            totalGithubProjectCount--;
        }
    }
}
