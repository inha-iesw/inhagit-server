package inha.git.auth.api.service;

import inha.git.auth.api.controller.dto.request.LoginRequest;
import inha.git.auth.api.controller.dto.response.LoginResponse;
import inha.git.auth.api.mapper.AuthMapper;
import inha.git.common.exceptions.BaseException;
import inha.git.user.domain.Company;
import inha.git.user.domain.Professor;
import inha.git.user.domain.User;
import inha.git.user.domain.enums.Role;
import inha.git.user.domain.repository.CompanyJpaRepository;
import inha.git.user.domain.repository.ProfessorJpaRepository;
import inha.git.user.domain.repository.UserJpaRepository;
import inha.git.utils.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static inha.git.common.BaseEntity.State.ACTIVE;
import static inha.git.common.code.status.ErrorStatus.*;


/**
 * AuthServiceImpl은 인증 관련 비즈니스 로직을 처리하는 서비스 클래스.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final UserJpaRepository userJpaRepository;
    private final AuthenticationManager authenticationManager;
    private final AuthMapper authMapper;
    private final JwtProvider jwtProvider;
    private final ProfessorJpaRepository professorJpaRepository;
    private final CompanyJpaRepository companyJpaRepository;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        User findUser = userJpaRepository.findByEmailAndState(loginRequest.email(), ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_FIND_USER));
        try{
            authenticationManager.authenticate
                    (new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password()));
        } catch (BadCredentialsException e) {
            throw new BaseException(FAILED_TO_LOGIN);
        }
        Role role = findUser.getRole();
        if(role == Role.PROFESSOR) {
            Professor professor = professorJpaRepository.findByUserId(findUser.getId())
                    .orElseThrow(() -> new BaseException(NOT_FIND_USER));
            if(professor.getAcceptedAt() == null) {
                throw new BaseException(NOT_APPROVED_USER);
            }
        }
        else if(role == Role.COMPANY) {
            Company company = companyJpaRepository.findByUserId(findUser.getId())
                    .orElseThrow(() -> new BaseException(NOT_FIND_USER));
            if(company.getAcceptedAt() == null)
                throw new BaseException(NOT_APPROVED_USER);
        }
        String accessToken = jwtProvider.generateToken(findUser);
        return authMapper.userToLoginResponse(findUser, accessToken);
    }
}

