package inha.git.utils.jwt;

import inha.git.utils.RedisProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import static inha.git.common.Constant.HEADER_AUTHORIZATION;
import static inha.git.common.Constant.TOKEN_PREFIX;


/**
 * LogoutService는 사용자가 로그아웃할 때 JWT를 처리하는 서비스.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LogoutService implements LogoutHandler {

  private final JwtProvider jwtProvider;
  private final RedisProvider redisProvider;
  /**
   * 사용자가 로그아웃할 때 실행되는 메서드.
   *
   * @param request        HTTP 요청 객체
   * @param response       HTTP 응답 객체
   * @param authentication 인증 객체
   */
  @Override
  public void logout(
          HttpServletRequest request,
          HttpServletResponse response,
          Authentication authentication
  ) {
    final String authHeader = request.getHeader(HEADER_AUTHORIZATION);
    final String jwt;
    if (authHeader == null ||!authHeader.startsWith(TOKEN_PREFIX)) {
      return;
    }
    jwt = authHeader.substring(7);
    String username = jwtProvider.extractUsername(jwt);
    log.info("username: {}", username);
    redisProvider.deleteValueOps(username);
    SecurityContextHolder.clearContext();
  }
}
