package inha.git.github.domain.repository;

import inha.git.github.domain.GithubToken;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * GithubTokenJpaRepository는 GithubToken 엔티티의 JPA 레포지토리이다.
 */
public interface GithubTokenJpaRepository extends JpaRepository<GithubToken, String> {
}
