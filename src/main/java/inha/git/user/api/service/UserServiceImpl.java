package inha.git.user.api.service;

import inha.git.user.api.controller.dto.response.StudentSignupResponse;
import inha.git.user.domain.repository.UserJpaRepository;
import inha.git.utils.RedisProvider;
import inha.git.utils.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserJpaRepository userJpaRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final RedisProvider redisProvider;
    private final JwtProvider jwtProvider;

    @Override
    public StudentSignupResponse studentSignup() {
        return null;
    }
}
