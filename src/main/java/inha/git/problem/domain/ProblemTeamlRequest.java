package inha.git.problem.domain;


import inha.git.team.domain.Team;
import jakarta.persistence.*;
import lombok.*;

/**
 * ProblemPersonalRequest 엔티티는 애플리케이션의 팀 문제 신청 정보를 나타냄.
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
@Builder
@Entity
@Table(name = "problem_team_request_tb")
public class ProblemTeamlRequest {

    @Id
    @Column(name = "problem_personal_request_id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;
}
