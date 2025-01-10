package inha.git.user.api.mapper;


import inha.git.admin.api.controller.dto.response.SearchDepartmentResponse;
import inha.git.category.domain.Category;
import inha.git.common.exceptions.BaseException;
import inha.git.department.domain.Department;
import inha.git.department.domain.repository.DepartmentJpaRepository;
import inha.git.field.domain.Field;
import inha.git.semester.domain.Semester;
import inha.git.user.api.controller.dto.request.CompanySignupRequest;
import inha.git.user.api.controller.dto.request.ProfessorSignupRequest;
import inha.git.user.api.controller.dto.request.StudentSignupRequest;
import inha.git.user.api.controller.dto.response.*;
import inha.git.user.domain.Company;
import inha.git.user.domain.Professor;
import inha.git.user.domain.User;
import inha.git.utils.EmailMapperUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

import static inha.git.common.code.status.ErrorStatus.DEPARTMENT_NOT_FOUND;

/**
 * UserMapper는 User 엔티티와 관련된 데이터 변환 기능을 제공.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, imports = { EmailMapperUtil.class })
public interface UserMapper {

    /**
     * StudentSignupRequest를 User로 변환하는 메서드 정의
     *
     * @param studentSignupRequest StudentSignupRequest
     * @return User
     */
    @Mapping(target = "role", constant = "USER")
    @Mapping(target = "reportCount", constant = "0")
    User studentSignupRequestToUser(StudentSignupRequest studentSignupRequest);

    /**
     * ProfessorSignupRequest를 User로 변환하는 메서드 정의
     *
     * @param professorSignupRequest ProfessorSignupRequest
     * @return User
     */
    @Mapping(target = "role", constant = "PROFESSOR")
    @Mapping(target = "reportCount", constant = "0")
    User professorSignupRequestToUser(ProfessorSignupRequest professorSignupRequest);

    /**
     * CompanySignupRequest를 User로 변환하는 메서드 정의
     *
     * @param companySignupRequest CompanySignupRequest
     * @return User
     */
    @Mapping(target = "role", constant = "COMPANY")
    @Mapping(target = "reportCount", constant = "0")
    User companySignupRequestToUser(CompanySignupRequest companySignupRequest);

    /**
     * User 엔티티에 Department를 매핑하는 메서드 정의
     *
     * @param user User 엔티티
     * @param departmentIdList Department ID 목록
     * @param departmentRepository DepartmentJpaRepository
     */
    default void mapDepartmentsToUser(User user, List<Integer> departmentIdList, DepartmentJpaRepository departmentRepository) {
        for (Integer departmentId : departmentIdList) {
            Department department = departmentRepository.findById(departmentId)
                    .orElseThrow(() -> new BaseException(DEPARTMENT_NOT_FOUND));
            user.addDepartment(department);
        }
    }

    /**
     * CompanySignupRequest를 Company로 변환하는 메서드 정의
     *
     * @param companySignupRequest CompanySignupRequest
     * @return Company
     */
    @Mapping(target = "acceptedAt", ignore = true)
    Company companySignupRequestToCompany(CompanySignupRequest companySignupRequest, String evidenceFilePath);


    /**
     * ProfessorSignupRequest를 Professor로 변환하는 메서드 정의
     *
     * @param professorSignupRequest ProfessorSignupRequest
     * @return Professor
     */
    @Mapping(target = "acceptedAt", ignore = true)
    Professor professorSignupRequestToProfessor(ProfessorSignupRequest professorSignupRequest);

    /**
     * User 엔티티를 StudentSignupResponse로 변환하는 메서드 정의
     *
     * @param user User 엔티티
     * @return StudentSignupResponse
     */
    @Mapping(source = "user.id", target = "idx")
    StudentSignupResponse userToStudentSignupResponse(User user);

    /**
     * User 엔티티를 ProfessorSignupResponse로 변환하는 메서드 정의
     *
     * @param user User 엔티티
     * @return ProfessorSignupResponse
     */
    @Mapping(source = "user.id", target = "idx")
    ProfessorSignupResponse userToProfessorSignupResponse(User user);

    /**
     * User 엔티티를 CompanySignupResponse로 변환하는 메서드 정의
     *
     * @param user User 엔티티
     * @return CompanySignupResponse
     */
    @Mapping(source = "user.id", target = "idx")
    CompanySignupResponse userToCompanySignupResponse(User user);



    /**
     * Department 엔티티를 SearchDepartmentResponse로 변환하는 메서드 정의
     *
     * @param department Department 엔티티
     * @return SearchDepartmentResponse
     */
    @Mapping(source = "department.id", target = "idx")
    SearchDepartmentResponse departmentToSearchDepartmentResponse(Department department);

    /**
     * Department 엔티티 목록을 SearchDepartmentResponse 목록으로 변환하는 메서드 정의
     *
     * @param departmentList Department 엔티티 목록
     * @return SearchDepartmentResponse 목록
     */
    List<SearchDepartmentResponse> departmentsToSearchDepartmentResponses(List<Department> departmentList);


    /**
     * User 엔티티를 SearchNonCompanyUserResponse로 변환하는 메서드 정의
     *
     * @param user User 엔티티
     * @param totalProjectCount 사용자의 총 프로젝트 수
     * @param totalQuestionCount 사용자의 총 질문 수
     * @param totalTeamCount 사용자의 총 팀 수
     * @param departmentList 사용자의 소속 부서 목록
     * @param position 사용자의 직책
     * @param githubTokenState 사용자의 깃허브 토큰 상태
     * @return SearchNonCompanyUserResponse
     */
    @Mapping(source = "user.id", target = "idx")
    @Mapping(source = "totalProjectCount", target = "projectNumber")
    @Mapping(source = "totalQuestionCount", target = "questionCommentNumber")
    @Mapping(source = "totalTeamCount", target = "belongTeamNumber")
    @Mapping(expression = "java(EmailMapperUtil.maskEmail(user.getEmail()))", target = "email")
    SearchNonCompanyUserResponse toSearchNonCompanyUserResponse(User user, Integer totalProjectCount, Integer totalQuestionCount, Integer totalTeamCount, List<SearchDepartmentResponse> departmentList, Integer position, Boolean githubTokenState);


    /**
     * User 엔티티를 SearchCompanyUserResponse로 변환하는 메서드 정의
     *
     * @param user User 엔티티
     * @param position User의 직책
     * @param company Company 엔티티
     * @return SearchCompanyUserResponse
     */
    @Mapping(source = "user.id", target = "idx")
    @Mapping(expression = "java(EmailMapperUtil.maskEmail(user.getEmail()))", target = "email")
    SearchCompanyUserResponse toSearchCompanyUserResponse(User user, Integer position, Company company);

    /**
     * User 엔티티를 UserResponse로 변환하는 메서드 정의
     *
     * @param user User 엔티티
     * @return UserResponse
     */
    @Mapping(source = "user.id", target = "idx")
    UserResponse toUserResponse(User user);
}
