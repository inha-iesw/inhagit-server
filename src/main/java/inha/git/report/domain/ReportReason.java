package inha.git.report.domain;

import jakarta.persistence.*;
import lombok.*;

/**
 * ReportReason 엔티티는 애플리케이션의 신고 사유 정보를 나타냄.
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
@Builder
@Entity
@Table(name = "report_reason_tb")
public class ReportReason {

    @Id
    @Column(name = "report_reason_id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 30)
    private String name; // 신고 사유 이름
}
