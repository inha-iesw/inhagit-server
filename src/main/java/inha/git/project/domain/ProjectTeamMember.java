package inha.git.project.domain;

import jakarta.persistence.*;
import lombok.*;

/**
 * ProjectTeamMember 엔티티는 애플리케이션의 프로젝트 특허 팀원 정보를 나타냄.
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
@Builder
@Entity
@Table(name = "project_teammember_tb")
public class ProjectTeamMember {
    @Id
    @Column(name = "project_member_id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "user_number")
    private String userNumber;

    @Column(name = "department_id")
    private Integer departmentIdx;

    @Column(name = "college_id")
    private Integer collegeIdx;

    @Column(name = "department_name")
    private String departmentName;

    @Column(name = "college_name")
    private String collegeName;
}
