package inha.git.admin.api.service;

import inha.git.admin.api.controller.dto.request.SearchReportCond;
import inha.git.admin.api.controller.dto.response.*;
import inha.git.admin.domain.repository.AdminQueryRepository;
import inha.git.bug_report.api.controller.dto.request.SearchBugReportCond;
import inha.git.bug_report.api.controller.dto.response.SearchBugReportsResponse;
import inha.git.common.exceptions.BaseException;
import inha.git.mapping.domain.UserDepartment;
import inha.git.mapping.domain.repository.UserDepartmentJpaRepository;
import inha.git.report.api.controller.dto.response.SearchReportResponse;
import inha.git.statistics.domain.Statistics;
import inha.git.statistics.domain.enums.StatisticsType;
import inha.git.statistics.domain.repository.StatisticsJpaRepository;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static inha.git.common.Constant.CREATE_AT;
import static inha.git.common.Constant.mapRoleToPosition;
import static inha.git.common.code.status.ErrorStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AdminSearchServiceImpl implements AdminSearchService {

    private final AdminQueryRepository adminQueryRepository;
    private final UserJpaRepository userJpaRepository;
    private final UserMapper userMapper;
    private final CompanyJpaRepository companyJpaRepository;
    private final UserDepartmentJpaRepository userDepartmentJpaRepository;
    private final StatisticsJpaRepository statisticsJpaRepository;

    /**
     * 관리자 사용자 조회
     *
     * @param search 검색어
     * @param page 페이지
     * @return 사용자 목록
     */
    @Override
    public Page<SearchUserResponse> getAdminUsers(String search, Integer page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, CREATE_AT));
        return adminQueryRepository.searchUsers(search, pageable);
    }

    /**
     * 관리자 학생 조회
     *
     * @param search 검색어
     * @param page 페이지
     * @return 학생 목록
     */
    @Override
    public Page<SearchStudentResponse> getAdminStudents(String search, Integer page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, CREATE_AT));
        return adminQueryRepository.searchStudents(search, pageable);
    }

    /**
     * 관리자 교수 조회
     *
     * @param search 검색어
     * @param page 페이지
     * @return 교수 목록
     */
    @Override
    public Page<SearchProfessorResponse> getAdminProfessors(String search, Integer page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, CREATE_AT));
        return adminQueryRepository.searchProfessors(search, pageable);
    }

    /**
     * 관리자 회사 조회
     *
     * @param search 검색어
     * @param page 페이지
     * @return 회사 목록
     */
    @Override
    public Page<SearchCompanyResponse> getAdminCompanies(String search, Integer page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, CREATE_AT));
        return adminQueryRepository.searchCompanies(search, pageable);
    }

    /**
     * 관리자 사용자 조회
     *
     * @param userIdx 사용자 인덱스
     * @return 사용자 정보
     */
    @Override
    public inha.git.user.api.controller.dto.response.SearchUserResponse getAdminUser(Integer userIdx) {
        User user = userJpaRepository.findById(userIdx)
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

            List<SearchDepartmentResponse> searchDepartmentResponses = userMapper.departmentsToSearchDepartmentResponses(userDepartmentJpaRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new BaseException(USER_STATISTICS_NOT_FOUND))
                    .stream()
                    .map(UserDepartment::getDepartment)
                    .toList());
            return userMapper.toSearchNonCompanyUserResponse(user, localProjectCount + githubProjectCount, totalQuestionCount, totalTeamCount, searchDepartmentResponses, position, user.getGithubToken() != null);
        }
    }

    /**
     * 관리자 신고 조회
     *
     * @param searchReportCond 신고 검색 조건
     * @param page 페이지
     * @return 신고 목록
     */
    @Override
    public Page<SearchReportResponse> getAdminReports(SearchReportCond searchReportCond, Integer page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, CREATE_AT));
        return adminQueryRepository.searchReports(searchReportCond, pageable);
    }

    /**
     * 관리자 버그 제보 조회
     *
     * @param searchBugReportCond 버그 제보 검색 조건
     * @param page 페이지
     * @return 버그 제보 목록
     */
    @Override
    public Page<SearchBugReportsResponse> getAdminBugReports(SearchBugReportCond searchBugReportCond, Integer page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, CREATE_AT));
        return adminQueryRepository.searchBugReports(searchBugReportCond, pageable);
    }
}
