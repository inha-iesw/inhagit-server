package inha.git.user.api.service;

import inha.git.admin.api.controller.dto.response.SearchDepartmentResponse;
import inha.git.common.exceptions.BaseException;
import inha.git.mapping.domain.UserDepartment;
import inha.git.mapping.domain.repository.UserDepartmentJpaRepository;
import inha.git.problem.api.controller.dto.response.SearchProblemsResponse;
import inha.git.problem.domain.repository.ProblemQueryRepository;
import inha.git.project.api.controller.dto.response.SearchProjectsResponse;
import inha.git.project.domain.repository.ProjectQueryRepository;
import inha.git.question.api.controller.dto.response.SearchQuestionsResponse;
import inha.git.question.domain.repository.QuestionQueryRepository;
import inha.git.statistics.domain.UserStatistics;
import inha.git.statistics.domain.repository.UserStatisticsJpaRepository;
import inha.git.team.api.controller.dto.response.SearchMyTeamsResponse;
import inha.git.team.domain.repository.TeamQueryRepository;
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
    private final UserStatisticsJpaRepository userStatisticsJpaRepository;
    private final UserDepartmentJpaRepository userDepartmentJpaRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProjectQueryRepository projectQueryRepository;
    private final QuestionQueryRepository questionQueryRepository;
    private final TeamQueryRepository teamQueryRepository;
    private final ProblemQueryRepository problemQueryRepository;

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
            UserStatistics userStatistics = userStatisticsJpaRepository.findById(user.getId())
                    .orElseThrow(() -> new BaseException(USER_STATISTICS_NOT_FOUND));
            List<SearchDepartmentResponse> searchDepartmentResponses = userMapper.departmentsToSearchDepartmentResponses(userDepartmentJpaRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new BaseException(USER_STATISTICS_NOT_FOUND))
                    .stream()
                    .map(UserDepartment::getDepartment)
                    .toList());
            return userMapper.toSearchNonCompanyUserResponse(user, userStatistics, searchDepartmentResponses, position, user.getGithubToken() != null);
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
     * 사용자 팀 조회
     *
     * @param user 사용자 정보
     * @param page 페이지 번호
     * @return 사용자 팀 조회 결과
     */
    @Override
    public Page<SearchMyTeamsResponse> getUserTeams(User user, Integer userIdx, Integer page) {
        User findUser = validUser(user, userIdx);
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, CREATE_AT));
        return teamQueryRepository.getUserTeams(findUser.getId(), pageable);
    }

    /**
     * 사용자 문제 조회
     *
     * @param user 사용자 정보
     * @param page 페이지 번호
     * @return 사용자 문제 조회 결과
     */
    @Override
    public Page<SearchProblemsResponse> getUserProblems(User user, Integer userIdx, Integer page) {
        User findUser = validUser(user, userIdx);
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, CREATE_AT));
        return problemQueryRepository.getUserProblems(findUser.getId(), pageable);

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
        return userMapper.toUserResponse(user);
    }

    private User validUser(User user, Integer userIdx) {
        User findUser = userJpaRepository.findByIdAndState(userIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_FIND_USER));
        if(!user.getId().equals(findUser.getId()) && !user.getRole().equals(Role.ADMIN) && !user.getRole().equals(Role.PROFESSOR) && !user.getRole().equals(Role.ASSISTANT)){
            throw new BaseException(NOT_AUTHORIZED_USER);
        }
        return findUser;
    }

}
