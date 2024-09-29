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
import inha.git.utils.IdempotentProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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
    private final IdempotentProvider idempotentProvider;


    /**
     * 관리자 권한 부여
     *
     * @param adminPromotionRequest 관리자 권한 부여 요청
     * @return 성공 메시지
     */
    @Override
    public String promotion(User admin, AdminPromotionRequest adminPromotionRequest) {
        idempotentProvider.isValidIdempotent(List.of("adminPromotionRequest", adminPromotionRequest.userIdx().toString()));

        User user = getUser(adminPromotionRequest.userIdx());
        if(user.getRole() == Role.ADMIN) {
            log.error("이미 관리자입니다. - 관리자: {}, 승격할 유저: {}", user.getName(), adminPromotionRequest.userIdx());
            throw new BaseException(ALREADY_ADMIN);
        }
        user.setRole(Role.ADMIN);
        log.info("관리자로 승격 성공 - 관리자: {}, 승격할 유저: {}", admin.getName(), adminPromotionRequest.userIdx());
        return adminPromotionRequest.userIdx() + ": 관리자 권한 부여 완료";
    }

    /**
     * 관리자 권한 박탈
     *
     * @param adminDemotionRequest 관리자 권한 박탈 요청
     * @return 성공 메시지
     */
    @Override
    public String demotion(User admin, AdminDemotionRequest adminDemotionRequest) {
        idempotentProvider.isValidIdempotent(List.of("adminDemotionRequest", adminDemotionRequest.userIdx().toString()));

        User user = getUser(adminDemotionRequest.userIdx());
        if(user.getRole() != Role.ADMIN) {
            log.error("관리자가 아닙니다. - 관리자: {}, 박탈할 유저: {}", user.getName(), adminDemotionRequest.userIdx());
            throw new BaseException(NOT_ADMIN);
        }
        if(professorJpaRepository.findByUserId(user.getId()).isPresent()) {
            log.info("관리자 권한 교수로 박탈 성공 - 관리자: {}, 박탈할 유저: {}", admin.getName(), adminDemotionRequest.userIdx());
            user.setRole(Role.PROFESSOR);
        } else if(companyJpaRepository.findByUserId(user.getId()).isPresent()) {
            log.info("관리자 권한 기업으로 박탈 성공 - 관리자: {}, 박탈할 유저: {}", admin.getName(), adminDemotionRequest.userIdx());
            user.setRole(Role.COMPANY);
        } else {
            log.info("관리자 권한 학생으로 박탈 성공 - 관리자: {}, 박탈할 유저: {}", admin.getName(), adminDemotionRequest.userIdx());
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
    public String acceptProfessor(User admin, ProfessorAcceptRequest professorAcceptRequest) {
        idempotentProvider.isValidIdempotent(List.of("professorAcceptRequest", professorAcceptRequest.userIdx().toString()));

        User user = getUser(professorAcceptRequest.userIdx());
        if(user.getRole() != Role.PROFESSOR) {
            log.error("교수가 아닙니다. - 관리자: {}, 승인할 유저: {}", user.getName(), professorAcceptRequest.userIdx());
            throw new BaseException(NOT_PROFESSOR);
        }
        Professor professor = getProfessor(user);
        if(professor.getAcceptedAt() != null) {
            log.error("이미 승인된 교수입니다. - 관리자: {}, 승인할 유저: {}", user.getName(), professorAcceptRequest.userIdx());
            throw new BaseException(ALREADY_ACCEPTED_PROFESSOR);
        }
        professor.setAcceptedAt(LocalDateTime.now());
        professorJpaRepository.save(professor);
        log.info("교수 승인 성공 - 관리자: {}, 승인할 유저: {}", admin.getName(), professorAcceptRequest.userIdx());
        return professorAcceptRequest.userIdx() + ": 교수 승인 완료";
    }

    @Override
    public String cancelProfessor(User admin, ProfessorCancelRequest professorCancelRequest) {
        idempotentProvider.isValidIdempotent(List.of("professorCancelRequest", professorCancelRequest.userIdx().toString()));

        User user = getUser(professorCancelRequest.userIdx());
        validProfessor(professorCancelRequest, user);
        Professor professor = getProfessor(user);
        if(professor.getAcceptedAt() == null) {
            log.error("이미 승인 취소된 교수입니다. - 관리자: {}, 승인할 유저: {}", user.getName(), professorCancelRequest.userIdx());
            throw new BaseException(NOT_ACCEPTED_PROFESSOR);
        }
        professor.setAcceptedAt(null);
        professorJpaRepository.save(professor);
        log.info("교수 승인 취소 성공 - 관리자: {}, 승인할 유저: {}", admin.getName(), professorCancelRequest.userIdx());
        return professorCancelRequest.userIdx() + ": 교수 승인 취소 완료";
    }



    /**
     * 기업 승인
     *
     * @param companyAcceptRequest 기업 승인 요청
     * @return 성공 메시지
     */
    @Override
    public String acceptCompany(User admin, CompanyAcceptRequest companyAcceptRequest) {
        idempotentProvider.isValidIdempotent(List.of("companyAcceptRequest", companyAcceptRequest.userIdx().toString()));

        User user = getUser(companyAcceptRequest.userIdx());
        if(user.getRole() != Role.COMPANY) {
            log.info("기업이 아닙니다. - 관리자: {}, 승인할 유저: {}", user.getName(), companyAcceptRequest.userIdx());
            throw new BaseException(NOT_COMPANY);
        }
        Company company = companyJpaRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BaseException(NOT_COMPANY));
        if(company.getAcceptedAt() != null) {
            log.error("이미 승인된 기업입니다. - 관리자: {}, 승인할 유저: {}", user.getName(), companyAcceptRequest.userIdx());
            throw new BaseException(ALREADY_ACCEPTED_COMPANY);
        }
        company.setAcceptedAt(LocalDateTime.now());
        companyJpaRepository.save(company);
        log.info("기업 승인 성공 - 관리자: {}, 승인할 유저: {}", admin.getName(), companyAcceptRequest.userIdx());
        return companyAcceptRequest.userIdx() + ": 기업 승인 완료";
    }

    /**
     * 기업 승인 취소
     *
     * @param companyCancelRequest 기업 승인 취소 요청
     * @return 성공 메시지
     */
    @Override
    public String cancelCompany(User admin, CompanyCancelRequest companyCancelRequest) {

        idempotentProvider.isValidIdempotent(List.of("companyCancelRequest", companyCancelRequest.userIdx().toString()));

        User user = getUser(companyCancelRequest.userIdx());
        if(user.getRole() != Role.COMPANY) {
            log.error("기업이 아닙니다. - 관리자: {}, 승인할 유저: {}", user.getName(), companyCancelRequest.userIdx());
            throw new BaseException(NOT_COMPANY);
        }
        Company company = companyJpaRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BaseException(NOT_COMPANY));
        if(company.getAcceptedAt() == null) {
            log.error("이미 승인 취소된 기업입니다. - 관리자: {}, 승인할 유저: {}", user.getName(), companyCancelRequest.userIdx());
            throw new BaseException(NOT_ACCEPTED_COMPANY);
        }
        company.setAcceptedAt(null);
        companyJpaRepository.save(company);
        log.info("기업 승인 취소 성공 - 관리자: {}, 승인할 유저: {}", admin.getName(), companyCancelRequest.userIdx());
        return companyCancelRequest.userIdx() + ": 기업 승인 취소 완료";
    }

    /**
     * 학생 승인
     *
     * @param assistantPromotionRequest 학생 승인 요청
     * @return 성공 메시지
     */
    @Override
    public String promotionStudent(User admin, AssistantPromotionRequest assistantPromotionRequest) {
        idempotentProvider.isValidIdempotent(List.of("assistantPromotionRequest", assistantPromotionRequest.userIdx().toString()));

        User user = getUser(assistantPromotionRequest.userIdx());
        if(user.getRole() != Role.USER) {
            log.error("학생이 아닙니다. - 관리자: {}, 승인할 유저: {}", user.getName(), assistantPromotionRequest.userIdx());
            throw new BaseException(NOT_STUDENT);
        }
        user.setRole(Role.ASSISTANT);
        log.info("조교 승인 성공 - 관리자: {}, 승인할 유저: {}", admin.getName(), assistantPromotionRequest.userIdx());
        return assistantPromotionRequest.userIdx() + ": 조교 승격 완료";
    }

    /**
     * 조교 승격 취소
     *
     * @param assistantDemotionRequest 학생 승인 취소 요청
     * @return 성공 메시지
     */
    @Override
    public String demotionStudent(User admin, AssistantDemotionRequest assistantDemotionRequest) {
        idempotentProvider.isValidIdempotent(List.of("assistantDemotionRequest", assistantDemotionRequest.userIdx().toString()));

        User user = getUser(assistantDemotionRequest.userIdx());
        if(user.getRole() != Role.ASSISTANT) {
            log.error("조교가 아닙니다. - 관리자: {}, 승인할 유저: {}", user.getName(), assistantDemotionRequest.userIdx());
            throw new BaseException(NOT_ASSISTANT);
        }
        user.setRole(Role.USER);
        log.info("조교 승격 취소 성공 - 관리자: {}, 승인할 유저: {}", admin.getName(), assistantDemotionRequest.userIdx());
        return assistantDemotionRequest.userIdx() + ": 조교 승격 취소 완료";
    }

    /**
     * 유저 차단
     *
     * @param userBlockRequest 유저 차단 요청
     * @return 성공 메시지
     */
    @Override
    public String blockUser(User admin, UserBlockRequest userBlockRequest) {

        idempotentProvider.isValidIdempotent(List.of("userBlockRequest", userBlockRequest.userIdx().toString()));

        User user = getUser(userBlockRequest.userIdx());
        if(user.getRole() == Role.ADMIN) {
            log.error("관리자는 차단할 수 없습니다. - 관리자: {}, 차단할 유저: {}", user.getName(), userBlockRequest.userIdx());
            throw new BaseException(CANNOT_BLOCK_ADMIN);
        }
        if(user.getBlockedAt() != null) {
            log.error("이미 차단된 유저입니다. - 관리자: {}, 차단할 유저: {}", user.getName(), userBlockRequest.userIdx());
            throw new BaseException(ALREADY_BLOCKED_USER);
        }
        user.setBlockedAt(LocalDateTime.now());
        log.info("유저 차단 성공 - 관리자: {}, 차단할 유저: {}", admin.getName(), userBlockRequest.userIdx());
        return userBlockRequest.userIdx() + ": 유저 차단 완료";
    }

    /**
     * 유저 차단 해제
     *
     * @param userUnblockRequest 유저 차단 해제 요청
     * @return 성공 메시지
     */
    @Override
    public String unblockUser(User admin, UserUnblockRequest userUnblockRequest) {

        idempotentProvider.isValidIdempotent(List.of("userUnblockRequest", userUnblockRequest.userIdx().toString()));


        User user = getUser(userUnblockRequest.userIdx());
        if(user.getBlockedAt() == null) {
            log.error("이미 차단 해제된 유저입니다. - 관리자: {}, 차단할 유저: {}", user.getName(), userUnblockRequest.userIdx());
            throw new BaseException(NOT_BLOCKED_USER);
        }
        user.setBlockedAt(null);
        log.info("유저 차단 해제 성공 - 관리자: {}, 차단할 유저: {}", admin.getName(), userUnblockRequest.userIdx());
        return userUnblockRequest.userIdx() + ": 유저 차단 해제 완료";
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

    /**
     * 교수 조회
     *
     * @param user 유저
     * @return 교수
     */
    private Professor getProfessor(User user) {
        return professorJpaRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BaseException(NOT_PROFESSOR));
    }

    /**
     * 교수 유효성 검사
     *
     * @param professorCancelRequest 교수 승인 취소 요청
     * @param user 유저
     */
    private void validProfessor(ProfessorCancelRequest professorCancelRequest, User user) {
        if(user.getRole() != Role.PROFESSOR) {
            log.error("교수가 아닙니다. - 관리자: {}, 승인할 유저: {}", user.getName(), professorCancelRequest.userIdx());
            throw new BaseException(NOT_PROFESSOR);
        }
    }


}
