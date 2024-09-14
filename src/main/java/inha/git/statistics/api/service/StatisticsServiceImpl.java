package inha.git.statistics.api.service;

import inha.git.college.domain.repository.CollegeJpaRepository;
import inha.git.common.exceptions.BaseException;
import inha.git.department.domain.Department;
import inha.git.department.domain.repository.DepartmentJpaRepository;
import inha.git.field.domain.Field;
import inha.git.field.domain.repository.FieldJpaRepository;
import inha.git.mapping.domain.UserDepartment;
import inha.git.mapping.domain.repository.UserDepartmentJpaRepository;
import inha.git.semester.domain.Semester;
import inha.git.semester.domain.repository.SemesterJpaRepository;
import inha.git.statistics.api.controller.dto.request.SearchCond;
import inha.git.statistics.api.controller.dto.response.*;
import inha.git.statistics.domain.*;
import inha.git.statistics.domain.id.CollegeStatisticsStatisticsId;
import inha.git.statistics.domain.id.DepartmentStatisticsId;
import inha.git.statistics.domain.id.UserCountStatisticsId;
import inha.git.statistics.domain.id.UserStatisticsId;
import inha.git.statistics.domain.repository.*;
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
    private final CollegeStatisticsJpaRepository collegeStatisticsJpaRepository;
    private final ProjectStatisticsQueryRepository projectStatisticsQueryRepository;
    private final QuestionStatisticsQueryRepository questionStatisticsQueryRepository;
    private final DepartmentJpaRepository departmentJpaRepository;
    private final CollegeJpaRepository collegeJpaRepository;
    private final FieldJpaRepository fieldJpaRepository;
    private final SemesterJpaRepository semesterJpaRepository;



    /**
     * 사용자 통계 정보를 증가시킨다.
     *
     * @param user User
     * @param type Integer
     */
    public void increaseCount(User user, List<Field> fields, Semester semester, Integer type) {
        TotalUserStatistics totalUserStatistics = totalUserStatisticsJpaRepository.findById(1) // 예시로 ID 1번 TotalStatistics 조회
                .orElseThrow(() -> new BaseException(TOTAL_STATISTICS_NOT_FOUND));
        if (type == 1 || type == 8) {
            if (!hasUploadedAnyProject(user)) {
                totalUserStatistics.increaseUserProjectCount(); // 사용자 프로젝트 카운트 증가
                updateDepartmentAndCollegeStatistics(user,
                        TotalDepartmentStatistics::increaseUserProjectCount,
                        TotalCollegeStatistics::increaseUserProjectCount); // 학과 및 단과대 통계 업데이트
            }
            // 전체 통계 업데이트
            updateTotalStatistics(totalUserStatistics, type);
            if (type == 1) {
                // 일반 프로젝트 통계 업데이트
                updateDepartmentAndCollegeStatistics(user,
                        TotalDepartmentStatistics::increaseTotalProjectCount,
                        TotalCollegeStatistics::increaseTotalProjectCount);
            } else {
                // 깃허브 프로젝트 통계 업데이트
                updateDepartmentAndCollegeStatistics(user,
                        TotalDepartmentStatistics::increaseTotalGithubProjectCount,
                        TotalCollegeStatistics::increaseTotalGithubProjectCount);
            }
        }
        for(Field field: fields) {
            UserStatistics userStatistics = userStatisticsJpaRepository.findById(new UserStatisticsId(user.getId(), semester.getId(), field.getId()))
                    .orElseThrow(() -> new BaseException(USER_COUNT_STATISTICS_NOT_FOUND));
            List<UserDepartment> userDepartments = userDepartmentJpaRepository.findByUserId(user.getId()).orElseThrow(() -> new BaseException(USER_DEPARTMENT_NOT_FOUND));
            UserCountStatistics userCountStatistics= userCountStatisticsJpaRepository.findById(new UserCountStatisticsId(semester.getId(), field.getId())).orElseThrow(() -> new BaseException(USER_COUNT_STATISTICS_NOT_FOUND));

            if(type == 1){
                userCountStatistics.increaseTotalProjectCount();
                if (userStatistics.getProjectCount() == 0 && userStatistics.getGithubProjectCount() == 0) {
                    userCountStatistics.increaseUserProjectCount();
                    userDepartments.forEach(userDepartment -> {
                        departmentStatisticsJpaRepository.findById(new DepartmentStatisticsId(userDepartment.getDepartment().getId(), semester.getId(), field.getId()))
                                .orElseThrow(() -> new BaseException(DEPARTMENT_STATISTICS_NOT_FOUND)).increaseProjectUserCount();
                        collegeStatisticsJpaRepository.findById(new CollegeStatisticsStatisticsId(userDepartment.getDepartment().getCollege().getId(), semester.getId(), field.getId()))
                                .orElseThrow(() -> new BaseException(COLLEGE_STATISTICS_NOT_FOUND)).increaseProjectUserCount();
                    });
                }
                userStatistics.increaseProjectCount();
                userDepartments
                        .forEach(userDepartment -> {
                            departmentStatisticsJpaRepository.findById(new DepartmentStatisticsId(userDepartment.getDepartment().getId(), semester.getId(), field.getId()))
                                    .orElseThrow(() -> new BaseException(DEPARTMENT_STATISTICS_NOT_FOUND)).increaseProjectCount();
                            collegeStatisticsJpaRepository.findById(new CollegeStatisticsStatisticsId(userDepartment.getDepartment().getCollege().getId(), semester.getId(), field.getId()))
                                    .orElseThrow(() -> new BaseException(COLLEGE_STATISTICS_NOT_FOUND)).increaseProjectCount();
                        });
            }else if(type == 2) {
                userCountStatistics.increaseTotalQuestionCount();
                if (userStatistics.getQuestionCount() == 0) {
                    userCountStatistics.increaseUserQuestionCount();
                    userDepartments.forEach(userDepartment -> {
                        departmentStatisticsJpaRepository.findById(new DepartmentStatisticsId(userDepartment.getDepartment().getId(), semester.getId(), field.getId()))
                                .orElseThrow(() -> new BaseException(DEPARTMENT_STATISTICS_NOT_FOUND)).increaseQuestionUserCount();
                        collegeStatisticsJpaRepository.findById(new CollegeStatisticsStatisticsId(userDepartment.getDepartment().getCollege().getId(), semester.getId(), field.getId()))
                                .orElseThrow(() -> new BaseException(COLLEGE_STATISTICS_NOT_FOUND)).increaseQuestionUserCount();
                    });
                }
                userStatistics.increaseQuestionCount();
                userDepartments
                        .forEach(userDepartment -> {
                            departmentStatisticsJpaRepository.findById(new DepartmentStatisticsId(userDepartment.getDepartment().getId(), semester.getId(), field.getId()))
                                    .orElseThrow(() -> new BaseException(DEPARTMENT_STATISTICS_NOT_FOUND)).increaseQuestionCount();
                            collegeStatisticsJpaRepository.findById(new CollegeStatisticsStatisticsId(userDepartment.getDepartment().getCollege().getId(), semester.getId(), field.getId()))
                                    .orElseThrow(() -> new BaseException(COLLEGE_STATISTICS_NOT_FOUND)).increaseQuestionCount();
                        });
            }
            else if(type == 3){
                userCountStatistics.increaseTotalTeamCount();
                if (userStatistics.getTeamCount() == 0) {
                    userCountStatistics.increaseUserTeamCount();
                    userDepartments.forEach(userDepartment -> {
                        departmentStatisticsJpaRepository.findById(new DepartmentStatisticsId(userDepartment.getDepartment().getId(), semester.getId(), field.getId()))
                                .orElseThrow(() -> new BaseException(DEPARTMENT_STATISTICS_NOT_FOUND)).increaseTeamUserCount();
                        collegeStatisticsJpaRepository.findById(new CollegeStatisticsStatisticsId(userDepartment.getDepartment().getCollege().getId(), semester.getId(), field.getId()))
                                .orElseThrow(() -> new BaseException(COLLEGE_STATISTICS_NOT_FOUND)).increaseTeamUserCount();
                    });
                }
                userStatistics.increaseTeamCount();
                userDepartments
                        .forEach(userDepartment -> {
                            departmentStatisticsJpaRepository.findById(new DepartmentStatisticsId(userDepartment.getDepartment().getId(), semester.getId(), field.getId()))
                                    .orElseThrow(() -> new BaseException(DEPARTMENT_STATISTICS_NOT_FOUND)).increaseTeamCount();
                            collegeStatisticsJpaRepository.findById(new CollegeStatisticsStatisticsId(userDepartment.getDepartment().getCollege().getId(), semester.getId(), field.getId()))
                                    .orElseThrow(() -> new BaseException(COLLEGE_STATISTICS_NOT_FOUND)).increaseTeamCount();
                        });
            }
            else if (type == 4) {
                if (userStatistics.getTeamCount() == 0) {
                    userCountStatistics.increaseUserTeamCount();
                    userDepartments
                            .forEach(userDepartment -> {
                                departmentStatisticsJpaRepository.findById(new DepartmentStatisticsId(userDepartment.getDepartment().getId(), semester.getId(), field.getId()))
                                        .orElseThrow(() -> new BaseException(DEPARTMENT_STATISTICS_NOT_FOUND)).increaseTeamUserCount();
                                collegeStatisticsJpaRepository.findById(new CollegeStatisticsStatisticsId(userDepartment.getDepartment().getCollege().getId(), semester.getId(), field.getId()))
                                        .orElseThrow(() -> new BaseException(COLLEGE_STATISTICS_NOT_FOUND)).increaseTeamUserCount();
                            });
                }
                userStatistics.increaseTeamCount();
            }
            else if(type == 5) {
                userCountStatistics.increaseTotalPatentCount();
                if (userStatistics.getPatentCount() == 0) {
                    userCountStatistics.increaseUserPatentCount();
                    userDepartments
                            .forEach(userDepartment -> {
                                departmentStatisticsJpaRepository.findById(new DepartmentStatisticsId(userDepartment.getDepartment().getId(), semester.getId(), field.getId()))
                                        .orElseThrow(() -> new BaseException(DEPARTMENT_STATISTICS_NOT_FOUND)).increasePatentUserCount();
                                collegeStatisticsJpaRepository.findById(new CollegeStatisticsStatisticsId(userDepartment.getDepartment().getCollege().getId(), semester.getId(), field.getId()))
                                        .orElseThrow(() -> new BaseException(COLLEGE_STATISTICS_NOT_FOUND)).increasePatentUserCount();
                            });
                }
                userStatistics.increasePatentCount();
                userDepartments
                        .forEach(userDepartment -> {
                            departmentStatisticsJpaRepository.findById(new DepartmentStatisticsId(userDepartment.getDepartment().getId(), semester.getId(), field.getId()))
                                    .orElseThrow(() -> new BaseException(DEPARTMENT_STATISTICS_NOT_FOUND)).increasePatentCount();
                            collegeStatisticsJpaRepository.findById(new CollegeStatisticsStatisticsId(userDepartment.getDepartment().getCollege().getId(), semester.getId(), field.getId()))
                                    .orElseThrow(() -> new BaseException(COLLEGE_STATISTICS_NOT_FOUND)).increasePatentCount();
                        });
            }
            else if(type == 6) {
                userCountStatistics.increaseTotalProblemCount();
                userDepartments
                        .forEach(userDepartment -> {
                            departmentStatisticsJpaRepository.findById(new DepartmentStatisticsId(userDepartment.getDepartment().getId(), semester.getId(), field.getId()))
                                    .orElseThrow(() -> new BaseException(DEPARTMENT_STATISTICS_NOT_FOUND)).increaseProblemCount();
                            collegeStatisticsJpaRepository.findById(new CollegeStatisticsStatisticsId(userDepartment.getDepartment().getCollege().getId(), semester.getId(), field.getId()))
                                    .orElseThrow(() -> new BaseException(COLLEGE_STATISTICS_NOT_FOUND)).increaseProblemCount();});
            }
            else if(type == 7) {
                if (userStatistics.getProblemCount() == 0) {
                    userCountStatistics.increaseUserProblemCount();
                    userDepartments
                            .forEach(userDepartment -> {
                                departmentStatisticsJpaRepository.findById(new DepartmentStatisticsId(userDepartment.getDepartment().getId(), semester.getId(), field.getId()))
                                        .orElseThrow(() -> new BaseException(DEPARTMENT_STATISTICS_NOT_FOUND)).increaseProblemUserCount();
                                collegeStatisticsJpaRepository.findById(new CollegeStatisticsStatisticsId(userDepartment.getDepartment().getCollege().getId(), semester.getId(), field.getId()))
                                        .orElseThrow(() -> new BaseException(COLLEGE_STATISTICS_NOT_FOUND)).increaseProblemUserCount();
                            });
                }
                userStatistics.increaseProblemCount();
                userDepartments
                        .forEach(userDepartment -> {
                            departmentStatisticsJpaRepository.findById(new DepartmentStatisticsId(userDepartment.getDepartment().getId(), semester.getId(), field.getId()))
                                    .orElseThrow(() -> new BaseException(DEPARTMENT_STATISTICS_NOT_FOUND)).increaseProblemParticipationCount();
                            collegeStatisticsJpaRepository.findById(new CollegeStatisticsStatisticsId(userDepartment.getDepartment().getCollege().getId(), semester.getId(), field.getId()))
                                    .orElseThrow(() -> new BaseException(COLLEGE_STATISTICS_NOT_FOUND)).increaseProblemParticipationCount();
                        });
            } else if(type == 8) {
                userCountStatistics.increaseTotalGithubProjectCount();
                if (userStatistics.getProjectCount() == 0 && userStatistics.getGithubProjectCount() == 0) {
                    userCountStatistics.increaseUserProjectCount();
                    userDepartments.forEach(userDepartment -> {
                        departmentStatisticsJpaRepository.findById(new DepartmentStatisticsId(userDepartment.getDepartment().getId(), semester.getId(), field.getId()))
                                .orElseThrow(() -> new BaseException(DEPARTMENT_STATISTICS_NOT_FOUND)).increaseProjectUserCount();
                        collegeStatisticsJpaRepository.findById(new CollegeStatisticsStatisticsId(userDepartment.getDepartment().getCollege().getId(), semester.getId(), field.getId()))
                                .orElseThrow(() -> new BaseException(COLLEGE_STATISTICS_NOT_FOUND)).increaseProjectUserCount();
                    });
                }
                userStatistics.increaseGithubProjectCount();
                userDepartments
                        .forEach(userDepartment -> {
                            departmentStatisticsJpaRepository.findById(new DepartmentStatisticsId(userDepartment.getDepartment().getId(), semester.getId(), field.getId()))
                                    .orElseThrow(() -> new BaseException(DEPARTMENT_STATISTICS_NOT_FOUND)).increaseGithubProjectCount();
                            collegeStatisticsJpaRepository.findById(new CollegeStatisticsStatisticsId(userDepartment.getDepartment().getCollege().getId(), semester.getId(), field.getId()))
                                    .orElseThrow(() -> new BaseException(COLLEGE_STATISTICS_NOT_FOUND)).increaseGithubProjectCount();
                        });
            }

        }


    }

    /**
     * 사용자 통계 정보를 감소시킨다.
     *
     * @param user User
     * @param type Integer
     */
    public void decreaseCount(User user, List<Field> fields, Semester semester, Integer type) {
        for(Field field: fields) {
            UserStatistics userStatistics = userStatisticsJpaRepository.findById(new UserStatisticsId(user.getId(), semester.getId(), field.getId()))
                    .orElseThrow(() -> new BaseException(USER_COUNT_STATISTICS_NOT_FOUND));
            List<UserDepartment> userDepartments = userDepartmentJpaRepository.findByUserId(user.getId()).orElseThrow(() -> new BaseException(USER_DEPARTMENT_NOT_FOUND));
            UserCountStatistics userCountStatistics = userCountStatisticsJpaRepository.findById(new UserCountStatisticsId(semester.getId(), field.getId())).orElseThrow(() -> new BaseException(USER_COUNT_STATISTICS_NOT_FOUND));
            if(type == 1) {
                userCountStatistics.decreaseTotalProjectCount();
                if(userStatistics.getProjectCount() == 1 && userStatistics.getGithubProjectCount() == 0) {
                    userCountStatistics.decreaseUserProjectCount();
                    userDepartments
                            .forEach(userDepartment -> {
                                departmentStatisticsJpaRepository.findById(new DepartmentStatisticsId(userDepartment.getDepartment().getId(), semester.getId(), field.getId()))
                                        .orElseThrow(() -> new BaseException(DEPARTMENT_STATISTICS_NOT_FOUND)).decreaseProjectUserCount();
                                collegeStatisticsJpaRepository.findById(new CollegeStatisticsStatisticsId(userDepartment.getDepartment().getCollege().getId(), semester.getId(), field.getId()))
                                        .orElseThrow(() -> new BaseException(COLLEGE_STATISTICS_NOT_FOUND)).decreaseProjectUserCount();
                            });
                }
                userStatistics.decreaseProjectCount();
                userDepartments
                        .forEach(userDepartment -> {
                            departmentStatisticsJpaRepository.findById(new DepartmentStatisticsId(userDepartment.getDepartment().getId(), semester.getId(), field.getId()))
                                    .orElseThrow(() -> new BaseException(DEPARTMENT_STATISTICS_NOT_FOUND)).decreaseProjectCount();
                            collegeStatisticsJpaRepository.findById(new CollegeStatisticsStatisticsId(userDepartment.getDepartment().getCollege().getId(), semester.getId(), field.getId()))
                                    .orElseThrow(() -> new BaseException(COLLEGE_STATISTICS_NOT_FOUND)).decreaseProjectCount();
                        });
            } else if(type == 2) {
                userCountStatistics.decreaseTotalQuestionCount();
                if(userStatistics.getQuestionCount() == 1) {
                    userCountStatistics.decreaseUserQuestionCount();
                    userDepartments
                            .forEach(userDepartment -> {
                                departmentStatisticsJpaRepository.findById(new DepartmentStatisticsId(userDepartment.getDepartment().getId(), semester.getId(), field.getId()))
                                        .orElseThrow(() -> new BaseException(DEPARTMENT_STATISTICS_NOT_FOUND)).decreaseQuestionUserCount();
                                collegeStatisticsJpaRepository.findById(new CollegeStatisticsStatisticsId(userDepartment.getDepartment().getCollege().getId(), semester.getId(), field.getId()))
                                        .orElseThrow(() -> new BaseException(COLLEGE_STATISTICS_NOT_FOUND)).decreaseQuestionUserCount();
                            });
                }
                userStatistics.decreaseQuestionCount();
                userDepartments
                        .forEach(userDepartment -> {
                            departmentStatisticsJpaRepository.findById(new DepartmentStatisticsId(userDepartment.getDepartment().getId(), semester.getId(), field.getId()))
                                    .orElseThrow(() -> new BaseException(DEPARTMENT_STATISTICS_NOT_FOUND)).decreaseQuestionCount();
                            collegeStatisticsJpaRepository.findById(new CollegeStatisticsStatisticsId(userDepartment.getDepartment().getCollege().getId(), semester.getId(), field.getId()))
                                    .orElseThrow(() -> new BaseException(COLLEGE_STATISTICS_NOT_FOUND)).decreaseQuestionCount();
                        });
            } else if(type == 3) {
                userCountStatistics.decreaseTotalTeamCount();
                if(userStatistics.getTeamCount() == 1) {
                    userCountStatistics.decreaseUserTeamCount();
                    userDepartments
                            .forEach(userDepartment -> {
                                departmentStatisticsJpaRepository.findById(new DepartmentStatisticsId(userDepartment.getDepartment().getId(), semester.getId(), field.getId()))
                                        .orElseThrow(() -> new BaseException(DEPARTMENT_STATISTICS_NOT_FOUND)).decreaseTeamUserCount();
                                collegeStatisticsJpaRepository.findById(new CollegeStatisticsStatisticsId(userDepartment.getDepartment().getCollege().getId(), semester.getId(), field.getId()))
                                        .orElseThrow(() -> new BaseException(COLLEGE_STATISTICS_NOT_FOUND)).decreaseTeamUserCount();
                            });
                }
                userStatistics.decreaseTeamCount();
                userDepartments
                        .forEach(userDepartment -> {
                            departmentStatisticsJpaRepository.findById(new DepartmentStatisticsId(userDepartment.getDepartment().getId(), semester.getId(), field.getId()))
                                    .orElseThrow(() -> new BaseException(DEPARTMENT_STATISTICS_NOT_FOUND)).decreaseTeamCount();
                            collegeStatisticsJpaRepository.findById(new CollegeStatisticsStatisticsId(userDepartment.getDepartment().getCollege().getId(), semester.getId(), field.getId()))
                                    .orElseThrow(() -> new BaseException(COLLEGE_STATISTICS_NOT_FOUND)).decreaseTeamCount();
                        });
            } else if (type == 4) {
                if(userStatistics.getTeamCount() == 1) {
                    userCountStatistics.decreaseUserTeamCount();
                    userDepartments
                            .forEach(userDepartment -> {
                                departmentStatisticsJpaRepository.findById(new DepartmentStatisticsId(userDepartment.getDepartment().getId(), semester.getId(), field.getId()))
                                        .orElseThrow(() -> new BaseException(DEPARTMENT_STATISTICS_NOT_FOUND)).decreaseTeamUserCount();
                                collegeStatisticsJpaRepository.findById(new CollegeStatisticsStatisticsId(userDepartment.getDepartment().getCollege().getId(), semester.getId(), field.getId()))
                                        .orElseThrow(() -> new BaseException(COLLEGE_STATISTICS_NOT_FOUND)).decreaseTeamUserCount();
                            });
                }
            }
            else if(type == 5) {
                userCountStatistics.decreaseTotalPatentCount();
                if(userStatistics.getPatentCount() == 1) {
                    userCountStatistics.decreaseUserPatentCount();
                    userStatistics.decreasePatentCount();
                    userDepartments
                            .forEach(userDepartment -> {
                                departmentStatisticsJpaRepository.findById(new DepartmentStatisticsId(userDepartment.getDepartment().getId(), semester.getId(), field.getId()))
                                        .orElseThrow(() -> new BaseException(DEPARTMENT_STATISTICS_NOT_FOUND)).decreasePatentUserCount();
                                collegeStatisticsJpaRepository.findById(new CollegeStatisticsStatisticsId(userDepartment.getDepartment().getCollege().getId(), semester.getId(), field.getId()))
                                        .orElseThrow(() -> new BaseException(COLLEGE_STATISTICS_NOT_FOUND)).decreasePatentUserCount();
                            });
                }
                userStatistics.decreasePatentCount();
                userDepartments
                        .forEach(userDepartment -> {
                            departmentStatisticsJpaRepository.findById(new DepartmentStatisticsId(userDepartment.getDepartment().getId(), semester.getId(), field.getId()))
                                    .orElseThrow(() -> new BaseException(DEPARTMENT_STATISTICS_NOT_FOUND)).decreasePatentCount();
                            collegeStatisticsJpaRepository.findById(new CollegeStatisticsStatisticsId(userDepartment.getDepartment().getCollege().getId(), semester.getId(), field.getId()))
                                    .orElseThrow(() -> new BaseException(COLLEGE_STATISTICS_NOT_FOUND)).decreasePatentCount();
                        });
            }
            else if(type == 6) {
                userCountStatistics.decreaseTotalProblemCount();
                userDepartments
                        .forEach(userDepartment -> {
                            departmentStatisticsJpaRepository.findById(new DepartmentStatisticsId(userDepartment.getDepartment().getId(), semester.getId(), field.getId()))
                                    .orElseThrow(() -> new BaseException(DEPARTMENT_STATISTICS_NOT_FOUND)).decreaseProblemCount();
                            collegeStatisticsJpaRepository.findById(new CollegeStatisticsStatisticsId(userDepartment.getDepartment().getCollege().getId(), semester.getId(), field.getId()))
                                    .orElseThrow(() -> new BaseException(COLLEGE_STATISTICS_NOT_FOUND)).decreaseProblemCount();
                        });
            }
            else if(type == 7) {
                if(userStatistics.getProblemCount() == 1) {
                    userCountStatistics.decreaseUserProblemCount();
                    userDepartments
                            .forEach(userDepartment -> {
                                departmentStatisticsJpaRepository.findById(new DepartmentStatisticsId(userDepartment.getDepartment().getId(), semester.getId(), field.getId()))
                                        .orElseThrow(() -> new BaseException(DEPARTMENT_STATISTICS_NOT_FOUND)).decreaseProblemUserCount();
                                collegeStatisticsJpaRepository.findById(new CollegeStatisticsStatisticsId(userDepartment.getDepartment().getCollege().getId(), semester.getId(), field.getId()))
                                        .orElseThrow(() -> new BaseException(COLLEGE_STATISTICS_NOT_FOUND)).decreaseProblemUserCount();
                            });
                }
                userStatistics.decreaseProblemCount();
                userDepartments
                        .forEach(userDepartment -> {
                            departmentStatisticsJpaRepository.findById(new DepartmentStatisticsId(userDepartment.getDepartment().getId(), semester.getId(), field.getId()))
                                    .orElseThrow(() -> new BaseException(DEPARTMENT_STATISTICS_NOT_FOUND)).decreaseProblemParticipationCount();
                            collegeStatisticsJpaRepository.findById(new CollegeStatisticsStatisticsId(userDepartment.getDepartment().getCollege().getId(), semester.getId(), field.getId()))
                                    .orElseThrow(() -> new BaseException(COLLEGE_STATISTICS_NOT_FOUND)).decreaseProblemParticipationCount();
                        });
            } else if(type == 8) {
                userCountStatistics.decreaseTotalGithubProjectCount();
                if(userStatistics.getProjectCount() == 0 && userStatistics.getGithubProjectCount() == 1) {
                    userCountStatistics.decreaseUserProjectCount();
                    userDepartments
                            .forEach(userDepartment -> {
                                departmentStatisticsJpaRepository.findById(new DepartmentStatisticsId(userDepartment.getDepartment().getId(), semester.getId(), field.getId()))
                                        .orElseThrow(() -> new BaseException(DEPARTMENT_STATISTICS_NOT_FOUND)).decreaseProjectUserCount();
                                collegeStatisticsJpaRepository.findById(new CollegeStatisticsStatisticsId(userDepartment.getDepartment().getCollege().getId(), semester.getId(), field.getId()))
                                        .orElseThrow(() -> new BaseException(COLLEGE_STATISTICS_NOT_FOUND)).decreaseProjectUserCount();
                            });
                }
                userStatistics.decreaseGithubProjectCount();
                userDepartments
                        .forEach(userDepartment -> {
                            departmentStatisticsJpaRepository.findById(new DepartmentStatisticsId(userDepartment.getDepartment().getId(), semester.getId(), field.getId()))
                                    .orElseThrow(() -> new BaseException(DEPARTMENT_STATISTICS_NOT_FOUND)).decreaseGithubProjectCount();
                            collegeStatisticsJpaRepository.findById(new CollegeStatisticsStatisticsId(userDepartment.getDepartment().getCollege().getId(), semester.getId(), field.getId()))
                                    .orElseThrow(() -> new BaseException(COLLEGE_STATISTICS_NOT_FOUND)).decreaseGithubProjectCount();
                        });
            }
        }

    }

    /**
     * 학과별 전체 통계 정보를 조회한다.
     *
     * @return List<HomeStatisticsResponse>
     */
    @Override
    public List<HomeStatisticsResponse> getStatistics() {
//        return departmentJpaRepository.findAllByState(ACTIVE)
//                .stream()
//                .map(department -> {
//                    DepartmentStatistics departmentStatistics = departmentStatisticsJpaRepository.findById(department.getId())
//                            .orElseThrow(() -> new BaseException(DEPARTMENT_STATISTICS_NOT_FOUND));
//                    return statisticsMapper.toHomeStatisticsResponse(department,  departmentStatistics);
//                })
//                .toList();
        return null;
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

    @Override
    public TeamStatisticsResponse getTeamStatistics(Integer idx) {
//        if (idx != null) {
//            return departmentJpaRepository.findByIdAndState(idx, ACTIVE)
//                    .map(department -> {
//                        DepartmentStatistics departmentStatistics = departmentStatisticsJpaRepository.findById(department.getId())
//                                .orElseThrow(() -> new BaseException(DEPARTMENT_STATISTICS_NOT_FOUND));
//                        return statisticsMapper.toTeamStatisticsResponse(departmentStatistics.getTeamCount(), departmentStatistics.getTeamUserCount());
//                    })
//                    .orElseThrow(() -> new BaseException(DEPARTMENT_NOT_FOUND));
//        }
//        UserCountStatistics userCountStatistics = userCountStatisticsJpaRepository.findById(1)
//                .orElseThrow(() -> new BaseException(USER_COUNT_STATISTICS_NOT_FOUND));
//        return statisticsMapper.toTeamStatisticsResponse(userCountStatistics.getTotalTeamCount(), userCountStatistics.getUserTeamCount());
        return null;
    }

    @Override
    public ProblemStatisticsResponse getProblemStatistics(Integer idx) {
//        if (idx != null) {
//            return departmentJpaRepository.findByIdAndState(idx, ACTIVE)
//                    .map(department -> {
//                        DepartmentStatistics departmentStatistics = departmentStatisticsJpaRepository.findById(department.getId())
//                                .orElseThrow(() -> new BaseException(DEPARTMENT_STATISTICS_NOT_FOUND));
//                        return statisticsMapper.toProblemStatisticsResponse(departmentStatistics.getProblemCount(), departmentStatistics.getProblemUserCount());
//                    })
//                    .orElseThrow(() -> new BaseException(DEPARTMENT_NOT_FOUND));
//        }
//        UserCountStatistics userCountStatistics = userCountStatisticsJpaRepository.findById(1)
//                .orElseThrow(() -> new BaseException(USER_COUNT_STATISTICS_NOT_FOUND));
//        return statisticsMapper.toProblemStatisticsResponse(userCountStatistics.getTotalProblemCount(), userCountStatistics.getUserProblemCount());
        return null;
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
    }

    // 학과 및 단과대 통계 업데이트
    private void updateDepartmentAndCollegeStatistics(User user, Consumer<TotalDepartmentStatistics> departmentUpdater, Consumer<TotalCollegeStatistics> collegeUpdater) {
        log.info("여기 문제다!!!!!!!!");
        List<UserDepartment> userDepartments = userDepartmentJpaRepository.findByUserId(user.getId()).orElseThrow(() -> new BaseException(USER_DEPARTMENT_NOT_FOUND));
        userDepartments.stream()
                .map(userDepartment -> userDepartment.getDepartment())  // Department 객체 추출
                .peek(department -> {
                    // 학과 통계 업데이트
                    totalDepartmentStatisticsJpaRepository.findById(department.getId())
                            .ifPresentOrElse(
                                    departmentUpdater,
                                    () -> { throw new BaseException(TOTAL_DEPARTMENT_STATISTICS_NOT_FOUND); }
                            );
                })
                .map(department -> department.getCollege().getId())  // Department에서 CollegeId 추출
                .forEach(collegeId -> {
                    // 단과대 통계 업데이트
                    totalCollegeStatisticsJpaRepository.findById(collegeId)
                            .ifPresentOrElse(
                                    collegeUpdater,
                                    () -> { throw new BaseException(TOTAL_COLLEGE_STATISTICS_NOT_FOUND); }
                            );
                });
    }

    // 전체 통계 업데이트
    private void updateTotalStatistics(TotalUserStatistics totalUserStatistics, int type) {
        if(type == 1) {
            totalUserStatistics.increaseTotalProjectCount(); // 전체 프로젝트 카운트 증가
        } else if(type == 8) {
            totalUserStatistics.increaseTotalGithubProjectCount(); // 전체 깃허브 프로젝트 카운트 증가
        }
    }

    // 유저가 프로젝트를 올린 적이 있는지 확인
    private boolean hasUploadedAnyProject(User user) {
        boolean hasUploadedGeneralProject = userStatisticsJpaRepository.countByUserIdAndProjectCountGreaterThan(user.getId(), 0) > 0;
        boolean hasUploadedGithubProject = userStatisticsJpaRepository.countByUserIdAndGithubProjectCountGreaterThan(user.getId(), 0) > 0;
        return hasUploadedGeneralProject || hasUploadedGithubProject;
    }
}
