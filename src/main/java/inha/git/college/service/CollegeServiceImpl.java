package inha.git.college.service;

import inha.git.category.domain.Category;
import inha.git.category.domain.repository.CategoryJpaRepository;
import inha.git.college.controller.dto.request.CreateCollegeRequest;
import inha.git.college.controller.dto.request.UpdateCollegeRequest;
import inha.git.college.controller.dto.response.SearchCollegeResponse;
import inha.git.college.domain.College;
import inha.git.college.domain.repository.CollegeJpaRepository;
import inha.git.college.mapper.CollegeMapper;
import inha.git.common.exceptions.BaseException;
import inha.git.department.domain.repository.DepartmentJpaRepository;
import inha.git.field.domain.Field;
import inha.git.field.domain.repository.FieldJpaRepository;
import inha.git.semester.domain.Semester;
import inha.git.semester.domain.repository.SemesterJpaRepository;
import inha.git.statistics.domain.CollegeStatistics;
import inha.git.statistics.domain.repository.CollegeStatisticsJpaRepository;
import inha.git.statistics.domain.repository.TotalCollegeStatisticsJpaRepository;
import inha.git.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static inha.git.common.BaseEntity.State.ACTIVE;
import static inha.git.common.BaseEntity.State.INACTIVE;
import static inha.git.common.code.status.ErrorStatus.COLLEGE_NOT_FOUND;
import static inha.git.common.code.status.ErrorStatus.DEPARTMENT_NOT_FOUND;

/**
 * CollegeServiceImpl는 CollegeService 인터페이스를 구현하는 클래스.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CollegeServiceImpl implements CollegeService {

    private final CollegeJpaRepository collegeJpaRepository;
    private final CollegeStatisticsJpaRepository collegeStatisticsJpaRepository;
    private final SemesterJpaRepository semesterJpaRepository;
    private final FieldJpaRepository fieldJpaRepository;
    private final CategoryJpaRepository categoryJpaRepository;
    private final TotalCollegeStatisticsJpaRepository totalCollegeStatisticsJpaRepository;
    private final DepartmentJpaRepository departmentJpaRepository;
    private final CollegeMapper collegeMapper;

    /**
     * 단과대 전체 조회
     *
     * @return 단과대 전체 조회 결과
     */
    @Override
    public List<SearchCollegeResponse> getColleges() {
        return collegeMapper.collegesToSearchCollegeResponses(collegeJpaRepository.findAllByState(ACTIVE));
    }


    /**
     * 단과대 조회
     *
     * @param departmentIdx 단과대 인덱스
     * @return 단과대 조회 결과
     */
    @Override
    public SearchCollegeResponse getCollege(Integer departmentIdx) {
        departmentJpaRepository.findByIdAndState(departmentIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(DEPARTMENT_NOT_FOUND));
        College college = collegeJpaRepository.findByDepartments_IdAndState(departmentIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(COLLEGE_NOT_FOUND));
        return collegeMapper.collegeToSearchCollegeResponse(college);
    }

    /**
     * 단과대 생성
     *
     * @param createDepartmentRequest 단과대 생성 요청
     * @return 생성된 단과대 이름
     */
    @Override
    @Transactional
    public String createCollege(User admin, CreateCollegeRequest createDepartmentRequest) {
        College college = collegeJpaRepository.save
                (collegeMapper.createCollegeRequestToCollege(createDepartmentRequest));
        List<Semester> semesters = semesterJpaRepository.findAllByState(ACTIVE);
        List<Field> fields = fieldJpaRepository.findAllByState(ACTIVE);
        List<Category> categories = categoryJpaRepository.findAllByState(ACTIVE);
        for (Semester semester : semesters) {
            for (Field field : fields) {
                for (Category category : categories) {
                    CollegeStatistics collegeStatistics = collegeMapper.createCollegeStatistics(college, semester, field, category);
                    collegeStatisticsJpaRepository.save(collegeStatistics);
                }
            }
        }
        totalCollegeStatisticsJpaRepository.save(collegeMapper.createTotalCollegeStatistics(college));
        log.info("단과대 생성 성공 - 관리자: {} 단과대 이름: {}", admin.getName(), college.getName());
        return college.getName() + " 단과대가 생성되었습니다.";
    }


    /**
     * 단과대 이름 수정
     *
     * @param collegeIdx 단과대 인덱스
     * @param updateCollegeRequest 단과대 수정 요청
     * @return 수정된 단과대 이름
     */
    @Override
    @Transactional
    public String updateCollegeName(User admin, Integer collegeIdx ,UpdateCollegeRequest updateCollegeRequest) {
        College college = collegeJpaRepository.findByIdAndState(collegeIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(COLLEGE_NOT_FOUND));
        college.setName(updateCollegeRequest.name());
        log.info("단과대 이름 수정 성공 - 관리자: {} 단과대 이름: {}", admin.getName(), college.getName());
        return college.getName() + " 단과대 이름이 변경되었습니다.";
    }

    /**
     * 단과대 삭제
     *
     * @param collegeIdx 단과대 인덱스
     * @return 삭제된 단과대 이름
     */
    @Override
    @Transactional
    public String deleteCollege(User admin, Integer collegeIdx) {
        College college = collegeJpaRepository.findByIdAndState(collegeIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(COLLEGE_NOT_FOUND));
        college.setState(INACTIVE);
        college.setDeletedAt();
        log.info("단과대 삭제 성공 - 관리자: {} 단과대 이름: {}", admin.getName(), college.getName());
        return college.getName() + " 단과대가 삭제되었습니다.";
    }
}
