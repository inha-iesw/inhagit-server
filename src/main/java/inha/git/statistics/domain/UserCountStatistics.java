package inha.git.statistics.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
@Builder
@Entity
@Table(name = "user_count_statistics_tb")
public class UserCountStatistics {

    @Id
    @Column(name = "user_count_statistics_id", nullable = false)
    private Integer id = 1;

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
        userProjectCount--;
    }

    public void decreaseUserQuestionCount() {
        userQuestionCount--;
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
        totalProjectCount--;
    }

    public void decreaseTotalQuestionCount() {
        totalQuestionCount--;
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
}
