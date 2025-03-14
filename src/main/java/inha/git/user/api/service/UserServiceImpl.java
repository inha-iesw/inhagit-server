package inha.git.user.api.service;

import inha.git.admin.api.controller.dto.response.SearchDepartmentResponse;
import inha.git.bug_report.api.controller.dto.request.SearchBugReportCond;
import inha.git.bug_report.api.controller.dto.response.SearchBugReportsResponse;
import inha.git.bug_report.domain.repository.BugReportQueryRepository;
import inha.git.common.exceptions.BaseException;
import inha.git.mapping.domain.UserDepartment;
import inha.git.mapping.domain.repository.UserDepartmentJpaRepository;
import inha.git.problem.api.controller.dto.response.SearchProblemsResponse;
import inha.git.problem.domain.repository.ProblemQueryRepository;
import inha.git.project.api.controller.dto.response.SearchProjectsResponse;
import inha.git.project.domain.repository.ProjectQueryRepository;
import inha.git.question.api.controller.dto.response.SearchQuestionsResponse;
import inha.git.question.domain.repository.QuestionQueryRepository;
import inha.git.report.api.controller.dto.response.SearchReportResponse;
import inha.git.report.domain.repository.ReportQueryRepository;
import inha.git.statistics.domain.Statistics;
import inha.git.statistics.domain.enums.StatisticsType;
import inha.git.statistics.domain.repository.StatisticsJpaRepository;
import inha.git.user.api.controller.dto.request.UpdatePwRequest;
import inha.git.user.api.controller.dto.response.SearchUserResponse;
import inha.git.user.api.controller.dto.response.UserResponse;
import inha.git.user.api.mapper.UserMapper;
import inha.git.user.domain.Company;
import inha.git.user.domain.User;
import inha.git.user.domain.enums.Role;
import inha.git.user.domain.repository.CompanyJpaRepository;
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

import java.util.List;

