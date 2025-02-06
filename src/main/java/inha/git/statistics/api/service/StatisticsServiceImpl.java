package inha.git.statistics.api.service;

import inha.git.category.domain.Category;
import inha.git.category.domain.repository.CategoryJpaRepository;
import inha.git.college.domain.repository.CollegeJpaRepository;
import inha.git.common.exceptions.BaseException;
import inha.git.department.domain.Department;
import inha.git.department.domain.repository.DepartmentJpaRepository;
import inha.git.field.domain.Field;
import inha.git.field.domain.repository.FieldJpaRepository;
import inha.git.mapping.domain.UserDepartment;
import inha.git.mapping.domain.repository.UserDepartmentJpaRepository;
import inha.git.project.domain.repository.ProjectJpaRepository;
import inha.git.project.domain.repository.ProjectPatentJpaRepository;
import inha.git.question.domain.repository.QuestionJpaRepository;
import inha.git.semester.domain.Semester;
import inha.git.semester.domain.repository.SemesterJpaRepository;
import inha.git.statistics.api.controller.dto.request.SearchCond;
import inha.git.statistics.api.controller.dto.response.BatchCollegeStatisticsResponse;
import inha.git.statistics.api.controller.dto.response.ProjectStatisticsResponse;
import inha.git.statistics.api.controller.dto.response.QuestionStatisticsResponse;
import inha.git.statistics.domain.Statistics;
import inha.git.statistics.domain.enums.StatisticsType;
import inha.git.statistics.domain.repository.*;
import inha.git.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static inha.git.common.BaseEntity.State.ACTIVE;
import static inha.git.common.code.status.ErrorStatus.*;

