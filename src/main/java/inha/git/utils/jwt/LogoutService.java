package inha.git.utils.jwt;

import inha.git.utils.RedisProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;
import inha.git.common.exceptions.BaseException;

import static inha.git.common.Constant.HEADER_AUTHORIZATION;
import static inha.git.common.Constant.TOKEN_PREFIX;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogoutService implements LogoutHandler {
  private final JwtProvider jwtProvider;
  private final RedisProvider redisProvider;

  @Value("${jwt.expiration}")
  private Long expiration;

  @Override
  public void logout(
          HttpServletRequest request,
          HttpServletResponse response,
          Authentication authentication
  ) {
    final String authHeader = request.getHeader(HEADER_AUTHORIZATION);

    if (authHeader == null || !authHeader.startsWith(TOKEN_PREFIX)) {
      return;
    }

    try {
      final String jwt = authHeader.substring(7);
      String username = jwtProvider.extractUsername(jwt);

      // Redis에서 사용자 정보 삭제
      redisProvider.deleteValueOps(username);
      // 토큰 블랙리스트 처리
      redisProvider.setDataExpire(jwt, jwt, expiration);

      log.info("사용자 {} 로그아웃 성공", username);

    } catch (BaseException e) {
      // 토큰이 만료된 경우
      log.info("만료된 토큰으로 로그아웃 시도");
    } catch (Exception e) {
      // 기타 예외 발생 시
      log.error("로그아웃 처리 중 오류 발생", e);
    } finally {
      // 항상 SecurityContext를 클리어
      SecurityContextHolder.clearContext();
    }
  }
}