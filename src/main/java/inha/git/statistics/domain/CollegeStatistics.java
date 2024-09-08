package inha.git.statistics.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

/**
 * CollegeStatistics 엔티티는 애플리케이션의 단과대별 통계 정보를 나타냄.
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
@Builder
@Entity
@Table(name = "college_statistics_tb")
public class CollegeStatistics {

    @Id
    @Column(name = "college_id", nullable = false)
    private Integer collegeId;

    @Column(name = "project_count", nullable = false)
    private Integer projectCount = 0;

    @Column(name = "question_count", nullable = false)
    private Integer questionCount = 0;

    @Column(name = "problem_count", nullable = false)
    private Integer problemCount = 0;

    @Column(name = "team_count", nullable = false)
    private Integer teamCount = 0;

    @Column(name = "patent_count", nullable = false)
    private Integer patentCount = 0;

    @Column(name = "project_user_count", nullable = false)
    private Integer projectUserCount = 0;

    @Column(name = "question_user_count", nullable = false)
    private Integer questionUserCount = 0;

    @Column(name = "problem_user_count", nullable = false)
    private Integer problemUserCount = 0;

    @Column(name = "team_user_count", nullable = false)
    private Integer teamUserCount = 0;

    @Column(name = "patent_user_count", nullable = false)
    private Integer patentUserCount = 0;

    @Column(name = "problem_participation_count", nullable = false)
    private Integer problemParticipationCount = 0;

    public void increaseProjectCount() {
        projectCount++;
    }

    public void increaseQuestionCount() {
        questionCount++;
    }

    public void decreaseProjectCount() {
        projectCount--;
    }

    public void decreaseQuestionCount() {
        questionCount--;
    }


    public void increaseProblemCount() {
        problemCount++;
    }

    public void decreaseProblemCount() {
        problemCount--;
    }

    public void increaseTeamCount() {
        teamCount++;
    }

    public void decreaseTeamCount() {
        teamCount--;
    }

    public void increasePatentCount() {
        patentCount++;
    }

    public void decreasePatentCount() {
        patentCount--;
    }

    public void increaseProjectUserCount() {
        projectUserCount++;
    }

    public void increaseQuestionUserCount() {
        questionUserCount++;
    }

    public void increaseProblemUserCount() {
        problemUserCount++;
    }

    public void increaseTeamUserCount() {
        teamUserCount++;
    }

    public void increasePatentUserCount() {
        patentUserCount++;
    }

    public void decreaseProjectUserCount() {
        projectUserCount--;
    }

    public void decreaseQuestionUserCount() {
        questionUserCount--;
    }

    public void decreaseProblemUserCount() {
        problemUserCount--;
    }

    public void decreaseTeamUserCount() {
        teamUserCount--;
    }

    public void decreasePatentUserCount() {
        patentUserCount--;
    }

    public void increaseProblemParticipationCount() {
        problemParticipationCount++;
    }

    public void decreaseProblemParticipationCount() {
        problemParticipationCount--;
    }

}
