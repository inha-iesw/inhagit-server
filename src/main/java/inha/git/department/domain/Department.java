package inha.git.department.domain;

import inha.git.common.BaseEntity;
import inha.git.mapping.domain.UserDepartment;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


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

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserDepartment> userDepartments = new ArrayList<>();

}
