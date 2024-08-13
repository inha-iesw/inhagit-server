package inha.git.user.api.mapper;


import inha.git.common.exceptions.BaseException;
import inha.git.department.domain.Department;
import inha.git.department.domain.repository.DepartmentJpaRepository;
import inha.git.user.api.controller.dto.request.StudentSignupRequest;
import inha.git.user.api.controller.dto.response.StudentSignupResponse;
import inha.git.user.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

import static inha.git.common.code.status.ErrorStatus.DEPARTMENT_NOT_FOUND;

/**
 * UserMapper는 User 엔티티와 관련된 데이터 변환 기능을 제공.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    @Mapping(target = "role", constant = "USER")
    User studentSignupRequestToUser(StudentSignupRequest studentSignupRequest);

    // Department 리스트를 기반으로 UserDepartment 설정하는 메서드 정의
    default void mapDepartmentsToUser(User user, List<Integer> departmentIdList, DepartmentJpaRepository departmentRepository) {
        for (Integer departmentId : departmentIdList) {
            Department department = departmentRepository.findById(departmentId)
                    .orElseThrow(() -> new BaseException(DEPARTMENT_NOT_FOUND));
            user.addDepartment(department);
        }
    }

    @Mapping(source = "user.id", target = "userId")
    StudentSignupResponse userToStudentSignupResponse(User user);
}
