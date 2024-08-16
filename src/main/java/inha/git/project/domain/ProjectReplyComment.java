package inha.git.project.domain;

import inha.git.common.BaseEntity;
import inha.git.user.domain.User;
import jakarta.persistence.*;
import lombok.*;


/**
 * ProjectComment 엔티티는 애플리케이션의 프로젝트 대댓글 정보를 나타냄.
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
@Builder
@Entity
@Table(name = "project_reply_comment_tb")
public class ProjectReplyComment extends BaseEntity {
    @Id
    @Column(name = "project_reply_comment_id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 255)
    private String contents;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_comment_id")
    private ProjectComment projectComment;

    public void setContents(String contents) {
        this.contents = contents;
    }
}
