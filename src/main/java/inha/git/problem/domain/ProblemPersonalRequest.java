package inha.git.problem.domain;


import inha.git.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

/**
 * ProblemPersonalRequest 엔티티는 애플리케이션의 개인 문제 신청 정보를 나타냄.
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
@Builder
@Entity
@Table(name = "problem_personal_request_tb")
public class ProblemPersonalRequest {

    @Id
    @Column(name = "problem_personal_request_id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
