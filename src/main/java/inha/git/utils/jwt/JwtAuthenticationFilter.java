package inha.git.utils.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import inha.git.common.code.ErrorReasonDTO;
import inha.git.common.exceptions.BaseException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static inha.git.common.Constant.HEADER_AUTHORIZATION;
import static inha.git.common.Constant.TOKEN_PREFIX;


/**
 * JwtAuthenticationFilter는 JWT 기반의 인증을 처리하는 필터.
 * 각 요청마다 실행되며, 토큰을 검증하고 사용자 정보를 설정.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        // /api/v1/auth 는 권한 필요 없는 api 이므로 바로 통과
        if (request.getServletPath().contains("/api/v1/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader(HEADER_AUTHORIZATION);
        final String jwt;
        final String username;

        // 헤더에 토큰이 없거나 Bearer 로 시작하지 않으면 필터 통과
        if (authHeader == null || !authHeader.startsWith(TOKEN_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);

        try {
            // JWT에서 username 추출
            username = jwtProvider.extractUsername(jwt);

            // 토큰이 유효하고 인증이 아직 이루어지지 않은 경우
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                // 토큰이 유효한지 확인
                if (jwtProvider.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (BaseException e) {
            // JWT 예외 발생 시 정의한 에러 메시지와 함께 JSON 응답 반환
            log.error("JWT 인증 실패: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");

            // ErrorReasonDTO를 JSON으로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            ErrorReasonDTO errorReasonDTO = e.getErrorReasonHttpStatus();

            // JSON으로 변환 후 응답으로 반환
            objectMapper.writeValue(response.getOutputStream(), errorReasonDTO);
            return; // 예외 발생 시 필터 체인 중단
        }

        // 필터 체인 계속 진행
        filterChain.doFilter(request, response);
    }
}