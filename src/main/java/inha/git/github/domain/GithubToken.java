package inha.git.github.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

/**
 * GithubToken 엔티티는 애플리케이션의 Github API 토큰 정보를 나타냄.
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
@Builder
@Entity
@Table(name = "github_token_tb")
public class GithubToken {


    @Id
    private String token;
}
