package inha.git.github.api.service;


import inha.git.common.exceptions.BaseException;
import inha.git.github.api.controller.dto.request.GitubTokenResquest;
import inha.git.github.api.controller.dto.response.GithubRepositoryResponse;
import inha.git.github.api.mapper.GithubMapper;
import inha.git.user.domain.User;
import inha.git.user.domain.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

import static inha.git.common.code.status.ErrorStatus.FAILED_TO_GET_GITHUB_REPOSITORIES;
import static inha.git.common.code.status.ErrorStatus.GITHUB_TOKEN_NOT_FOUND;

/**
 * Github 관련 비즈니스 로직을 처리합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GithubServiceImpl implements GithubService {
    private final UserJpaRepository userJpaRepository;
    private final GithubMapper githubMapper;

    /**
     * Github Token을 갱신합니다.
     *
     * @param user                사용자 정보
     * @param gitubTokenResquest Github Token 갱신 요청 정보
     * @return 갱신 성공 메시지
     */
    @Override
    @Transactional
    public String updateGithubToken(User user, GitubTokenResquest gitubTokenResquest) {
        user.setGithubToken(gitubTokenResquest.githubToken());
        userJpaRepository.save(user);
        return "Github Token이 갱신되었습니다.";
    }

    /**
     * 사용자의 Github 레포지토리 목록을 조회합니다.
     *
     * @param user 사용자 정보
     * @return Github 레포지토리 목록
     */
    public List<GithubRepositoryResponse> getGithubRepositories(User user) {
        String githubToken = user.getGithubToken();
        if (githubToken == null) {
            throw new BaseException(GITHUB_TOKEN_NOT_FOUND);
        }
        try {
            GitHub github = GitHub.connectUsingOAuth(githubToken);
            List<GHRepository> repositories = github.getMyself().listRepositories().toList();
            return repositories.stream()
                    .sorted(Comparator.comparing(GHRepository::getId).reversed()) // 아이디 최신순으로 정렬
                    .map(githubMapper::toDto)
                    .toList();
        } catch (IOException e) {
            throw new BaseException(FAILED_TO_GET_GITHUB_REPOSITORIES);
        }
    }
}
