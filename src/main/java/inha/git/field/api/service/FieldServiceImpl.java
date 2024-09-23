package inha.git.field.api.service;

import inha.git.college.domain.College;
import inha.git.college.domain.repository.CollegeJpaRepository;
import inha.git.common.exceptions.BaseException;
import inha.git.department.domain.Department;
import inha.git.department.domain.repository.DepartmentJpaRepository;
import inha.git.field.api.controller.dto.request.CreateFieldRequest;
import inha.git.field.api.controller.dto.request.UpdateFieldRequest;
import inha.git.field.api.controller.dto.response.SearchFieldResponse;
import inha.git.field.api.mapper.FieldMapper;
import inha.git.field.domain.Field;
import inha.git.field.domain.repository.FieldJpaRepository;
import inha.git.semester.domain.Semester;
import inha.git.semester.domain.repository.SemesterJpaRepository;
import inha.git.statistics.domain.CollegeStatistics;
import inha.git.statistics.domain.DepartmentStatistics;
import inha.git.statistics.domain.UserCountStatistics;
import inha.git.statistics.domain.UserStatistics;
import inha.git.statistics.domain.repository.CollegeStatisticsJpaRepository;
import inha.git.statistics.domain.repository.DepartmentStatisticsJpaRepository;
import inha.git.statistics.domain.repository.UserCountStatisticsJpaRepository;
import inha.git.statistics.domain.repository.UserStatisticsJpaRepository;
import inha.git.user.domain.User;
import inha.git.user.domain.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static inha.git.common.BaseEntity.State.ACTIVE;
import static inha.git.common.BaseEntity.State.INACTIVE;
import static inha.git.common.code.status.ErrorStatus.FIELD_NOT_FOUND;

/**
 * FieldServiceImpl는 FieldService 인터페이스를 구현하는 클래스.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class FieldServiceImpl implements FieldService {

    private final FieldJpaRepository fieldJpaRepository;
    private final CollegeJpaRepository collegeJpaRepository;
    private final CollegeStatisticsJpaRepository collegeStatisticsJpaRepository;
    private final SemesterJpaRepository semesterJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final DepartmentJpaRepository departmentJpaRepository;
    private final DepartmentStatisticsJpaRepository departmentStatisticsJpaRepository;
    private final UserStatisticsJpaRepository userStatisticsJpaRepository;
    private final UserCountStatisticsJpaRepository userCountStatisticsJpaRepository;
    private final FieldMapper fieldMapper;

    /**
     * 분야 전체 조회
     *
     * @return 분야 전체 조회 결과
     */
    @Override
    public List<SearchFieldResponse> getFields() {
        return fieldMapper.fieldsToSearchFieldResponses(fieldJpaRepository.findAllByState(ACTIVE));
    }

    /**
     * 분야 생성
     *
     * @param createFieldRequest 분야 생성 요청
     * @return 생성된 분야 이름
     */
    @Override
    @Transactional
    public String createField(User admin, CreateFieldRequest createFieldRequest) {
        Field field = fieldMapper.createFieldRequestToField(createFieldRequest);
        Field savedField = fieldJpaRepository.save(field);
        List<College> colleges = collegeJpaRepository.findAllByState(ACTIVE);
        List<Department> departments = departmentJpaRepository.findAllByState(ACTIVE);
        List<User> users = userJpaRepository.findAllByState(ACTIVE);
        List<Semester> semesters = semesterJpaRepository.findAllByState(ACTIVE);

        List<CollegeStatistics> statisticsList = fieldMapper.createCollegeStatistics(savedField, colleges, semesters);
        List<DepartmentStatistics> departmentStatistics = fieldMapper.createDepartmentStatistics(savedField, departments, semesters);
        List<UserStatistics> userStatistics = fieldMapper.createUserStatistics(savedField, users, semesters);
        List<UserCountStatistics> userCountStatistics = fieldMapper.createUserCountStatistics(savedField, semesters);
        collegeStatisticsJpaRepository.saveAll(statisticsList);
        departmentStatisticsJpaRepository.saveAll(departmentStatistics);
        userStatisticsJpaRepository.saveAll(userStatistics);
        userCountStatisticsJpaRepository.saveAll(userCountStatistics);

        log.info("분야 생성 성공 - 관리자: {} 분야명: {}", admin.getName(), field.getName());
        return savedField.getName() + " 분야가 생성되었습니다.";
    }

    /**
     * 분야 이름 변경
     *
     * @param fieldIdx 분야 인덱스
     * @param updateFieldRequest 분야 이름 변경 요청
     * @return 변경된 분야 이름
     */
    @Override
    public String updateField(User admin, Integer fieldIdx, UpdateFieldRequest updateFieldRequest) {
        Field field = fieldJpaRepository.findByIdAndState(fieldIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(FIELD_NOT_FOUND));
        field.setName(updateFieldRequest.name());
        log.info("분야 수정 성공 - 관리자: {} 분야명: {}", admin.getName(), field.getName());
        return field.getName() + " 분야가 수정되었습니다.";
    }

    /**
     * 분야 삭제
     *
     * @param fieldIdx 분야 인덱스
     * @return 삭제된 분야 이름
     */
    @Override
    @Transactional
    public String deleteField(User admin, Integer fieldIdx) {
        Field field = fieldJpaRepository.findByIdAndState(fieldIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(FIELD_NOT_FOUND));
        field.setState(INACTIVE);
        field.setDeletedAt();
        log.info("분야 삭제 성공 - 관리자: {} 분야명: {}", admin.getName(), field.getName());
        return field.getName() + " 분야가 삭제되었습니다.";
    }
}
