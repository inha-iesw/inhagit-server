package inha.git.admin.api.service;

import inha.git.admin.api.controller.dto.request.*;
import inha.git.common.exceptions.BaseException;
import inha.git.user.domain.Company;
import inha.git.user.domain.Professor;
import inha.git.user.domain.User;
import inha.git.user.domain.enums.Role;
import inha.git.user.domain.repository.CompanyJpaRepository;
import inha.git.user.domain.repository.ProfessorJpaRepository;
import inha.git.user.domain.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static inha.git.common.BaseEntity.State.ACTIVE;
import static inha.git.common.code.status.ErrorStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AdminApproveServiceImpl implements AdminApproveService {

    private final UserJpaRepository userJpaRepository;
    private final CompanyJpaRepository companyJpaRepository;
    private final ProfessorJpaRepository professorJpaRepository;

    /**
     * 관리자 권한 부여
     *
     * @param adminPromotionRequest 관리자 권한 부여 요청
     * @return 성공 메시지
     */
    @Override
    public String promotion(AdminPromotionRequest adminPromotionRequest) {
        User user = getUser(adminPromotionRequest.userIdx());
        if(user.getRole() == Role.ADMIN) {
            throw new BaseException(ALREADY_ADMIN);
        }

        user.setRole(Role.ADMIN);
        return adminPromotionRequest.userIdx() + ": 관리자 권한 부여 완료";
    }

    /**
     * 관리자 권한 박탈
     *
     * @param adminDemotionRequest 관리자 권한 박탈 요청
     * @return 성공 메시지
     */
    @Override
    public String demotion(AdminDemotionRequest adminDemotionRequest) {
        User user = getUser(adminDemotionRequest.userIdx());
        if(user.getRole() != Role.ADMIN) {
            throw new BaseException(NOT_ADMIN);
        }
        if(professorJpaRepository.findByUserId(user.getId()).isPresent()) {
            user.setRole(Role.PROFESSOR);
        } else if(companyJpaRepository.findByUserId(user.getId()).isPresent()) {
            user.setRole(Role.COMPANY);
        } else {
            user.setRole(Role.USER);
        }
        return adminDemotionRequest.userIdx() + ": 관리자 권한 박탈 완료 -> " + user.getRole() + "로 변경 완료";
    }

    /**
     * 교수 승인
     *
     * @param professorAcceptRequest 교수 승인 요청
     * @return 성공 메시지
     */
    @Override
    public String acceptProfessor(ProfessorAcceptRequest professorAcceptRequest) {
        User user = getUser(professorAcceptRequest.userIdx());
        if(user.getRole() != Role.PROFESSOR) {
            throw new BaseException(NOT_PROFESSOR);
        }
        Professor professor = professorJpaRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BaseException(NOT_PROFESSOR));
        if(professor.getAcceptedAt() != null) {
            throw new BaseException(ALREADY_ACCEPTED_PROFESSOR);
        }
        professor.setAcceptedAt(LocalDateTime.now());
        professorJpaRepository.save(professor);
        return professorAcceptRequest.userIdx() + ": 교수 승인 완료";
    }

    @Override
    public String cancelProfessor(ProfessorCancelRequest professorCancelRequest) {
        User user = getUser(professorCancelRequest.userIdx());
        if(user.getRole() != Role.PROFESSOR) {
            throw new BaseException(NOT_PROFESSOR);
        }
        Professor professor = professorJpaRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BaseException(NOT_PROFESSOR));
        if(professor.getAcceptedAt() == null) {
            throw new BaseException(NOT_ACCEPTED_PROFESSOR);
        }
        professor.setAcceptedAt(null);
        professorJpaRepository.save(professor);
        return professorCancelRequest.userIdx() + ": 교수 승인 취소 완료";
    }

    /**
     * 기업 승인
     *
     * @param companyAcceptRequest 기업 승인 요청
     * @return 성공 메시지
     */
    @Override
    public String acceptCompany(CompanyAcceptRequest companyAcceptRequest) {
        User user = getUser(companyAcceptRequest.userIdx());
        if(user.getRole() != Role.COMPANY) {
            throw new BaseException(NOT_COMPANY);
        }
        Company company = companyJpaRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BaseException(NOT_COMPANY));
        if(company.getAcceptedAt() != null) {
            throw new BaseException(ALREADY_ACCEPTED_COMPANY);
        }
        company.setAcceptedAt(LocalDateTime.now());
        companyJpaRepository.save(company);
        return companyAcceptRequest.userIdx() + ": 기업 승인 완료";
    }

    @Override
    public String cancelCompany(CompanyCancelRequest companyCancelRequest) {
        User user = getUser(companyCancelRequest.userIdx());
        if(user.getRole() != Role.COMPANY) {
            throw new BaseException(NOT_COMPANY);
        }
        Company company = companyJpaRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BaseException(NOT_COMPANY));
        if(company.getAcceptedAt() == null) {
            throw new BaseException(NOT_ACCEPTED_COMPANY);
        }
        company.setAcceptedAt(null);
        companyJpaRepository.save(company);
        return companyCancelRequest.userIdx() + ": 기업 승인 취소 완료";
    }

    /**
     * 학생 승인
     *
     * @param assistantPromotionRequest 학생 승인 요청
     * @return 성공 메시지
     */
    @Override
    public String promotionStudent(AssistantPromotionRequest assistantPromotionRequest) {
        User user = getUser(assistantPromotionRequest.userIdx());
        if(user.getRole() != Role.USER) {
            throw new BaseException(NOT_STUDENT);
        }
        user.setRole(Role.ASSISTANT);
        return assistantPromotionRequest.userIdx() + ": 조교 승격 완료";
    }


    /**
     * 유저 조회
     *
     * @param userIdx 유저 인덱스
     * @return 유저
     */
    private User getUser(Integer userIdx) {
        return userJpaRepository.findByIdAndState(userIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_FIND_USER));
    }


}
