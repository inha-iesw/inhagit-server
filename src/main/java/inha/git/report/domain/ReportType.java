package inha.git.report.domain;

import jakarta.persistence.*;
import lombok.*;

/**
 * ReportType 엔티티는 애플리케이션의 신고 타입 정보를 나타냄.
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
@Builder
@Entity
@Table(name = "report_type_tb")
public class ReportType {

    @Id
    @Column(name = "report_type_id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 30)
    private String name; // 신고 타입 이름
}
