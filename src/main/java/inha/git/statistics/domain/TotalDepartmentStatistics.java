package inha.git.statistics.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

/**
 * TotalStatistics 엔티티는 애플리케이션의 전체 통계 정보를 나타냄.
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
@Builder
@Entity
@Table(name = "total_department_statistics_tb")
public class TotalDepartmentStatistics {

    @Id
    @Column(name = "total_department_statistics_id")
    private Integer departmentId;

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
        userProblemCount--;
    }

    public void decreaseUserTeamCount() {
        userTeamCount--;
    }

    public void decreaseUserPatentCount() {
        userPatentCount--;
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
        totalProblemCount--;
    }

    public void decreaseTotalTeamCount() {
        totalTeamCount--;
    }

    public void decreaseTotalPatentCount() {
        totalPatentCount--;
    }

    public void increaseTotalGithubProjectCount() {
        if (totalGithubProjectCount < 0) {
            totalGithubProjectCount = 0;
        }
    }

    public void decreaseTotalGithubProjectCount() {
        if (totalGithubProjectCount > 0) {
            totalGithubProjectCount--;
        }
    }

}
