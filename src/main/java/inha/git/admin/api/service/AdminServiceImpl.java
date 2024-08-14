package inha.git.admin.api.service;

import inha.git.admin.api.controller.dto.response.SearchCompanyResponse;
import inha.git.admin.api.controller.dto.response.SearchProfessorResponse;
import inha.git.admin.api.controller.dto.response.SearchStudentResponse;
import inha.git.admin.api.controller.dto.response.SearchUserResponse;
import inha.git.admin.domain.repository.AdminQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static inha.git.common.Constant.CREATE_AT;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AdminServiceImpl implements AdminService{

    private final AdminQueryRepository adminQueryRepository;

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


}
