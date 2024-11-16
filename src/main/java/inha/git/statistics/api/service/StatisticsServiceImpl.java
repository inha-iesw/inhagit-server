package inha.git.statistics.api.service;

import inha.git.category.domain.Category;
import inha.git.category.domain.repository.CategoryJpaRepository;
import inha.git.college.domain.repository.CollegeJpaRepository;
import inha.git.college.mapper.CollegeMapper;
import inha.git.common.exceptions.BaseException;
import inha.git.department.api.mapper.DepartmentMapper;
import inha.git.department.domain.Department;
import inha.git.department.domain.repository.DepartmentJpaRepository;
import inha.git.field.domain.Field;
import inha.git.field.domain.repository.FieldJpaRepository;
import inha.git.mapping.domain.UserDepartment;
import inha.git.mapping.domain.repository.UserDepartmentJpaRepository;
import inha.git.project.domain.repository.ProjectJpaRepository;
import inha.git.semester.domain.Semester;
import inha.git.semester.domain.repository.SemesterJpaRepository;
import inha.git.statistics.api.controller.dto.request.SearchCond;
import inha.git.statistics.api.controller.dto.response.ProjectStatisticsResponse;
import inha.git.statistics.api.controller.dto.response.QuestionStatisticsResponse;
import inha.git.statistics.domain.*;
import inha.git.statistics.domain.id.CollegeStatisticsStatisticsId;
import inha.git.statistics.domain.id.DepartmentStatisticsId;
import inha.git.statistics.domain.id.UserCountStatisticsId;
import inha.git.statistics.domain.id.UserStatisticsId;
import inha.git.statistics.domain.repository.*;
import inha.git.user.api.mapper.UserMapper;
import inha.git.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Consumer;

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

    private final TotalUserStatisticsJpaRepository totalUserStatisticsJpaRepository;
    private final TotalDepartmentStatisticsJpaRepository totalDepartmentStatisticsJpaRepository;
    private final TotalCollegeStatisticsJpaRepository totalCollegeStatisticsJpaRepository;
    private final UserStatisticsJpaRepository userStatisticsJpaRepository;
    private final DepartmentStatisticsJpaRepository departmentStatisticsJpaRepository;
    private final UserDepartmentJpaRepository userDepartmentJpaRepository;
    private final UserCountStatisticsJpaRepository userCountStatisticsJpaRepository;
    private final ProjectJpaRepository projectJpaRepository;
    private final CollegeStatisticsJpaRepository collegeStatisticsJpaRepository;
    private final ProjectStatisticsQueryRepository projectStatisticsQueryRepository;
    private final QuestionStatisticsQueryRepository questionStatisticsQueryRepository;
    private final DepartmentJpaRepository departmentJpaRepository;
    private final CollegeJpaRepository collegeJpaRepository;
    private final FieldJpaRepository fieldJpaRepository;
    private final SemesterJpaRepository semesterJpaRepository;
    private final CategoryJpaRepository categoryJpaRepository;
    private final UserMapper userMapper;
    private final DepartmentMapper departmentMapper;
    private final CollegeMapper collegeMapper;


    /**
     * 사용자 통계 정보를 증가시킨다.
     *
     * @param user User
     * @param type Integer
     */
    public void increaseCount(User user, List<Field> fields, Semester semester, Category category, Integer type) {
        TotalUserStatistics totalUserStatistics = totalUserStatisticsJpaRepository.findById(1) // 예시로 ID 1번 TotalStatistics 조회
                .orElseThrow(() -> new BaseException(TOTAL_STATISTICS_NOT_FOUND));
        if (type == 1 || type == 8) {
            if (!hasUploadedAnyProject(user)) {
                totalUserStatistics.increaseUserProjectCount(); // 사용자 프로젝트 카운트 증가
                createOrUpdateDepartmentAndCollegeStatistics(user, true,
                        TotalDepartmentStatistics::increaseUserProjectCount,
                        TotalCollegeStatistics::increaseUserProjectCount); // 학과 및 단과대 통계 업데이트
            }
            // 전체 통계 업데이트
            updateTotalStatistics(totalUserStatistics, type);
            if (type == 1) {
                // 일반 프로젝트 통계 업데이트
                createOrUpdateDepartmentAndCollegeStatistics(user, true,
                        TotalDepartmentStatistics::increaseTotalProjectCount,
                        TotalCollegeStatistics::increaseTotalProjectCount);
            } else {
                // 깃허브 프로젝트 통계 업데이트
                createOrUpdateDepartmentAndCollegeStatistics(user, true,
                        TotalDepartmentStatistics::increaseTotalGithubProjectCount,
                        TotalCollegeStatistics::increaseTotalGithubProjectCount);
            }
        }

        if (type == 2 ) {
            if (!hasUploadedAnyQuestion(user)) {
                totalUserStatistics.increaseUserQuestionCount();
                createOrUpdateDepartmentAndCollegeStatistics(user, true,
                        TotalDepartmentStatistics::increaseUserQuestionCount,
                        TotalCollegeStatistics::increaseUserQuestionCount);
            }
            updateTotalStatistics(totalUserStatistics, type);

            createOrUpdateDepartmentAndCollegeStatistics(user, true,
                    TotalDepartmentStatistics::increaseTotalQuestionCount,
                    TotalCollegeStatistics::increaseTotalQuestionCount);
        }
        for(Field field: fields) {
            UserStatistics userStatistics = userStatisticsJpaRepository
                    .findById(new UserStatisticsId(user.getId(), semester.getId(), field.getId(), category.getId()))
                    .orElseGet(() -> userStatisticsJpaRepository.save(userMapper.createUserStatistics(user, semester, field, category)));
            List<UserDepartment> userDepartments = userDepartmentJpaRepository.findByUserId(user.getId()).orElseThrow(() -> new BaseException(USER_DEPARTMENT_NOT_FOUND));
            UserCountStatistics userCountStatistics = userCountStatisticsJpaRepository
                    .findById(new UserCountStatisticsId(semester.getId(), field.getId(), category.getId()))
                    .orElseGet(() -> userCountStatisticsJpaRepository.save(
                            userMapper.createUserCountStatistics(semester, field, category)));
            if(type == 1){
                userCountStatistics.increaseTotalProjectCount();
                if (projectJpaRepository.countByUserAndSemesterAndProjectFields_FieldAndState(user, semester, field, ACTIVE) == 1) {
                    userCountStatistics.increaseUserProjectCount();
                    userDepartments.forEach(userDepartment -> {
                        getDepartmentStatistics(semester, category, field, userDepartment).increaseProjectUserCount();
                        getCollegeStatistics(semester, category, field, userDepartment).increaseProjectUserCount();
                    });
                }
                userStatistics.increaseProjectCount();
                userDepartments
                        .forEach(userDepartment -> {
                            getDepartmentStatistics(semester, category, field, userDepartment).increaseProjectCount();
                            getCollegeStatistics(semester, category, field, userDepartment).increaseProjectCount();
                        });
            }else if(type == 2) {
                userCountStatistics.increaseTotalQuestionCount();
                if (userStatistics.getQuestionCount() == 0) {
                    userCountStatistics.increaseUserQuestionCount();
                    userDepartments.forEach(userDepartment -> {
                        getDepartmentStatistics(semester, category, field, userDepartment).increaseQuestionUserCount();
                        getCollegeStatistics(semester, category, field, userDepartment).increaseQuestionUserCount();
                    });
                }
                userStatistics.increaseQuestionCount();
                userDepartments
                        .forEach(userDepartment -> {
                            getDepartmentStatistics(semester, category, field, userDepartment).increaseQuestionCount();
                            getCollegeStatistics(semester, category, field, userDepartment).increaseQuestionCount();
                        });
            } else if(type == 8) {
                userCountStatistics.increaseTotalGithubProjectCount();
                if (projectJpaRepository.countByUserAndSemesterAndProjectFields_FieldAndState(user, semester, field, ACTIVE) == 1) {
                    userCountStatistics.increaseUserProjectCount();
                    userDepartments.forEach(userDepartment -> {
                        getDepartmentStatistics(semester, category, field, userDepartment).increaseProjectUserCount();
                        getCollegeStatistics(semester, category, field, userDepartment).increaseProjectUserCount();
                    });
                }
                userStatistics.increaseGithubProjectCount();
                userDepartments
                        .forEach(userDepartment -> {
                            getDepartmentStatistics(semester, category, field, userDepartment).increaseGithubProjectCount();
                            getCollegeStatistics(semester, category, field, userDepartment).increaseGithubProjectCount();
                        });
            }
        }
    }

    private CollegeStatistics getCollegeStatistics(Semester semester, Category category, Field field, UserDepartment userDepartment) {
        return collegeStatisticsJpaRepository.findById(new CollegeStatisticsStatisticsId(userDepartment.getDepartment().getCollege().getId(), semester.getId(), field.getId(), category.getId()))
                .orElseGet(() -> collegeStatisticsJpaRepository.save((collegeMapper.createCollegeStatistics(userDepartment.getDepartment().getCollege(), semester, field, category))));
    }

    private DepartmentStatistics getDepartmentStatistics(Semester semester, Category category, Field field, UserDepartment userDepartment) {
        return departmentStatisticsJpaRepository.findById(new DepartmentStatisticsId(userDepartment.getDepartment().getId(), semester.getId(), field.getId(), category.getId()))
                .orElseGet(() -> departmentStatisticsJpaRepository.save(departmentMapper.createDepartmentStatistics(userDepartment.getDepartment(), semester, field, category)));
    }

    /**
     * 사용자 통계 정보를 감소시킨다.
     *
     * @param user User
     * @param type Integer
     */
    public void decreaseCount(User user, List<Field> fields, Semester semester, Category category, Integer type) {
        TotalUserStatistics totalUserStatistics = totalUserStatisticsJpaRepository.findById(1) // 예시로 ID 1번 TotalStatistics 조회
                .orElseThrow(() -> new BaseException(TOTAL_STATISTICS_NOT_FOUND));
        if (type == 1 || type == 8) {
            if (hasUploadedExactlyOneProject(user)) {
                totalUserStatistics.decreaseUserProjectCount(); // 사용자 프로젝트 카운트 감소
                createOrUpdateDepartmentAndCollegeStatistics(user, false,
                        TotalDepartmentStatistics::decreaseUserProjectCount,
                        TotalCollegeStatistics::decreaseUserProjectCount); // 학과 및 단과대 통계 업데이트
            }
            // 전체 통계 업데이트
            reverseTotalStatistics(totalUserStatistics, type);
            if (type == 1) {
                // 일반 프로젝트 통계 업데이트
                createOrUpdateDepartmentAndCollegeStatistics(user, false,
                        TotalDepartmentStatistics::decreaseTotalProjectCount,
                        TotalCollegeStatistics::decreaseTotalProjectCount);
            } else {
                // 깃허브 프로젝트 통계 업데이트
                createOrUpdateDepartmentAndCollegeStatistics(user, false,
                        TotalDepartmentStatistics::decreaseTotalGithubProjectCount,
                        TotalCollegeStatistics::decreaseTotalGithubProjectCount);
            }
        }

        if (type == 2) {
            if (hasUploadedExactlyOneQuestion(user)) {
                totalUserStatistics.decreaseUserQuestionCount();
                createOrUpdateDepartmentAndCollegeStatistics(user, false,
                        TotalDepartmentStatistics::decreaseUserQuestionCount,
                        TotalCollegeStatistics::decreaseUserQuestionCount);
            }
            reverseTotalStatistics(totalUserStatistics, type);
            createOrUpdateDepartmentAndCollegeStatistics(user, false,
                    TotalDepartmentStatistics::decreaseTotalQuestionCount,
                    TotalCollegeStatistics::decreaseTotalQuestionCount);
        }
        for(Field field: fields) {
            UserStatistics userStatistics = userStatisticsJpaRepository
                    .findById(new UserStatisticsId(user.getId(), semester.getId(), field.getId(), category.getId()))
                    .orElseGet(() -> userMapper.createUserStatistics(user, semester, field, category));
            List<UserDepartment> userDepartments = userDepartmentJpaRepository.findByUserId(user.getId()).orElseThrow(() -> new BaseException(USER_DEPARTMENT_NOT_FOUND));
            UserCountStatistics userCountStatistics = userCountStatisticsJpaRepository
                    .findById(new UserCountStatisticsId(semester.getId(), field.getId(), category.getId()))
                    .orElseGet(() -> userCountStatisticsJpaRepository.save(
                            userMapper.createUserCountStatistics(semester, field, category)));
            if(type == 1) {
                userCountStatistics.decreaseTotalProjectCount();
                if (projectJpaRepository.countByUserAndSemesterAndProjectFields_FieldAndState(user, semester, field, ACTIVE) == 0) {
                    userCountStatistics.decreaseUserProjectCount();
                    userDepartments
                            .forEach(userDepartment -> {
                                getDepartmentStatistics2(semester, category, field, userDepartment).decreaseProjectUserCount();
                                getCollegeStatistics2(semester, category, field, userDepartment).decreaseProjectUserCount();
                            });
                }
                userStatistics.decreaseProjectCount();
                userDepartments
                        .forEach(userDepartment -> {
                            getDepartmentStatistics2(semester, category, field, userDepartment).decreaseProjectCount();
                            getCollegeStatistics2(semester, category, field, userDepartment).decreaseProjectCount();
                        });
            } else if(type == 2) {
                userCountStatistics.decreaseTotalQuestionCount();
                if(userStatistics.getQuestionCount() == 1) {
                    userCountStatistics.decreaseUserQuestionCount();
                    userDepartments
                            .forEach(userDepartment -> {
                                getDepartmentStatistics2(semester, category, field, userDepartment).decreaseQuestionUserCount();
                                getCollegeStatistics2(semester, category, field, userDepartment).decreaseQuestionUserCount();
                            });
                }
                userStatistics.decreaseQuestionCount();
                userDepartments
                        .forEach(userDepartment -> {
                            getDepartmentStatistics2(semester, category, field, userDepartment).decreaseQuestionCount();
                            getCollegeStatistics2(semester, category, field, userDepartment).decreaseQuestionCount();
                        });
            } else if(type == 8) {
                userCountStatistics.decreaseTotalGithubProjectCount();
                if((projectJpaRepository.countByUserAndSemesterAndProjectFields_FieldAndState(user, semester, field, ACTIVE) == 0)) {
                    userCountStatistics.decreaseUserProjectCount();
                    userDepartments
                            .forEach(userDepartment -> {
                                getDepartmentStatistics2(semester, category, field, userDepartment).decreaseProjectUserCount();
                                getCollegeStatistics2(semester, category, field, userDepartment).decreaseProjectUserCount();
                            });
                }
                userStatistics.decreaseGithubProjectCount();
                userDepartments
                        .forEach(userDepartment -> {
                            getDepartmentStatistics2(semester, category, field, userDepartment).decreaseGithubProjectCount();
                            getCollegeStatistics2(semester, category, field, userDepartment).decreaseGithubProjectCount();
                        });
            }
        }

    }

    private CollegeStatistics getCollegeStatistics2(Semester semester, Category category, Field field, UserDepartment userDepartment) {
        return collegeStatisticsJpaRepository.findById(new CollegeStatisticsStatisticsId(userDepartment.getDepartment().getCollege().getId(), semester.getId(), field.getId(), category.getId()))
                .orElseThrow(() -> new BaseException(COLLEGE_STATISTICS_NOT_FOUND));
    }

    private DepartmentStatistics getDepartmentStatistics2(Semester semester, Category category, Field field, UserDepartment userDepartment) {
        return departmentStatisticsJpaRepository.findById(new DepartmentStatisticsId(userDepartment.getDepartment().getId(), semester.getId(), field.getId(), category.getId()))
                .orElseThrow(() -> new BaseException(DEPARTMENT_STATISTICS_NOT_FOUND));
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

    // 전체 통계 업데이트
    private void updateTotalStatistics(TotalUserStatistics totalUserStatistics, int type) {
        if(type == 1) {
            totalUserStatistics.increaseTotalProjectCount(); // 전체 프로젝트 카운트 증가
        }
        else if(type ==2 ) {
            totalUserStatistics.increaseTotalQuestionCount(); // 전체 질문 카운트 증가
        }
        else if(type == 8) {
            totalUserStatistics.increaseTotalGithubProjectCount(); // 전체 깃허브 프로젝트 카운트 증가
        }
    }

    private void reverseTotalStatistics(TotalUserStatistics totalUserStatistics, int type) {
        if(type == 1) {
            totalUserStatistics.decreaseTotalProjectCount(); // 전체 프로젝트 카운트 감소
        }
        else if(type == 2) {
            totalUserStatistics.decreaseTotalQuestionCount(); // 전체 질문 카운트 감소
        }
        else if(type == 8) {
            totalUserStatistics.decreaseTotalGithubProjectCount(); // 전체 깃허브 프로젝트 카운트 감소
        }
    }

    // 유저가 프로젝트를 올린 적이 있는지 확인
    private boolean hasUploadedAnyProject(User user) {
        boolean hasUploadedGeneralProject = userStatisticsJpaRepository.countByUserIdAndProjectCountGreaterThan(user.getId(), 0) > 0;
        boolean hasUploadedGithubProject = userStatisticsJpaRepository.countByUserIdAndGithubProjectCountGreaterThan(user.getId(), 0) > 0;
        return hasUploadedGeneralProject || hasUploadedGithubProject;
    }

    private boolean hasUploadedExactlyOneProject(User user) {
        int generalProjectCount = userStatisticsJpaRepository.countByUserIdAndProjectCount(user.getId(), 1);
        // 깃허브 프로젝트 개수가 정확히 1개인 경우
        int githubProjectCount = userStatisticsJpaRepository.countByUserIdAndGithubProjectCount(user.getId(), 1);
        // 두 프로젝트의 총합이 1개인지 확인
        return (generalProjectCount + githubProjectCount) == 1;
    }

    private boolean hasUploadedAnyQuestion(User user) {
        return userStatisticsJpaRepository.countByUserIdAndQuestionCountGreaterThan(user.getId(), 0) > 0;
    }

    private boolean hasUploadedExactlyOneQuestion(User user) {
        return userStatisticsJpaRepository.countByUserIdAndQuestionCount(user.getId(), 1) == 1;
    }


    /**
     * 학과 및 단과대 통계를 생성하거나 업데이트
     */
    private void createOrUpdateDepartmentAndCollegeStatistics(User user, boolean isIncrease,
                                                              Consumer<TotalDepartmentStatistics> departmentUpdater,
                                                              Consumer<TotalCollegeStatistics> collegeUpdater) {
        List<UserDepartment> userDepartments = userDepartmentJpaRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BaseException(USER_DEPARTMENT_NOT_FOUND));

        userDepartments.stream()
                .map(UserDepartment::getDepartment)
                .forEach(department -> {
                    // 학과 통계 생성 또는 업데이트
                    TotalDepartmentStatistics deptStats = totalDepartmentStatisticsJpaRepository
                            .findById(department.getId())
                            .orElseGet(() -> totalDepartmentStatisticsJpaRepository.save(
                                    departmentMapper.createTotalDepartmentStatistics(department)));
                    departmentUpdater.accept(deptStats);

                    // 단과대 통계 생성 또는 업데이트
                    TotalCollegeStatistics collegeStats = totalCollegeStatisticsJpaRepository
                            .findById(department.getCollege().getId())
                            .orElseGet(() -> totalCollegeStatisticsJpaRepository.save(
                                    collegeMapper.createTotalCollegeStatistics(department.getCollege())));
                    collegeUpdater.accept(collegeStats);
                });
    }
}
