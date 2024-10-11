package inha.git.statistics.domain;

import inha.git.college.domain.College;
import inha.git.field.domain.Field;
import inha.git.semester.domain.Semester;
import inha.git.statistics.domain.id.CollegeStatisticsStatisticsId;
import jakarta.persistence.*;
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

    @EmbeddedId
    private CollegeStatisticsStatisticsId id;

    @MapsId("collegeId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "college_id", nullable = false)
    private College college;

    @MapsId("semesterId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semester_id", nullable = false)
    private Semester semester;

    @MapsId("fieldId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "field_id", nullable = false)
    private Field field;

    @Column(name = "project_count", nullable = false)
    private Integer projectCount = 0;

    @Column(name = "github_project_count", nullable = false)
    private Integer githubProjectCount = 0;

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
        if (projectCount > 0) {
            projectCount--;
        }
    }

    public void decreaseQuestionCount() {
        if (questionCount > 0) {
            questionCount--;
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
        if (projectUserCount > 0) {
            projectUserCount--;
        }
    }

    public void decreaseQuestionUserCount() {
        if (questionUserCount > 0) {
            questionUserCount--;
        }
    }

    public void decreaseProblemUserCount() {
        if (problemUserCount > 0) {
            problemUserCount--;
        }
    }

    public void decreaseTeamUserCount() {
        if (teamUserCount > 0) {
            teamUserCount--;
        }
    }

    public void decreasePatentUserCount() {
        if (patentUserCount > 0) {
            patentUserCount--;
        }
    }

    public void increaseProblemParticipationCount() {
        problemParticipationCount++;
    }

    public void decreaseProblemParticipationCount() {
        if (problemParticipationCount > 0) {
            problemParticipationCount--;
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
