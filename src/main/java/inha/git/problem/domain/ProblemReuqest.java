package inha.git.problem.domain;

import inha.git.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


/**
 * ProblemRequest 엔티티는 애플리케이션의 문제 요청 정보를 나타냄.
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
@Builder
@Entity
@Table(name = "problem_request_tb")
public class ProblemReuqest extends BaseEntity {

    @Id
    @Column(name = "problem_request_id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50)
    private String title;

    @Column(nullable = false)
    private Integer type;

    @Column(nullable = false, name = "accept_at")
    private LocalDateTime acceptAt;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id")
    private Problem problem;
}
