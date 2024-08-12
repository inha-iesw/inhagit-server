package inha.git.question.domain;

import inha.git.common.BaseEntity;
import inha.git.problem.domain.ProblemComment;
import inha.git.user.domain.User;
import jakarta.persistence.*;
import lombok.*;


/**
 * QuestionReplyComment는 엔티티는 애플리케이션의 질문 대댓글 정보를 나타냄.
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
@Builder
@Entity
@Table(name = "problem_reply_comment_tb")
public class QuestionReplyComment extends BaseEntity {

    @Id
    @Column(name = "problem_reply_comment_id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50)
    private String title;

    @Column(nullable = false, length = 255)
    private String contents;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_comment_id")
    private QuestionComment questionComment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
