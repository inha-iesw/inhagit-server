package inha.git.notice.domain;

import jakarta.persistence.*;
import lombok.*;

/**
 * NoticeAttachment 엔티티는 공지사항 첨부파일 정보를 나타냄.
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
@Builder
@Entity
@Table(name = "notice_attachment_tb")
public class NoticeAttachment {

    @Id
    @Column(name = "notice_attachment_id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 200)
    private String originalFileName; // 원본 파일명

    @Column(nullable = false, length = 200)
    private String storedFileUrl; // 저장 파일명

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id", nullable = false)
    private Notice notice;

    public void setNotice(Notice notice) {
        this.notice = notice;
        notice.getNoticeAttachments().add(this);  // 양방향 연관관계 설정
    }
}
