package inha.git.problem.domain;

import inha.git.common.BaseEntity;
import inha.git.mapping.domain.ProblemField;
import inha.git.mapping.domain.ProjectField;
import inha.git.notice.domain.NoticeAttachment;
import inha.git.problem.domain.enums.ProblemStatus;
import inha.git.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Setter
    @Column(nullable = false, length = 200)
    private String title;

    @Setter
    @Column(nullable = false, length = 3000)
    private String contents;

    @Setter
    @Column(nullable = false)
    private String duration;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20, columnDefinition = "varchar(20) default 'PROGRESS'")
    private ProblemStatus status;

    @Column(name = "has_attachment",nullable = false, columnDefinition = "boolean default false")
    private Boolean hasAttachment;

    @Column(name = "participant_count", nullable = false, columnDefinition = "int default 0")
    private Integer participantCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder.Default
    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProblemField> problemFields = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProblemRequest> problemRequests = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProblemAttachment> problemAttachments = new ArrayList<>();

    public void setHasAttachment(boolean hasAttachment) {
        this.hasAttachment = hasAttachment;
    }
}
