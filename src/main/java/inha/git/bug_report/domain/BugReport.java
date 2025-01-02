package inha.git.bug_report.domain;

import inha.git.bug_report.domain.enums.BugStatus;
import inha.git.common.BaseEntity;
import inha.git.user.domain.User;
import jakarta.persistence.*;
import lombok.*;


/**
 * BugReport 엔티티는 애플리케이션의 버그 제보 정보를 나타냄.
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
@Builder
@Entity
@Table(name = "bug_report_tb")
public class BugReport extends BaseEntity {

    @Id
    @Column(name = "bug_report_id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Setter
    @Column(nullable = false, length = 200)
    private String title;

    @Setter
    private String contents;

    @Enumerated(EnumType.STRING)
    @Column(name = "bug_status", nullable = false, length = 20)
    private BugStatus bugStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public void setBugStatus(BugStatus bugStatus) {
        this.bugStatus = bugStatus;
    }
}
