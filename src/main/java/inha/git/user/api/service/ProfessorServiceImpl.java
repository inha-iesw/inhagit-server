package inha.git.user.api.service;

import inha.git.admin.api.controller.dto.response.SearchStudentResponse;
import inha.git.auth.api.service.MailService;
import inha.git.department.domain.repository.DepartmentJpaRepository;
import inha.git.user.api.controller.dto.request.ProfessorSignupRequest;
import inha.git.user.api.controller.dto.response.ProfessorSignupResponse;
import inha.git.user.api.mapper.UserMapper;
import inha.git.user.domain.Professor;
import inha.git.user.domain.User;
import inha.git.user.domain.repository.ProfessorJpaRepository;
import inha.git.user.domain.repository.ProfessorQueryRepository;
import inha.git.user.domain.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static inha.git.common.Constant.*;


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
    private final EmailDomainService emailDomainService;
    private final ProfessorQueryRepository professorQueryRepository;

    /**
     * 교수 학생 조회
     *
     * @param search 검색어
     * @param page 페이지
     * @return 학생 목록
     */
    @Override
    public Page<SearchStudentResponse> getProfessorStudents(String search, Integer page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, CREATE_AT));
        return professorQueryRepository.searchStudents(search, pageable);
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
        emailDomainService.validateEmailDomain(professorSignupRequest.email(), PROFESSOR_TYPE);
        mailService.emailAuth(professorSignupRequest.email(), PROFESSOR_SIGN_UP_TYPE);
        User user = userMapper.professorSignupRequestToUser(professorSignupRequest);
        userMapper.mapDepartmentsToUser(user, professorSignupRequest.departmentIdList(), departmentRepository);
        user.setPassword(passwordEncoder.encode(professorSignupRequest.pw()));
        Professor professor = userMapper.professorSignupRequestToProfessor(professorSignupRequest);
        professor.setUser(user);
        professorJpaRepository.save(professor);
        log.info("교수 회원가입 성공 - 이메일: {}", professorSignupRequest.email());
        return userMapper.userToProfessorSignupResponse(userJpaRepository.save(user));
    }
}
