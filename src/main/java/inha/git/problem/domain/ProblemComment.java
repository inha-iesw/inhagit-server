package inha.git.problem.domain;

import inha.git.common.BaseEntity;
import inha.git.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

/**
 * ProblemComment 엔티티는 애플리케이션의 문제 댓글 정보를 나타냄.
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
@Builder
@Entity
@Table(name = "problem_comment_tb")
public class ProblemComment extends BaseEntity {
    @Id
    @Column(name = "problem_comment_id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 255)
    private String contents;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id")
    private Problem problem;
}
