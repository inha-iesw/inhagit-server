package inha.git.user.domain;

import inha.git.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


/**
 * Company 엔티티는 애플리케이션의 기업 증명 정보를 나타냄.
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
@Builder
@Entity
@Table(name = "company_tb")
public class Company {

    @Id
    @Column(name = "company_id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50)
    private String affiliation;

    @Column(nullable = false, length = 50, name = "evidence_file_path")
    private String evidenceFilePath;

    @Column(nullable = false, name = "accepted_at")
    private LocalDateTime acceptedAt;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}