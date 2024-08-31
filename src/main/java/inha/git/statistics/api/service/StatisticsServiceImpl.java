package inha.git.statistics.api.service;

import inha.git.common.exceptions.BaseException;
import inha.git.mapping.domain.UserDepartment;
import inha.git.mapping.domain.repository.UserDepartmentJpaRepository;
import inha.git.statistics.domain.UserCountStatistics;
import inha.git.statistics.domain.UserStatistics;
import inha.git.statistics.domain.repository.DepartmentStatisticsJpaRepository;
import inha.git.statistics.domain.repository.UserCountStatisticsJpaRepository;
import inha.git.statistics.domain.repository.UserStatisticsJpaRepository;
import inha.git.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static inha.git.common.code.status.ErrorStatus.*;


/**
 * StatisticsServiceImpl은 통계 관련 비즈니스 로직을 처리한다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StatisticsServiceImpl implements StatisticsService {

    private final UserStatisticsJpaRepository userStatisticsJpaRepository;
    private final DepartmentStatisticsJpaRepository departmentStatisticsJpaRepository;
    private final UserDepartmentJpaRepository userDepartmentJpaRepository;
    private final UserCountStatisticsJpaRepository userCountStatisticsJpaRepository;

    /**
     * 사용자 통계 정보를 증가시킨다.
     *
     * @param user User
     * @param type Integer
     */
    public void increaseCount(User user, Integer type) {
        UserStatistics userStatistics = userStatisticsJpaRepository.findById(user.getId())
                .orElseThrow(() -> new BaseException(USER_STATISTICS_NOT_FOUND));
        List<UserDepartment> userDepartments = userDepartmentJpaRepository.findByUserId(user.getId()).orElseThrow(() -> new BaseException(USER_DEPARTMENT_NOT_FOUND));
        UserCountStatistics userCountStatistics = userCountStatisticsJpaRepository.findById(1).orElseThrow(() -> new BaseException(USER_COUNT_STATISTICS_NOT_FOUND));
        if(type == 1){
            if(userStatistics.getProjectCount() == 0) {
                userCountStatistics.increaseUserProjectCount();
            }
            userStatistics.increaseProjectCount();
            userDepartments
                    .forEach(userDepartment -> {
                        departmentStatisticsJpaRepository.findById(userDepartment.getDepartment().getId())
                                .orElseThrow(() -> new BaseException(DEPARTMENT_STATISTICS_NOT_FOUND)).increaseProjectCount();
                    });
        }else if(type == 2){
            if(userStatistics.getQuestionCount() == 0) {
                userCountStatistics.increaseUserQuestionCount();
            }
            userStatistics.increaseQuestionCount();
            userDepartments
                    .forEach(userDepartment -> {
                        departmentStatisticsJpaRepository.findById(userDepartment.getDepartment().getId())
                                .orElseThrow(() -> new BaseException(DEPARTMENT_STATISTICS_NOT_FOUND)).increaseQuestionCount();
                    });
        }else if(type == 3){
            if (userStatistics.getTeamCount() == 0) {
                userCountStatistics.increaseUserTeamCount();
            }
            userStatistics.increaseTeamCount();
            userDepartments
                    .forEach(userDepartment -> {
                        departmentStatisticsJpaRepository.findById(userDepartment.getDepartment().getId())
                                .orElseThrow(() -> new BaseException(DEPARTMENT_STATISTICS_NOT_FOUND)).increaseTeamCount();
                    });
        }else if(type == 4) {
            if (userStatistics.getPatentCount() == 0) {
                userCountStatistics.increaseUserPatentCount();
            }
            userStatistics.increasePatentCount();
            userDepartments
                    .forEach(userDepartment -> {
                        departmentStatisticsJpaRepository.findById(userDepartment.getDepartment().getId())
                                .orElseThrow(() -> new BaseException(DEPARTMENT_STATISTICS_NOT_FOUND)).increasePatentCount();
                    });
        }
    }

    /**
     * 사용자 통계 정보를 감소시킨다.
     *
     * @param user User
     * @param type Integer
     */
    public void decreaseCount(User user, Integer type) {
        UserStatistics userStatistics = userStatisticsJpaRepository.findById(user.getId())
                .orElseThrow(() -> new BaseException(USER_STATISTICS_NOT_FOUND));
        List<UserDepartment> userDepartments = userDepartmentJpaRepository.findByUserId(user.getId()).orElseThrow(() -> new BaseException(USER_DEPARTMENT_NOT_FOUND));
        UserCountStatistics userCountStatistics = userCountStatisticsJpaRepository.findById(1).orElseThrow(() -> new BaseException(USER_COUNT_STATISTICS_NOT_FOUND));
        if(type == 1) {
            if(userStatistics.getProjectCount() == 1) {
                userCountStatistics.decreaseUserProjectCount();
            }
            userStatistics.decreaseProjectCount();
            userDepartments
                    .forEach(userDepartment -> {
                        departmentStatisticsJpaRepository.findById(userDepartment.getDepartment().getId())
                                .orElseThrow(() -> new BaseException(DEPARTMENT_STATISTICS_NOT_FOUND)).decreaseProjectCount();
                    });
        } else if(type == 2) {
            if(userStatistics.getQuestionCount() == 1) {
                userCountStatistics.decreaseUserQuestionCount();
            }
            userStatistics.decreaseQuestionCount();
            userDepartments
                    .forEach(userDepartment -> {
                        departmentStatisticsJpaRepository.findById(userDepartment.getDepartment().getId())
                                .orElseThrow(() -> new BaseException(DEPARTMENT_STATISTICS_NOT_FOUND)).decreaseQuestionCount();
                    });
        } else if(type == 3) {
            if(userStatistics.getTeamCount() == 1) {
                userCountStatistics.decreaseUserTeamCount();
            }
            userStatistics.decreaseTeamCount();
            userDepartments
                    .forEach(userDepartment -> {
                        departmentStatisticsJpaRepository.findById(userDepartment.getDepartment().getId())
                                .orElseThrow(() -> new BaseException(DEPARTMENT_STATISTICS_NOT_FOUND)).decreaseTeamCount();
                    });
        } else if(type == 4) {
            if(userStatistics.getPatentCount() == 1) {
                userCountStatistics.decreaseUserPatentCount();
            }
            userStatistics.decreasePatentCount();
            userDepartments
                    .forEach(userDepartment -> {
                        departmentStatisticsJpaRepository.findById(userDepartment.getDepartment().getId())
                                .orElseThrow(() -> new BaseException(DEPARTMENT_STATISTICS_NOT_FOUND)).decreasePatentCount();
                    });
        }
    }
}
