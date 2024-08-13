package inha.git.user.api.service;

import inha.git.common.exceptions.BaseException;
import inha.git.department.domain.repository.DepartmentJpaRepository;
import inha.git.user.api.controller.dto.request.StudentSignupRequest;
import inha.git.user.api.controller.dto.response.StudentSignupResponse;
import inha.git.user.api.mapper.UserMapper;
import inha.git.user.domain.User;
import inha.git.user.domain.repository.UserJpaRepository;
import inha.git.utils.RedisProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static inha.git.common.code.status.ErrorStatus.EMAIL_AUTH_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserJpaRepository userJpaRepository;
    private final PasswordEncoder passwordEncoder;
    private final DepartmentJpaRepository departmentRepository;
    private final RedisProvider redisProvider;
    private final UserMapper userMapper;

    /**
     * 학생 회원가입
     *
     * @param studentSignupRequest 학생 회원가입 요청 정보
     * @return 학생 회원가입 결과
     */
    @Transactional
    @Override
    public StudentSignupResponse studentSignup(StudentSignupRequest studentSignupRequest) {
        String verificationKey = "verification-" + studentSignupRequest.email() + "-1";
        String verificationStatus = redisProvider.getValueOps(verificationKey);
        if (verificationStatus == null || !verificationStatus.equals("1")) {
            throw new BaseException(EMAIL_AUTH_NOT_FOUND);
        }
        User user = userMapper.studentSignupRequestToUser(studentSignupRequest);
        userMapper.mapDepartmentsToUser(user, studentSignupRequest.departmentIdList(), departmentRepository);
        user.setPassword(passwordEncoder.encode(studentSignupRequest.pw()));
        return userMapper.userToStudentSignupResponse(userJpaRepository.save(user));
    }
}
