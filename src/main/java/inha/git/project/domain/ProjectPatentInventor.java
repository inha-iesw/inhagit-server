package inha.git.project.domain;

import jakarta.persistence.*;
import lombok.*;

/**
 * ProjectPatentinventor 엔티티는 애플리케이션의 프로젝트 특허 발명자 정보를 나타냄.
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
@Builder
@Entity
@Table(name = "project_patent_inventor_tb")
public class ProjectPatentInventor {

    @Id
    @Column(name = "project_patent_inventor_id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "english_name", nullable = false)
    private String englishName;

    @Column(name = "affiliation", nullable = false)
    private String affiliation;

    @Column(name = "share", nullable = false)
    private String share;

    @Column(name = "main_inventor", nullable = false)
    private Boolean mainInventor;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "user_number")
    private String userNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_patent_id")
    private ProjectPatent projectPatent;
}
