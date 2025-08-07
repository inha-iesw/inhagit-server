package inha.git.user.domain;

import inha.git.user.domain.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Company 엔티티는 애플리케이션의 기업 증명 정보를 나타냄.
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
@Builder
@Entity
@Table(name = "user_ranking_tb")
public class UserRanking {

    @Id
    @Column(name = "user_id")
    private Integer userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "login_count", nullable = false)
    private Integer loginCount = 0;

    @Getter
    @Setter
    @Column(name = "github_noncurricular_count", nullable = false)
    private Integer githubNoncurricularCount = 0;

    @Getter
    @Setter
    @Column(name = "local_noncurricular_count", nullable = false)
    private Integer localNoncurricularCount = 0;

    @Getter
    @Setter
    @Column(name = "github_curricular_count", nullable = false)
    private Integer githubCurricularCount = 0;

    @Getter
    @Setter
    @Column(name = "local_curricular_count", nullable = false)
    private Integer localCurricularCount = 0;

    @Getter
    @Setter
    @Column(name = "question_count", nullable = false)
    private Integer questionCount = 0;

    @Getter
    @Setter
    @Column(name = "question_comment_count", nullable = false)
    private Integer questionCommentCount = 0;

    @Getter
    @Setter
    @Column(name = "patent_program_count", nullable = false)
    private Integer patentProgramCount = 0;

    @Getter
    @Setter
    @Column(name = "like_count", nullable = false)
    private Integer likeCount = 0;

    @Getter
    @Setter
    @Column(name = "recommend_count", nullable = false)
    private Integer recommendCount = 0;

    @Getter
    @Setter
    @Column(name = "project_star_count", nullable = false)
    private Integer projectStarCount = 0;

    @Getter
    @Setter
    @Column(name = "problem_participation_count", nullable = false)
    private Integer problemParticipationCount = 0;

    @Getter
    @Setter
    @Column(name = "admin_score", nullable = false)
    private Integer adminScore = 0;

    @Getter
    @Setter
    @Column(name = "total_score", nullable = false)
    private Integer totalScore = 0;

    @Column(name = "updatedat", nullable = false)
    private LocalDateTime updatedAt;

    @Column(length = 10)
    @Enumerated(EnumType.STRING)
    private Role role;

    public void increaseLoginCount() {
        this.loginCount++;
    }

    public void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
