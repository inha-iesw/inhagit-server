package inha.git.github.api.service;


import inha.git.github.api.controller.dto.request.GitubTokenResquest;
import inha.git.user.domain.User;
import inha.git.user.domain.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GithubServiceImpl implements GithubService {
    private final UserJpaRepository userJpaRepository;

    @Override
    @Transactional
    public String updateGithubToken(User user, GitubTokenResquest gitubTokenResquest) {
        user.setGithubToken(gitubTokenResquest.githubToken());
        userJpaRepository.save(user);
        return "Github Token이 갱신되었습니다.";
    }
}
