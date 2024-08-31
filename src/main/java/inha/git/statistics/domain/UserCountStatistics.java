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
}
