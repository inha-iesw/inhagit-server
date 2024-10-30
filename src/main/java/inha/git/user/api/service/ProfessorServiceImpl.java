package inha.git.user.api.service;

import inha.git.auth.api.service.MailService;
import inha.git.category.domain.Category;
import inha.git.category.domain.repository.CategoryJpaRepository;
import inha.git.department.domain.repository.DepartmentJpaRepository;
import inha.git.field.domain.Field;
import inha.git.field.domain.repository.FieldJpaRepository;
import inha.git.semester.domain.Semester;
import inha.git.semester.domain.repository.SemesterJpaRepository;
import inha.git.statistics.domain.UserStatistics;
import inha.git.statistics.domain.repository.UserStatisticsJpaRepository;
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

import java.util.List;

import static inha.git.common.BaseEntity.State.ACTIVE;
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
    private final SemesterJpaRepository semesterJpaRepository;
    private final CategoryJpaRepository categoryJpaRepository;
    private final FieldJpaRepository fieldJpaRepository;
    private final UserStatisticsJpaRepository userStatisticsJpaRepository;
    private final UserMapper userMapper;
    private final MailService mailService;
    private final EmailDomainService emailDomainService;
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

        List<Semester> semesters = semesterJpaRepository.findAllByState(ACTIVE);
        List<Field> fields = fieldJpaRepository.findAllByState(ACTIVE);
        List<Category> categories = categoryJpaRepository.findAllByState(ACTIVE);
        for (Semester semester : semesters) {
            for (Field field : fields) {
                for (Category category : categories) {
                    UserStatistics userStatistics = userMapper.createUserStatistics(user, semester, field, category);
                    userStatisticsJpaRepository.save(userStatistics);
                }
            }
        }
        log.info("교수 회원가입 성공 - 이메일: {}", professorSignupRequest.email());
        return userMapper.userToProfessorSignupResponse(userJpaRepository.save(user));
    }
}
