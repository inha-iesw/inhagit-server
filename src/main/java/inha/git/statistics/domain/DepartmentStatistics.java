package inha.git.statistics.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

/**
 * DepartmentStatistics 엔티티는 애플리케이션의 학과별 통계 정보를 나타냄.
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
@Builder
@Entity
@Table(name = "department_statistics_tb")
public class DepartmentStatistics {

    @Id
    @Column(name = "department_id", nullable = false)
    private Integer departmentId;

    @Column(name = "project_count", nullable = false)
    private Integer projectCount = 0;

    @Column(name = "question_count", nullable = false)
    private Integer questionCount = 0;

    @Column(name = "team_count", nullable = false)
    private Integer teamCount = 0;

    @Column(name = "patent_count", nullable = false)
    private Integer patentCount = 0;

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
}
