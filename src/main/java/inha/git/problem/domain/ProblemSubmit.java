package inha.git.problem.domain;

import inha.git.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Problem 엔티티는 애플리케이션의 문제 제출 정보를 나타냄.
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
@Builder
@Entity
@Table(name = "problem_submit_tb")
public class ProblemSubmit extends BaseEntity {

    @Id
    @Column(name = "problem_submit_id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer projectId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_request_id")
    private ProblemRequest problemRequest;

    public void setProblemRequest(ProblemRequest problemRequest) {
        this.problemRequest = problemRequest;
        problemRequest.getProblemSubmits().add(this);  // 양방향 연관관계 설정
    }
}
