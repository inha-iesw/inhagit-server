package inha.git.problem.domain;

import inha.git.common.BaseEntity;
import inha.git.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


/**
 * Problem 엔티티는 애플리케이션의 문제 정보를 나타냄.
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
@Builder
@Entity
@Table(name = "problem_tb")
public class Problem extends BaseEntity {

    @Id
    @Column(name = "problem_id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50)
    private String title;

    @Column(nullable = false, length = 255)
    private String contents;

    @Column(nullable = false)
    private LocalDateTime duration;

    @Column(nullable = false, length = 255, name = "file_path")
    private String filePath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
