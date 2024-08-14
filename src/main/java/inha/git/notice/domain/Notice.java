package inha.git.notice.domain;

import inha.git.common.BaseEntity;
import inha.git.user.domain.User;
import jakarta.persistence.*;
import lombok.*;


/**
 * Notice 엔티티는 애플리케이션의 공지사항 정보를 나타냄.
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
@Builder
@Entity
@Table(name = "notice_tb")
public class Notice extends BaseEntity {

    @Id
    @Column(name = "notice_id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50)
    private String title;

    @Column(nullable = false, length = 255)
    private String contents;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public void updateNotice(String title, String contents) {
        this.title = title;
        this.contents = contents;
    }
}
