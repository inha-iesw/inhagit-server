package inha.git.user.api.service;

import inha.git.auth.api.service.MailService;
import inha.git.department.domain.repository.DepartmentJpaRepository;
import inha.git.field.domain.Field;
import inha.git.field.domain.repository.FieldJpaRepository;
import inha.git.semester.domain.Semester;
import inha.git.semester.domain.repository.SemesterJpaRepository;
import inha.git.statistics.domain.UserStatistics;
import inha.git.statistics.domain.repository.UserStatisticsJpaRepository;
import inha.git.user.api.controller.dto.request.StudentSignupRequest;
import inha.git.user.api.controller.dto.response.StudentSignupResponse;
import inha.git.user.api.mapper.UserMapper;
import inha.git.user.domain.User;
import inha.git.user.domain.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static inha.git.common.BaseEntity.State.ACTIVE;
import static inha.git.common.Constant.*;


/**
 * StudentServiceImpl은 학생 관련 비즈니스 로직을 처리하는 서비스 클래스.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class StudentServiceImpl implements StudentService{

    private final UserJpaRepository userJpaRepository;
    private final PasswordEncoder passwordEncoder;
    private final DepartmentJpaRepository departmentRepository;
    private final UserStatisticsJpaRepository userStatisticsJpaRepository;
    private final SemesterJpaRepository semesterJpaRepository;
    private final FieldJpaRepository fieldJpaRepository;
    private final UserMapper userMapper;
    private final MailService mailService;
    private final EmailDomainService emailDomainService;


    /**
     * 학생 회원가입
     *
     * @param studentSignupRequest 학생 회원가입 요청 정보
     * @return 학생 회원가입 결과
     */
    @Transactional
    @Override
    public StudentSignupResponse studentSignup(StudentSignupRequest studentSignupRequest) {
        emailDomainService.validateEmailDomain(studentSignupRequest.email(), STUDENT_TYPE);
        mailService.emailAuth(studentSignupRequest.email(), STUDENT_SIGN_UP_TYPE);
        User user = userMapper.studentSignupRequestToUser(studentSignupRequest);
        userMapper.mapDepartmentsToUser(user, studentSignupRequest.departmentIdList(), departmentRepository);
        user.setPassword(passwordEncoder.encode(studentSignupRequest.pw()));
        User savedUser = userJpaRepository.save(user);
        List<Semester> semesters = semesterJpaRepository.findAllByState(ACTIVE);
        List<Field> fields = fieldJpaRepository.findAllByState(ACTIVE);

        for (Semester semester : semesters) {
            for (Field field : fields) {
                UserStatistics userStatistics = userMapper.createUserStatistics(user, semester, field);
                userStatisticsJpaRepository.save(userStatistics);
            }
        }
        log.info("학생 회원가입 성공 - 이메일: {}", studentSignupRequest.email());
        return userMapper.userToStudentSignupResponse(savedUser);
    }
}
