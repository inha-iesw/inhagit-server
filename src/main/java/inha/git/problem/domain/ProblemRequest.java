package inha.git.problem.domain;

import inha.git.common.BaseEntity;
import inha.git.problem.domain.enums.ProblemRequestStatus;
import inha.git.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * ProblemRequest 엔티티는 애플리케이션의 문제 요청 정보를 나타냄.
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
@Builder
@Entity
@Table(name = "problem_request_tb")
public class ProblemRequest extends BaseEntity {

    @Id
    @Column(name = "problem_request_id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 3000)
    private String contents;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20, name = "problem_request_status", columnDefinition = "varchar(20) default 'REQUEST'")
    private ProblemRequestStatus problemRequestStatus;

    @Column(name = "original_fileName", length = 200)
    private String originalFileName; // 원본 파일명

    @Column(name = "stored_file_url", length = 200)
    private String storedFileUrl; // 저장 파일명

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id")
    private Problem problem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder.Default
    @OneToMany(mappedBy = "problemRequest", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProblemSubmit> problemSubmits = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "problemRequest", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProblemParticipant> problemParticipants = new ArrayList<>();

    public void setProblem(Problem problem) {
        this.problem = problem;
        problem.getProblemRequests().add(this);  // 양방향 연관관계 설정
    }

    public void setFile(String originalFilename, String filePath) {
        this.originalFileName = originalFilename;
        this.storedFileUrl = filePath;
    }

    public void updateRequestProblem(String title, String contents) {
        this.title = title;
        this.contents = contents;
    }

    public void setProblemRequestStatus(ProblemRequestStatus problemRequestStatus) {
        this.problemRequestStatus = problemRequestStatus;
    }
}
