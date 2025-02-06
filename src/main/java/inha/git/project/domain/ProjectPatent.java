package inha.git.project.domain;

import inha.git.common.BaseEntity;
import inha.git.project.domain.enums.PatentType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ProjectPatent 엔티티는 애플리케이션의 프로젝트 특허 정보를 나타냄.
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
@Builder
@Entity
@Table(name = "project_patent_tb")
public class ProjectPatent extends BaseEntity {

    @Id
    @Column(name = "project_patent_id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "application_number", nullable = false, length = 100)
    private String applicationNumber; // 출원번호

    @Column(name = "application_date", nullable = false)
    private String applicationDate; // 출원일자

    @Column(name = "registration_date")
    private String inventionTitle; // 특허 한글명

    @Column(name = "invention_title_english")
    private String inventionTitleEnglish; // 특허 영문명

    @Column(name = "applicant_name")
    private String applicantName; // 출원인 이름

    @Column(name = "applicant_english_name")
    private String applicantEnglishName; // 출원인 영문 이름

    @Column(name = "evidence")
    private String evidence; // 증빙 파일

    @Column(name = "patent_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private PatentType patentType; // 특허 유형

    @Column(name = "accept_at", nullable = true)
    private LocalDateTime acceptAt; // 승인일

    @OneToMany(mappedBy = "projectPatent", fetch = FetchType.LAZY)
    private List<ProjectPatentInventor> projectPatentInventors = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    public void setProject(Project project) {
        this.project = project;
        project.setProjectPatent(this); // 양방향 연관관계 설정
    }

    public void setEvidence(String storedFileUrl) {
        this.evidence = storedFileUrl;
    }

    public void updatePatent(String applicationNumber, PatentType patentType, String applicationDate, String inventionTitle, String inventionTitleEnglish, String applicantName, String applicantEnglishName) {
        this.applicationNumber = applicationNumber;
        this.patentType = patentType;
        this.applicationDate = applicationDate;
        this.inventionTitle = inventionTitle;
        this.inventionTitleEnglish = inventionTitleEnglish;
        this.applicantName = applicantName;
        this.applicantEnglishName = applicantEnglishName;
    }
}
