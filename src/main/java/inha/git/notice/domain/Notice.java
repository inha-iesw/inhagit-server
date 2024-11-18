package inha.git.notice.domain;

import inha.git.common.BaseEntity;
import inha.git.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


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

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 3000)
    private String contents;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder.Default
    @OneToMany(mappedBy = "notice", fetch = FetchType.LAZY)
    private List<NoticeAttachment> noticeAttachments = new ArrayList<>();

    public void updateNotice(String title, String contents) {
        this.title = title;
        this.contents = contents;
    }

    public void setUser(User user) {
        this.user = user;
        user.getNotices().add(this);  // 양방향 연관관계 설정
    }

    public void setNoticeAttachments(ArrayList<NoticeAttachment> noticeAttachments) {
        this.noticeAttachments = noticeAttachments;
        noticeAttachments.forEach(noticeAttachment -> noticeAttachment.setNotice(this));  // 양방향 연관관계 설정
    }
}