/**
 * StatisticsServiceImpl은 통계 관련 비즈니스 로직을 처리한다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StatisticsServiceImpl implements StatisticsService {

    private final UserDepartmentJpaRepository userDepartmentJpaRepository;
    private final DepartmentJpaRepository departmentJpaRepository;
    private final StatisticsJpaRepository statisticsJpaRepository;
    private final CollegeJpaRepository collegeJpaRepository;
    private final FieldJpaRepository fieldJpaRepository;
    private final SemesterJpaRepository semesterJpaRepository;
    private final CategoryJpaRepository categoryJpaRepository;
    private final ProjectJpaRepository projectJpaRepository;
    private final QuestionJpaRepository questionJpaRepository;
    private final ProjectPatentJpaRepository projectPatentJpaRepository;
    private final ProjectStatisticsQueryRepository projectStatisticsQueryRepository;
    private final QuestionStatisticsQueryRepository questionStatisticsQueryRepository;
    private final BatchStatisticsQueryRepository batchStatisticsQueryRepository;

    /**
     * 사용자 통계 정보를 증가시킨다.
     *
     * @param user User
     * @param type Integer
     */
    @Transactional
    public void adjustCount(User user, List<Field> fields, Semester semester, Category category, Integer type, boolean isIncrease) {
        // 전체 통계 업데이트
        updateStatistics(user, StatisticsType.TOTAL, null, semester, fields, category, type, isIncrease);
        // 유저 통계 업데이트
        updateStatistics(user, StatisticsType.USER, user.getId(), semester, fields, category, type, isIncrease);
        // 학과 및 단과대 통계 업데이트
        List<UserDepartment> userDepartments = userDepartmentJpaRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BaseException(USER_DEPARTMENT_NOT_FOUND));

        for (UserDepartment userDepartment : userDepartments) {
            updateStatistics(user, StatisticsType.DEPARTMENT, userDepartment.getDepartment().getId(), semester, fields, category, type, isIncrease);
            updateStatistics(user, StatisticsType.COLLEGE, userDepartment.getDepartment().getCollege().getId(), semester, fields, category, type, isIncrease);
        }
    }

    /**
     * 프로젝트 통계 정보를 조회한다.
     *
     * @param searchCond ProjectSearchCond
     * @return ProjectStatisticsResponse
     */
    @Override
    @Transactional(readOnly = true)
    public ProjectStatisticsResponse getProjectStatistics(SearchCond searchCond) {
        validateSearchCond(searchCond);
        return projectStatisticsQueryRepository.getProjectStatistics(searchCond);
    }

    /**
     * 질문 통계 정보를 조회한다.
     *
     * @param searchCond SearchCond
     * @return QuestionStatisticsResponse
     */
    @Override
    public QuestionStatisticsResponse getQuestionStatistics(SearchCond searchCond) {
        validateSearchCond(searchCond);
        return questionStatisticsQueryRepository.getQuestionStatistics(searchCond);
    }

    /**
     * 단과대별 학기별 통계를 조회한다.
     *
     * @return List<BatchCollegeStatisticsResponse>
     */
    @Override
    @Transactional(readOnly = true)
    public List<BatchCollegeStatisticsResponse> getBatchStatistics() {
        return batchStatisticsQueryRepository.getBatchStatistics();
    }

    private void validateSearchCond(SearchCond searchCond) {
        if(searchCond.collegeIdx() != null) {
            collegeJpaRepository.findByIdAndState(searchCond.collegeIdx(), ACTIVE)
                    .orElseThrow(() -> new BaseException(COLLEGE_NOT_FOUND));
        }
        if(searchCond.departmentIdx() != null) {
            Department department = departmentJpaRepository.findByIdAndState(searchCond.departmentIdx(), ACTIVE)
                    .orElseThrow(() -> new BaseException(DEPARTMENT_NOT_FOUND));
            if(searchCond.collegeIdx() != null && !department.getCollege().getId().equals(searchCond.collegeIdx())) {
                throw new BaseException(DEPARTMENT_NOT_BELONG_TO_COLLEGE);
            }
        }
        if(searchCond.fieldIdx() != null) {
            fieldJpaRepository.findByIdAndState(searchCond.fieldIdx(), ACTIVE)
                    .orElseThrow(() -> new BaseException(FIELD_NOT_FOUND));
        }
        if(searchCond.semesterIdx() != null) {
            semesterJpaRepository.findByIdAndState(searchCond.semesterIdx(), ACTIVE)
                    .orElseThrow(() -> new BaseException(SEMESTER_NOT_FOUND));
        }
        if(searchCond.categoryIdx() != null) {
            categoryJpaRepository.findByIdAndState(searchCond.categoryIdx(), ACTIVE)
                    .orElseThrow(() -> new BaseException(CATEGORY_NOT_FOUND));
        }
    }

    /**
     * 통계를 업데이트(증가/감소)하는 공통 메서드
     */
    private void updateStatistics(User user, StatisticsType type, Integer targetId, Semester semester, List<Field> fields, Category category, Integer actionType, boolean isIncrease) {
        for (Field field : fields) {
            Long safeTargetId = (targetId != null) ? targetId.longValue() : null;
            Statistics statistics;
            try {
                // 락을 걸고 조회
                statistics = statisticsJpaRepository.findByStatisticsTypeAndTargetIdAndSemesterIdAndFieldIdAndCategoryIdWithPessimisticLock(
                                type, safeTargetId, semester.getId().longValue(), field.getId().longValue(), category.getId().longValue())
                        .orElse(null);

                // 통계가 없으면 새로 생성
                if (statistics == null) {
                    statistics = Statistics.builder()
                            .statisticsType(type)
                            .targetId(targetId)
                            .semesterId(semester.getId())
                            .fieldId(field.getId())
                            .categoryId(category.getId())
                            .localProjectCount(0)
                            .githubProjectCount(0)
                            .questionCount(0)
                            .projectParticipationCount(0)
                            .questionParticipationCount(0)
                            .patentCount(0)
                            .patentParticipationCount(0)
                            .build();
                    statisticsJpaRepository.save(statistics);
                }
            } catch (PessimisticLockingFailureException e) {
                log.error("통계 관련 락 획득 실패 - 사용자 ID: {}, 학기 ID: {}, 분야 ID: {}, 카테고리 ID: {}", user.getId(), semester.getId(), field.getId(), category.getId());
                throw new BaseException(TEMPORARY_UNAVAILABLE);
            }

            // 통계 업데이트
            switch (actionType) {
                case 1 -> {  // 로컬 프로젝트
                    if (isIncrease) {
                        if (isFirstProjectUpload(user, semester, field)) {
                            statistics.incrementProjectParticipation();
                        }
                        statistics.incrementLocalProjectCount();
                    } else {
                        statistics.decrementLocalProjectCount();
                        if (isLastProject(user, semester, field)) {
                            statistics.decrementProjectParticipation();
                        }
                    }
                }
                case 2 -> {  // 깃허브 프로젝트
                    if (isIncrease) {
                        if (isFirstProjectUpload(user, semester, field)) {
                            statistics.incrementProjectParticipation();
                        }
                        statistics.incrementGithubProjectCount();
                    } else {
                        statistics.decrementGithubProjectCount();
                        if (isLastProject(user, semester, field)) {
                            statistics.decrementProjectParticipation();
                        }
                    }
                }
                case 3 -> {  // 질문
                    if (isIncrease) {
                        if (isFirstQuestionUpload(user, semester, field)) {
                            statistics.incrementQuestionParticipation();
                        }
                        statistics.incrementQuestionCount();
                    } else {
                        statistics.decrementQuestionCount();
                        if (isLastQuestion(user, semester, field)) {
                            statistics.decrementQuestionParticipation();
                        }
                    }
                }
                case 4 -> {  // 특허
                    if (isIncrease) {
                        if (isFirstPatent(user, semester, field)) {
                            statistics.incrementPatentParticipation();
                        }
                        statistics.incrementPatentCount();
                    } else {
                        statistics.decrementPatentCount();
                        if (isLastPatent(user, semester, field)) {
                            statistics.decrementPatentParticipation();
                        }
                    }
                }
                default -> throw new BaseException(INVALID_ACTION_TYPE);
            }

            // 변경된 통계 저장
            statisticsJpaRepository.save(statistics);
        }
    }

    private boolean isFirstProjectUpload(User user, Semester semester, Field field) {
        return projectJpaRepository.countByUserAndSemesterAndProjectFields_FieldAndState(user, semester, field, ACTIVE) == 1;
    }

    private boolean isLastProject(User user, Semester semester, Field field) {
        return projectJpaRepository.countByUserAndSemesterAndProjectFields_FieldAndState(user, semester, field, ACTIVE) == 0;
    }

    private boolean isFirstQuestionUpload(User user, Semester semester, Field field) {
        return questionJpaRepository.countByUserAndSemesterAndQuestionFields_FieldAndState(user, semester, field, ACTIVE) == 1;
    }

    private boolean isLastQuestion(User user, Semester semester, Field field) {
        return questionJpaRepository.countByUserAndSemesterAndQuestionFields_FieldAndState(user, semester, field, ACTIVE) == 0;
    }

    private boolean isFirstPatent(User user, Semester semester, Field field) {
        return projectPatentJpaRepository.countByProject_UserAndProject_SemesterAndProject_ProjectFields_FieldAndProject_State(
                user, semester, field, ACTIVE) == 1;
    }

    private boolean isLastPatent(User user, Semester semester, Field field) {
        return projectPatentJpaRepository.countByProject_UserAndProject_SemesterAndProject_ProjectFields_FieldAndProject_State(
                user, semester, field, ACTIVE) == 0;
    }
}
