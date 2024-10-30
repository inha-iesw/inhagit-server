package inha.git.category.service;

import inha.git.category.controller.dto.request.CreateCategoryRequest;
import inha.git.category.controller.dto.request.UpdateCategoryRequest;
import inha.git.category.controller.dto.response.SearchCategoryResponse;
import inha.git.category.domain.Category;
import inha.git.category.domain.repository.CategoryJpaRepository;
import inha.git.category.mapper.CategoryMapper;
import inha.git.college.domain.College;
import inha.git.college.domain.repository.CollegeJpaRepository;
import inha.git.common.BaseEntity;
import inha.git.common.exceptions.BaseException;
import inha.git.department.domain.Department;
import inha.git.department.domain.repository.DepartmentJpaRepository;
import inha.git.field.domain.Field;
import inha.git.field.domain.repository.FieldJpaRepository;
import inha.git.semester.domain.Semester;
import inha.git.semester.domain.repository.SemesterJpaRepository;
import inha.git.semester.mapper.SemesterMapper;
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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static inha.git.common.BaseEntity.State.ACTIVE;
import static inha.git.common.BaseEntity.State.INACTIVE;
import static inha.git.common.code.status.ErrorStatus.CATEGORY_NOT_FOUND;

/**
 * SemesterServiceImpl는 SemesterService 인터페이스를 구현하는 클래스.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryJpaRepository categoryJpaRepository;
    private final CategoryMapper categoryMapper;
    private final SemesterJpaRepository semesterJpaRepository;
    private final CollegeStatisticsJpaRepository collegeStatisticsJpaRepository;
    private final CollegeJpaRepository collegeJpaRepository;
    private final FieldJpaRepository fieldJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final DepartmentJpaRepository departmentJpaRepository;
    private final DepartmentStatisticsJpaRepository departmentStatisticsJpaRepository;
    private final UserStatisticsJpaRepository userStatisticsJpaRepository;
    private final UserCountStatisticsJpaRepository userCountStatisticsJpaRepository;



    /**
     * 카테고리 전체 조회
     *
     * @return 카테고리 전체 조회 결과
     */
    @Override
    public List<SearchCategoryResponse> getCategories() {
        return categoryMapper.categoriesToSearchCategoryResponses
                (categoryJpaRepository.findAllByState(BaseEntity.State.ACTIVE, Sort.by(Sort.Direction.ASC, "name")));
    }

    /**
     * 카테고리 생성
     *
     * @param createCategoryRequest 카테고리 생성 요청
     * @return 생성된 카테고리 이름
     */
    @Override
    @Transactional
    public String createCategory(User admin, CreateCategoryRequest createCategoryRequest) {
        Category category = categoryJpaRepository.save(categoryMapper.createCategoryRequestToSemester(createCategoryRequest));

        List<College> colleges = collegeJpaRepository.findAllByState(ACTIVE);
        List<Department> departments = departmentJpaRepository.findAllByState(ACTIVE);
        List<User> users = userJpaRepository.findAllByState(ACTIVE);
        List<Field> fields = fieldJpaRepository.findAllByState(ACTIVE);
        List<Semester> semesters = semesterJpaRepository.findAllByState(ACTIVE);

        List<CollegeStatistics> statisticsList = categoryMapper.createCollegeStatistics(category, colleges, fields, semesters);
        List<DepartmentStatistics> departmentStatistics = categoryMapper.createDepartmentStatistics(category, departments, fields, semesters);
        List<UserStatistics> userStatistics = categoryMapper.createUserStatistics(category, users, fields, semesters);
        List<UserCountStatistics> userCountStatistics = categoryMapper.createUserCountStatistics(category, fields, semesters);

        collegeStatisticsJpaRepository.saveAll(statisticsList);
        departmentStatisticsJpaRepository.saveAll(departmentStatistics);
        userStatisticsJpaRepository.saveAll(userStatistics);
        userCountStatisticsJpaRepository.saveAll(userCountStatistics);

        log.info("카테고리 생성 성공 - 관리자: {} 학기명: {}", admin.getName(), createCategoryRequest.name());
        return category.getName() + " 카테고리가 생성되었습니다.";
    }

    /**
     * 카테고리 이름 수정
     *
     * @param categoryIdx 카테고리 인덱스
     * @param updateCategoryRequest 카테고리 수정 요청
     * @return 수정된 카테고리 이름
     */
    @Override
    @Transactional
    public String updateCategoryName(User admin, Integer categoryIdx, UpdateCategoryRequest updateCategoryRequest) {
        log.info("categoryIdx {}", categoryIdx);
        Category category = categoryJpaRepository.findByIdAndState(categoryIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(CATEGORY_NOT_FOUND));
        category.setName(updateCategoryRequest.name());
        log.info("카테고리 이름 수정 성공 - 관리자: {} 카테고리명: {}", admin.getName(), updateCategoryRequest.name());
        return category.getName() + " 카테고리 이름이 수정되었습니다.";
    }

    /**
     * 카테고리 삭제
     *
     * @param categoryIdx 카테고리 인덱스
     * @return 삭제된 카테고리 이름
     */
    @Override
    @Transactional
    public String deleteCategory(User admin, Integer categoryIdx) {
        Category category = categoryJpaRepository.findByIdAndState(categoryIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(CATEGORY_NOT_FOUND));
        category.setState(INACTIVE);
        category.setDeletedAt();
        log.info("카테고리 삭제 성공 - 관리자: {} 카테고리명: {}", admin.getName(), category.getName());
        return category.getName() + " 카테고리 삭제되었습니다.";
    }
}
