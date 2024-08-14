package inha.git.user.api.service;

import inha.git.auth.api.service.MailService;
import inha.git.department.domain.repository.DepartmentJpaRepository;
import inha.git.user.api.controller.dto.request.ProfessorSignupRequest;
import inha.git.user.api.controller.dto.response.ProfessorSignupResponse;
import inha.git.user.api.mapper.UserMapper;
import inha.git.user.domain.Professor;
import inha.git.user.domain.User;
import inha.git.user.domain.repository.ProfessorJpaRepository;
import inha.git.user.domain.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static inha.git.common.Constant.SIGN_UP_TYPE;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProfessorServiceImpl implements ProfessorService{

    private final UserJpaRepository userJpaRepository;
    private final PasswordEncoder passwordEncoder;
    private final DepartmentJpaRepository departmentRepository;
    private final ProfessorJpaRepository professorJpaRepository;
    private final UserMapper userMapper;
    private final MailService mailService;
    /**
     * 교수 회원가입
     *
     * @param professorSignupRequest 교수 회원가입 요청 정보
     * @return 교수 회원가입 결과
     */
    @Transactional
    @Override
    public ProfessorSignupResponse professorSignup(ProfessorSignupRequest professorSignupRequest) {
        mailService.emailAuth(professorSignupRequest.email(), SIGN_UP_TYPE);
        User user = userMapper.professorSignupRequestToUser(professorSignupRequest);
        userMapper.mapDepartmentsToUser(user, professorSignupRequest.departmentIdList(), departmentRepository);
        user.setPassword(passwordEncoder.encode(professorSignupRequest.pw()));
        Professor professor = userMapper.professorSignupRequestToProfessor(professorSignupRequest);
        professor.setUser(user);
        professorJpaRepository.save(professor);
        return userMapper.userToProfessorSignupResponse(userJpaRepository.save(user));
    }
}
