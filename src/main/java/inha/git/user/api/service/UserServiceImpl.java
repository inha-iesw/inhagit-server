package inha.git.user.api.service;

import inha.git.department.domain.repository.DepartmentJpaRepository;
import inha.git.user.api.controller.dto.response.SearchUserResponse;
import inha.git.user.api.mapper.UserMapper;
import inha.git.user.domain.User;
import inha.git.user.domain.repository.CompanyJpaRepository;
import inha.git.user.domain.repository.UserJpaRepository;
import inha.git.utils.RedisProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final DepartmentJpaRepository departmentRepository;
    private final CompanyJpaRepository companyJpaRepository;
    private final RedisProvider redisProvider;
    private final UserMapper userMapper;


    @Override
    public SearchUserResponse getUser(User user) {
        return null;
    }
}
