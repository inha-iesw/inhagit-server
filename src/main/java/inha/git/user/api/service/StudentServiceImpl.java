package inha.git.user.api.service;

import inha.git.auth.api.service.MailService;
import inha.git.department.domain.repository.DepartmentJpaRepository;
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

import static inha.git.common.Constant.STUDENT_SIGN_UP_TYPE;
import static inha.git.common.Constant.STUDENT_TYPE;


/**
 * 학생 관련 비즈니스 로직을 처리하는 서비스 구현체입니다.
 * 학생 회원가입과 관련된 도메인 로직을 수행합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class StudentServiceImpl implements StudentService{

    private final UserJpaRepository userJpaRepository;
    private final PasswordEncoder passwordEncoder;
    private final DepartmentJpaRepository departmentRepository;
    private final UserMapper userMapper;
    private final MailService mailService;
    private final EmailDomainService emailDomainService;

    /**
     * 학생 회원가입을 처리합니다.
     *
     * @param studentSignupRequest 학생 회원가입 요청 정보 (이메일, 비밀번호, 이름, 학번, 학과 정보)
     * @return StudentSignupResponse 가입된 학생 정보를 포함한 응답
     * @throws BaseException 다음의 경우에 발생:
     *      - INVALID_EMAIL_DOMAIN: 유효하지 않은 이메일 도메인
     *      - EMAIL_AUTH_NOT_FOUND: 이메일 인증이 완료되지 않은 경우
     *      - DEPARTMENT_NOT_FOUND: 존재하지 않는 학과인 경우
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

        log.info("학생 회원가입 성공 - 이메일: {}", studentSignupRequest.email());
        return userMapper.userToStudentSignupResponse(savedUser);
    }
}
