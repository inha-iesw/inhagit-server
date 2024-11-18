package inha.git.report.domain;

import inha.git.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * Report 엔티티는 애플리케이션의 신고 정보를 나타냄.
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
@Builder
@Entity
@Table(name = "report_tb")
public class Report extends BaseEntity {


    @Id
    @Column(name = "report_id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_reason_id", nullable = false)
    private ReportReason reportReason; // 신고 사유와의 연관 관계

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_type_id", nullable = false)
    private ReportType reportType; // 신고 타입과의 연관 관계

    @Column(name = "reported_id", nullable = false)
    private Integer reportedId; // 신고된 게시글 또는 댓글 ID

    @Column(name = "reporter_id", nullable = false)
    private Integer reporterId; // 신고한 사용자 ID

    @Column(name = "reported_user_id", nullable = false)
    private Integer reportedUserId;

    @Column(name = "description")
    private String description; // 추가적인 신고 설명 (선택 사항)

}
