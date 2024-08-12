package inha.git.banner.department.domain;

import inha.git.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


/**
 * Department 엔티티는 애플리케이션의 학과 정보를 나타냄.
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
@Builder
@Entity
@Table(name = "department_tb")
public class Department extends BaseEntity {

    @Id
    @Column(name = "department_id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 50, name = "evidence_file_path")
    private String evidenceFilePath;

}
