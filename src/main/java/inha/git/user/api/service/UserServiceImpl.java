package inha.git.user.api.service;

import inha.git.common.exceptions.BaseException;
import inha.git.department.domain.repository.DepartmentJpaRepository;
import inha.git.user.api.controller.dto.request.CompanySignupRequest;
import inha.git.user.api.controller.dto.request.ProfessorSignupRequest;
import inha.git.user.api.controller.dto.request.StudentSignupRequest;
import inha.git.user.api.controller.dto.response.CompanySignupResponse;
import inha.git.user.api.controller.dto.response.ProfessorSignupResponse;
import inha.git.user.api.controller.dto.response.StudentSignupResponse;
import inha.git.user.api.mapper.UserMapper;
import inha.git.user.domain.Company;
import inha.git.user.domain.User;
import inha.git.user.domain.repository.CompanyJpaRepository;
import inha.git.user.domain.repository.UserJpaRepository;
import inha.git.utils.FilePath;
import inha.git.utils.RedisProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import static inha.git.common.code.status.ErrorStatus.EMAIL_AUTH_NOT_FOUND;
import static inha.git.utils.jwt.JwtProvider.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserJpaRepository userJpaRepository;
    private final PasswordEncoder passwordEncoder;
    private final DepartmentJpaRepository departmentRepository;
    private final CompanyJpaRepository companyJpaRepository;
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
        emailAuth(studentSignupRequest.email(), STUDENT_TYPE);
        User user = userMapper.studentSignupRequestToUser(studentSignupRequest);
        userMapper.mapDepartmentsToUser(user, studentSignupRequest.departmentIdList(), departmentRepository);
        user.setPassword(passwordEncoder.encode(studentSignupRequest.pw()));
        return userMapper.userToStudentSignupResponse(userJpaRepository.save(user));
    }


    /**
     * 교수 회원가입
     *
     * @param professorSignupRequest 교수 회원가입 요청 정보
     * @return 교수 회원가입 결과
     */
    @Transactional
    @Override
    public ProfessorSignupResponse professorSignup(ProfessorSignupRequest professorSignupRequest) {
        emailAuth(professorSignupRequest.email(), PROFESSOR_TYPE);
        User user = userMapper.professorSignupRequestToUser(professorSignupRequest);
        userMapper.mapDepartmentsToUser(user, professorSignupRequest.departmentIdList(), departmentRepository);
        user.setPassword(passwordEncoder.encode(professorSignupRequest.pw()));
        return userMapper.userToProfessorSignupResponse(userJpaRepository.save(user));
    }

    @Transactional
    @Override
    public CompanySignupResponse companySignup(CompanySignupRequest companySignupRequest, MultipartFile evidence) {
        emailAuth(companySignupRequest.email(), COMPANY_TYPE);
        User user = userMapper.companySignupRequestToUser(companySignupRequest);
        user.setPassword(passwordEncoder.encode(companySignupRequest.pw()));
        User savedUser = userJpaRepository.save(user);
        Company company = userMapper.companySignupRequestToCompany(companySignupRequest,  FilePath.storeFile(evidence, "evidence"));
        company.setUser(savedUser);
        companyJpaRepository.save(company);
        return userMapper.userToCompanySignupResponse(savedUser);
    }


    private void emailAuth(String email, String userPosition) {
        String verificationKey = "verification-" + email + "-" + userPosition;
        String verificationStatus = redisProvider.getValueOps(verificationKey);
        if (verificationStatus == null || !verificationStatus.equals(userPosition)) {
            throw new BaseException(EMAIL_AUTH_NOT_FOUND);
        }
    }


}
