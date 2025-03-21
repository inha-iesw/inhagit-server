package inha.git.utils.jwt;

import inha.git.common.code.status.ErrorStatus;
import inha.git.common.exceptions.BaseException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JwtProvider는 JWT 토큰의 생성 및 검증을 담당하는 클래스.
 */
@Service
@Slf4j
public class JwtProvider {

  @Value("${jwt.secret_key}")
  private String secretKey;
  @Value("${jwt.expiration}")
  private Long accessExpiration;
  @Value("${jwt.refresh-token.expiration}")
  private Long refreshExpiration;
  @Value("${jwt.issuer}")
  private String issuer;

  /**
   * JWT 토큰에서 사용자 이름을 추출.
   *
   * @param token JWT 토큰
   * @return 사용자 이름
   */
  public String extractUsername(String token) {
    try {
      return extractClaim(token, Claims::getSubject);
    } catch (io.jsonwebtoken.ExpiredJwtException e) {
      log.error("JWT 토큰이 만료되었습니다.");
      throw new BaseException(ErrorStatus.INVALID_JWT_EXPIRED); // 만료된 토큰
    } catch (io.jsonwebtoken.security.SecurityException e) {
      log.error("JWT 서명이 유효하지 않습니다.");
      throw new BaseException(ErrorStatus.INVALID_JWT_SIGNATURE); // 서명 오류
    } catch (Exception e) {
      log.error("JWT 토큰이 유효하지 않습니다.");
      throw new BaseException(ErrorStatus.INVALID_JWT); // 기타 오류
    }
  }

  /**
   * 토큰에서 특정 클레임을 추출.
   *
   * @param token JWT 토큰
   * @param claimsResolver 클레임 추출 함수
   * @param <T> 클레임 타입
   * @return 추출된 클레임
   */
  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  /**
   * 사용자 정보를 바탕으로 JWT 토큰을 생성.
   *
   * @param userDetails 사용자 정보
   * @return 생성된 JWT 토큰
   */
  public String generateToken(UserDetails userDetails) {
    return generateToken(new HashMap<>(), userDetails);
  }

  /**
   * 주어진 클레임과 사용자 정보를 바탕으로 JWT 토큰을 생성합.
   *
   * @param extraClaims 추가 클레임
   * @param userDetails 사용자 정보
   * @return 생성된 JWT 토큰
   */
  public String generateToken(
          Map<String, Object> extraClaims,
          UserDetails userDetails
  ) {
    return buildToken(extraClaims, userDetails, accessExpiration);
  }

  private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
    return Jwts
            .builder()
            .setClaims(extraClaims)
            .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
            .setIssuer(issuer)
            .setSubject(userDetails.getUsername())
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(getSignInKey(), SignatureAlgorithm.HS256)
            .compact();
  }

  /**
   * 토큰이 유효한지 확인.
   *
   * @param token JWT 토큰
   * @param userDetails 사용자 정보
   * @return 토큰이 유효한지 여부
   */
  public boolean isTokenValid(String token, UserDetails userDetails) {
    try {
      final String username = extractUsername(token);
      return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    } catch (io.jsonwebtoken.ExpiredJwtException e) {
      log.error("JWT 토큰이 만료되었습니다.");
      throw new BaseException(ErrorStatus.INVALID_JWT_EXPIRED); // 만료된 토큰
    } catch (io.jsonwebtoken.security.SecurityException e) {
      log.error("JWT 서명이 유효하지 않습니다.");
      throw new BaseException(ErrorStatus.INVALID_JWT_SIGNATURE); // 서명 오류
    } catch (Exception e) {
      log.error("JWT 토큰이 유효하지 않습니다.");
      throw new BaseException(ErrorStatus.INVALID_JWT); // 기타 오류
    }
  }

  /**
   * 사용자 정보를 바탕으로 리프레시 토큰을 생성.
   *
   * @param userDetails 사용자 정보
   * @return 생성된 리프레시 토큰
   */
  public String generateRefreshToken(UserDetails userDetails) {
    return buildToken(new HashMap<>(), userDetails, refreshExpiration);
  }

  /**
   * 토큰 발급 시간을 반환.
   *
   * @param token JWT 토큰
   * @return 발급 시간 (밀리초)
   */
  public Long getIssuedAt (String token) {
    Claims claims = getClaims(token);
    return claims.getIssuedAt().getTime();
  }

  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  private Claims extractAllClaims(String token) {
    return Jwts
            .parserBuilder()
            .setSigningKey(getSignInKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
  }

  private Key getSignInKey() {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  private Claims getClaims(String token) {
    return Jwts.parser()
            .setSigningKey(secretKey)
            .parseClaimsJws(token)
            .getBody();
  }
}
