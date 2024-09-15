package inha.git.user.api.service;

import inha.git.auth.api.service.MailService;
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
     * 기업 회원가입
     *
     * @param companySignupRequest 기업 회원가입 요청 정보
     * @param evidence 기업 등록증
     * @return 기업 회원가입 결과
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
        return userMapper.userToCompanySignupResponse(savedUser);
    }
}
