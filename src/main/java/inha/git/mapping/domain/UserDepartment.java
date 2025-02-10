package inha.git.mapping.domain;

import inha.git.department.domain.Department;
import inha.git.mapping.domain.id.UserDepartmentId;
import inha.git.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

/**
 * UserDepartment 엔티티는 애플리케이션의 유저와 학과 매핑 정보를 나타냄.
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
@Builder
@Entity
@Table(name = "user_department_tb")
public class UserDepartment {

    @EmbeddedId
    private UserDepartmentId id;

    @MapsId("userId")
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @MapsId("departmentId")
    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    public UserDepartment(User user, Department department) {
        this.user = user;
        this.department = department;
        this.id = new UserDepartmentId(user.getId(), department.getId());
    }
}
