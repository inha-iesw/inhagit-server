package inha.git.problem.domain;

import inha.git.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


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
public class ProblemRequest extends BaseEntity {

    @Id
    @Column(name = "problem_request_id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer type;

    @Column(name = "accept_at")
    private LocalDateTime acceptAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id")
    private Problem problem;

    // 개인 신청의 경우 연관관계 설정
    @OneToOne(mappedBy = "problemRequest", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ProblemPersonalRequest personalRequest;

    // 팀 신청의 경우 연관관계 설정
    @OneToOne(mappedBy = "problemRequest", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ProblemTeamRequest teamRequest;


    @OneToMany(mappedBy = "problemRequest", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProblemSubmit> problemSubmits = new ArrayList<>();

    public void setAcceptAt() {
        this.acceptAt = LocalDateTime.now();
    }
}