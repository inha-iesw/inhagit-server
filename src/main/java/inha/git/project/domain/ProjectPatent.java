package inha.git.project.domain;

import inha.git.common.BaseEntity;
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

    @OneToMany(mappedBy = "projectPatent", fetch = FetchType.LAZY)
    private List<ProjectPatentInventor> projectPatentInventors = new ArrayList<>();

}