import static inha.git.common.BaseEntity.State.ACTIVE;
import static inha.git.common.Constant.CREATE_AT;
import static inha.git.common.Constant.mapRoleToPosition;
import static inha.git.common.code.status.ErrorStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final CompanyJpaRepository companyJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final UserMapper userMapper;
    private final UserDepartmentJpaRepository userDepartmentJpaRepository;
    private final StatisticsJpaRepository statisticsJpaRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProjectQueryRepository projectQueryRepository;
    private final QuestionQueryRepository questionQueryRepository;
    private final ProblemQueryRepository problemQueryRepository;
    private final ReportQueryRepository reportQueryRepository;
    private final BugReportQueryRepository bugReportQueryRepository;

    /**
     * 사용자 정보 조회
     *
     * @param userIdx 사용자 인덱스
     * @return 사용자 정보 조회 결과
     */
    @Override
    public SearchUserResponse getUser(Integer userIdx) {
        User user = userJpaRepository.findByIdAndState(userIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_FIND_USER));
        Integer position = mapRoleToPosition(user.getRole());
        if (user.getRole().equals(Role.COMPANY)) {
            Company company = companyJpaRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new BaseException(NOT_COMPANY));
            return userMapper.toSearchCompanyUserResponse(user, position, company);
        } else {
            List<Statistics> userStatistics = statisticsJpaRepository.findByStatisticsTypeAndTargetId(StatisticsType.USER, user.getId().longValue());

            int localProjectCount = userStatistics.stream().mapToInt(Statistics::getLocalProjectCount).sum();
            int githubProjectCount = userStatistics.stream().mapToInt(Statistics::getGithubProjectCount).sum();
            int totalQuestionCount = userStatistics.stream().mapToInt(Statistics::getQuestionCount).sum();
            int totalTeamCount = userStatistics.stream().mapToInt(Statistics::getProjectParticipationCount).sum();  // 팀 참여 수는 프로젝트 참여 수로 대체
            int totalPatentCount = userStatistics.stream().mapToInt(Statistics::getPatentCount).sum();

            List<SearchDepartmentResponse> searchDepartmentResponses = userMapper.departmentsToSearchDepartmentResponses(userDepartmentJpaRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new BaseException(USER_STATISTICS_NOT_FOUND))
                    .stream()
                    .map(UserDepartment::getDepartment)
                    .toList());
            return userMapper.toSearchNonCompanyUserResponse(user, localProjectCount + githubProjectCount, totalQuestionCount, totalTeamCount, totalPatentCount, searchDepartmentResponses, position, user.getGithubToken() != null);
        }
    }

    /**
     * 사용자 프로젝트 조회
     *
     * @param user 사용자 정보
     * @param page 페이지 번호
     * @return 사용자 프로젝트 조회 결과
     */
    @Override
    public Page<SearchProjectsResponse> getUserProjects(User user, Integer userIdx, Integer page) {
        User findUser = validUser(user, userIdx);
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, CREATE_AT));
        return projectQueryRepository.getUserProjects(findUser.getId(), pageable);
    }

    /**
     * 사용자 질문 조회
     *
     * @param user 사용자 정보
     * @param page 페이지 번호
     * @return 사용자 질문 조회 결과
     */
    @Override
    public Page<SearchQuestionsResponse> getUserQuestions(User user, Integer userIdx, Integer page) {
        User findUser = validUser(user, userIdx);
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, CREATE_AT));
        return questionQueryRepository.getUserQuestions(findUser.getId(), pageable);
    }

    /**
     * 사용자 문제 조회
     *
     * @param user 사용자 정보
     * @param pageIndex 페이지 번호
     * @return 사용자 문제 조회 결과
     */
    @Override
    public Page<SearchProblemsResponse> getUserProblems(User user, Integer userIdx, Integer pageIndex) {
        User findUser = validUser(user, userIdx);
        Pageable pageable = PageRequest.of(pageIndex, 10, Sort.by(Sort.Direction.DESC, CREATE_AT));
        return problemQueryRepository.getUserProblems(findUser.getId(), pageable);
    }

    /**
     * 사용자 문제 조회
     *
     * @param user 사용자 정보
     * @param page 페이지 번호
     * @return 사용자 문제 조회 결과
     */
    @Override
    public Page<SearchProblemsResponse> getUserProblemsParticipating(User user, Integer userIdx, Integer page) {
        User findUser = validUser(user, userIdx);
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        return problemQueryRepository.getUserProblemsParticipating(findUser.getId(), pageable);
    }

    /**
     * 사용자 신고 조회
     *
     * @param user 사용자 정보
     * @param page 페이지 번호
     * @return 사용자 신고 조회 결과
     */
    @Override
    public Page<SearchReportResponse> getUserReports(User user, Integer userIdx, Integer page) {
        User findUser = validUser(user, userIdx);
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, CREATE_AT));
        return reportQueryRepository.getUserReports(findUser.getId(), pageable);
    }

    /**
     * 사용자 버그 리포트 조회
     *
     * @param user 사용자 정보
     * @param searchBugReportCond 버그 리포트 검색 조건
     * @param page 페이지 번호
     * @return 사용자 버그 리포트 조회 결과
     */
    @Override
    public Page<SearchBugReportsResponse> getUserBugReports(User user, Integer userIdx, SearchBugReportCond searchBugReportCond, Integer page) {
        User findUser = validUser(user, userIdx);
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, CREATE_AT));
        return bugReportQueryRepository.getUserBugReports(findUser.getId(), searchBugReportCond, pageable);
    }

    /**
     * 비밀번호 변경
     *
     * @param id                사용자 인덱스
     * @param updatePwRequest 비밀번호 변경 요청
     * @return 사용자 정보 응답
     */
    @Override
    @Transactional
    public UserResponse changePassword(Integer id, UpdatePwRequest updatePwRequest) {
        User user = userJpaRepository.findByIdAndState(id, ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_FIND_USER));
        user.setPassword(passwordEncoder.encode(updatePwRequest.pw()));
        log.info("비밀번호 변경 성공 - 이메일: {}", user.getEmail());
        return userMapper.toUserResponse(user);
    }

    private User validUser(User user, Integer userIdx) {
        User findUser = userJpaRepository.findByIdAndState(userIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_FIND_USER));
        if(!user.getId().equals(findUser.getId()) && !user.getRole().equals(Role.ADMIN) && !user.getRole().equals(Role.PROFESSOR) && !user.getRole().equals(Role.ASSISTANT)){
            log.error("사용자 권한 없음 - 이메일: {}", user.getEmail());
            throw new BaseException(NOT_AUTHORIZED_USER);
        }
        return findUser;
    }
}
