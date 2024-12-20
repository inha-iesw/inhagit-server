package inha.git.user.api.service;

import inha.git.auth.api.service.MailService;
import inha.git.common.exceptions.BaseException;
import inha.git.user.api.controller.dto.request.CompanySignupRequest;
import inha.git.user.api.controller.dto.response.CompanySignupResponse;
import inha.git.user.api.mapper.UserMapper;
import inha.git.user.domain.Company;
import inha.git.user.domain.User;
import inha.git.user.domain.repository.CompanyJpaRepository;
import inha.git.user.domain.repository.UserJpaRepository;
import inha.git.utils.file.FilePath;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import static inha.git.common.Constant.*;


/**
 * 기업 관련 비즈니스 로직을 처리하는 서비스 구현체입니다.
 * 기업 회원가입과 관련된 도메인 로직을 수행합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CompanyServiceImpl implements CompanyService{

    private final UserJpaRepository userJpaRepository;
    private final PasswordEncoder passwordEncoder;
    private final CompanyJpaRepository companyJpaRepository;
    private final UserMapper userMapper;
    private final MailService mailService;

    /**
     * 기업 회원가입을 처리합니다.
     *
     * <p>
     * 다음과 같은 절차로 회원가입을 진행합니다:
     * 1. 이메일 인증 확인
     * 2. 사용자 정보 생성
     * 3. 비밀번호 암호화
     * 4. 사용자 정보 저장
     * 5. 사업자등록증 파일 저장
     * 6. 기업 정보 생성 및 연관관계 설정
     * 7. 기업 정보 저장
     * </p>
     *
     * @param companySignupRequest 기업 회원가입 요청 정보 (이메일, 비밀번호, 이름, 회사명)
     * @param evidence 사업자등록증 파일
     * @return CompanySignupResponse 가입된 기업 정보를 포함한 응답
     * @throws BaseException 다음의 경우에 발생:
     *      - EMAIL_AUTH_NOT_FOUND: 이메일 인증이 완료되지 않은 경우
     *      - FILE_CONVERT & FILE_NOT_FOUND: 파일 업로드 실패한 경우
     */
    @Transactional
    @Override
    public CompanySignupResponse companySignup(CompanySignupRequest companySignupRequest, MultipartFile evidence) {
        mailService.emailAuth(companySignupRequest.email(), COMPANY_SIGN_UP_TYPE);
        User user = userMapper.companySignupRequestToUser(companySignupRequest);
        user.setPassword(passwordEncoder.encode(companySignupRequest.pw()));
        User savedUser = userJpaRepository.save(user);
        Company company = userMapper.companySignupRequestToCompany(companySignupRequest,  FilePath.storeFile(evidence, EVIDENCE));
        company.setUser(savedUser);
        companyJpaRepository.save(company);
        log.info("기업 회원가입 성공 - 이메일: {}", companySignupRequest.email());
        return userMapper.userToCompanySignupResponse(savedUser);
    }
}
