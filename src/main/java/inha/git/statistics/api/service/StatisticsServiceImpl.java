package inha.git.statistics.api.service;

import inha.git.common.exceptions.BaseException;
import inha.git.mapping.domain.repository.UserDepartmentJpaRepository;
import inha.git.statistics.domain.repository.DepartmentStatisticsJpaRepository;
import inha.git.statistics.domain.repository.UserStatisticsJpaRepository;
import inha.git.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /**
     * 사용자 통계 정보를 증가시킨다.
     *
     * @param user User
     * @param type Integer
     */
    public void increaseCount(User user, Integer type) {
        if(type == 1){
            userStatisticsJpaRepository.findById(user.getId())
                    .orElseThrow(() -> new BaseException(USER_STATISTICS_NOT_FOUND)).increaseProjectCount();
            userDepartmentJpaRepository.findByUserId(user.getId()).orElseThrow(() -> new BaseException(USER_DEPARTMENT_NOT_FOUND))
                    .forEach(userDepartment -> {
                        departmentStatisticsJpaRepository.findById(userDepartment.getDepartment().getId())
                                .orElseThrow(() -> new BaseException(DEPARTMENT_STATISTICS_NOT_FOUND)).increaseProjectCount();
                    });
        }else if(type == 2){
            userStatisticsJpaRepository.findById(user.getId())
                    .orElseThrow(() -> new BaseException(USER_STATISTICS_NOT_FOUND)).increaseQuestionCount();
            userDepartmentJpaRepository.findByUserId(user.getId()).orElseThrow(() -> new BaseException(USER_DEPARTMENT_NOT_FOUND))
                    .forEach(userDepartment -> {
                        departmentStatisticsJpaRepository.findById(userDepartment.getDepartment().getId())
                                .orElseThrow(() -> new BaseException(DEPARTMENT_STATISTICS_NOT_FOUND)).increaseQuestionCount();
                    });
        }else if(type == 3){
            userStatisticsJpaRepository.findById(user.getId())
                    .orElseThrow(() -> new BaseException(USER_STATISTICS_NOT_FOUND)).increaseTeamCount();
            userDepartmentJpaRepository.findByUserId(user.getId()).orElseThrow(() -> new BaseException(USER_DEPARTMENT_NOT_FOUND))
                    .forEach(userDepartment -> {
                        departmentStatisticsJpaRepository.findById(userDepartment.getDepartment().getId())
                                .orElseThrow(() -> new BaseException(DEPARTMENT_STATISTICS_NOT_FOUND)).increaseTeamCount();
                    });
        }else if(type == 4) {
            userStatisticsJpaRepository.findById(user.getId())
                    .orElseThrow(() -> new BaseException(USER_STATISTICS_NOT_FOUND)).increasePatentCount();
            userDepartmentJpaRepository.findByUserId(user.getId()).orElseThrow(() -> new BaseException(USER_DEPARTMENT_NOT_FOUND))
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
        if(type == 1) {
            userStatisticsJpaRepository.findById(user.getId())
                    .orElseThrow(() -> new BaseException(USER_STATISTICS_NOT_FOUND)).decreaseProjectCount();
            userDepartmentJpaRepository.findByUserId(user.getId()).orElseThrow(() -> new BaseException(USER_DEPARTMENT_NOT_FOUND))
                    .forEach(userDepartment -> {
                        departmentStatisticsJpaRepository.findById(userDepartment.getDepartment().getId())
                                .orElseThrow(() -> new BaseException(DEPARTMENT_STATISTICS_NOT_FOUND)).decreaseProjectCount();
                    });
        } else if(type == 2) {
            userStatisticsJpaRepository.findById(user.getId())
                    .orElseThrow(() -> new BaseException(USER_STATISTICS_NOT_FOUND)).decreaseQuestionCount();
            userDepartmentJpaRepository.findByUserId(user.getId()).orElseThrow(() -> new BaseException(USER_DEPARTMENT_NOT_FOUND))
                    .forEach(userDepartment -> {
                        departmentStatisticsJpaRepository.findById(userDepartment.getDepartment().getId())
                                .orElseThrow(() -> new BaseException(DEPARTMENT_STATISTICS_NOT_FOUND)).decreaseQuestionCount();
                    });
        } else if(type == 3) {
            userStatisticsJpaRepository.findById(user.getId())
                    .orElseThrow(() -> new BaseException(USER_STATISTICS_NOT_FOUND)).decreaseTeamCount();
            userDepartmentJpaRepository.findByUserId(user.getId()).orElseThrow(() -> new BaseException(USER_DEPARTMENT_NOT_FOUND))
                    .forEach(userDepartment -> {
                        departmentStatisticsJpaRepository.findById(userDepartment.getDepartment().getId())
                                .orElseThrow(() -> new BaseException(DEPARTMENT_STATISTICS_NOT_FOUND)).decreaseTeamCount();
                    });
        } else if(type == 4) {
            userStatisticsJpaRepository.findById(user.getId())
                    .orElseThrow(() -> new BaseException(USER_STATISTICS_NOT_FOUND)).decreasePatentCount();
            userDepartmentJpaRepository.findByUserId(user.getId()).orElseThrow(() -> new BaseException(USER_DEPARTMENT_NOT_FOUND))
                    .forEach(userDepartment -> {
                        departmentStatisticsJpaRepository.findById(userDepartment.getDepartment().getId())
                                .orElseThrow(() -> new BaseException(DEPARTMENT_STATISTICS_NOT_FOUND)).decreasePatentCount();
                    });
        }
    }
}
