package inha.git.common.config;

import inha.git.common.exceptions.handler.CustomAccessDeniedHandler;
import inha.git.common.exceptions.handler.CustomAuthenticationEntryPoint;
import inha.git.utils.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

import static inha.git.user.domain.enums.Permission.*;
import static inha.git.user.domain.enums.Role.*;
import static io.lettuce.core.AclCategory.ADMIN;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {
    // 관리자 전용 경로
    private static final String ADMIN_URL = "/api/v1/admin/**";
    // 조교 전용 경로
    private static final String ASSISTANT_URL = "/api/v1/assistant/**";
    // 교수 전용 경로
    private static final String PROFESSOR_URL = "/api/v1/professors/**";
    // 기업 전용 경로
    private static final String COMPANY_URL = "/api/v1/company/**";

    private static final String[] GET_ONLY_WHITE_LIST_URL = {
            "/api/v1/notices",
            "/api/v1/notices/{noticeIdx}",
            "/api/v1/departments",
            "/api/v1/reports/reportTypes",
            "/api/v1/reports/reportReasons",
            "/api/v1/colleges",
            "/api/v1/colleges/{departmentIdx}",
            "/api/v1/semesters",
            "/api/v1/categories",
            "/api/v1/fields",
            "/api/v1/banners",
            "/api/v1/banner/**",
            "/api/v1/image/**",
            "/api/v1/evidence/**",
            "/api/v1/problem-file/**",
            "/api/v1/project/**",
            "/api/v1project-zip/**",
            "/api/v1/attachment/**",
            "/api/v1/questions",
            "/api/v1/questions/cond",
            "/api/v1/notices",
            "/api/v1/teams/posts",
            "/api/v1/statistics/**",
            "/api/v1/statistics",
            "/api/v1/problems",
            "/api/v1/projects",
            "/api/v1/projects/cond",
            "/api/v1/notices/{noticeIdx}",
            "/api/v1/searches",
            "/api/v1/searches/**",
    };
    private static final String[] WHITE_LIST_URL = {
            "/",
            "/api/v1/auth/**",
            "/api/v1/users/student",
            "/api/v1/users/professor",
            "/api/v1/users/company",
            "/api/v1/test/**",
            "/v2/api-docs",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/index.html",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui/**",
            "/webjars/**",
            "/swagger-ui.html",
            "/error/**"
    };
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final LogoutHandler logoutHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;



    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .requiresChannel(channel -> channel
                .requestMatchers(r -> r.getHeader("X-Forwarded-Proto") != null)
                .requiresSecure())
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::deny)
                        .xssProtection(xss -> xss.disable())
                        .contentSecurityPolicy(csp -> csp
                                .policyDirectives("default-src 'self'; " +
                                        "script-src 'self' 'unsafe-inline'; " +
                                        "style-src 'self' 'unsafe-inline'; " +
                                        "img-src 'self' data:; " +
                                        "font-src 'self' data:; " +
                                        "object-src 'none'"))
                )
                .authorizeHttpRequests(req ->
                        req.requestMatchers(WHITE_LIST_URL).permitAll().
                                requestMatchers(GET, GET_ONLY_WHITE_LIST_URL).permitAll()  // GET 요청만 허용
                                // 관리자 전용 접근 설정
                                .requestMatchers(ADMIN_URL).hasRole(ADMIN.name())
                                .requestMatchers(GET, ADMIN_URL).hasAuthority(ADMIN_READ.name())
                                .requestMatchers(POST, ADMIN_URL).hasAuthority(ADMIN_CREATE.name())
                                .requestMatchers(PUT, ADMIN_URL).hasAuthority(ADMIN_UPDATE.name())
                                .requestMatchers(DELETE, ADMIN_URL).hasAuthority(ADMIN_DELETE.name())
                                // 조교 전용 접근 설정
                                .requestMatchers(ASSISTANT_URL).hasAnyRole(ASSISTANT.name(), PROFESSOR.name(), ADMIN.name())
                                .requestMatchers(GET, ASSISTANT_URL).hasAnyAuthority(ASSISTANT_READ.name(), PROFESSOR_READ.name(), ADMIN_READ.name())
                                .requestMatchers(POST, ASSISTANT_URL).hasAnyAuthority(ASSISTANT_CREATE.name(), PROFESSOR_CREATE.name(), ADMIN_CREATE.name())
                                .requestMatchers(PUT, ASSISTANT_URL).hasAnyAuthority(ASSISTANT_UPDATE.name(), PROFESSOR_UPDATE.name(), ADMIN_UPDATE.name())
                                .requestMatchers(DELETE, ASSISTANT_URL).hasAnyAuthority(ASSISTANT_DELETE.name(), PROFESSOR_DELETE.name(), ADMIN_DELETE.name())
                                // 교수 전용 접근 설정
                                .requestMatchers(PROFESSOR_URL).hasAnyRole(PROFESSOR.name(), ADMIN.name())
                                .requestMatchers(GET, PROFESSOR_URL).hasAnyAuthority(PROFESSOR_READ.name(), ADMIN_READ.name())
                                .requestMatchers(POST, PROFESSOR_URL).hasAnyAuthority(PROFESSOR_CREATE.name(), ADMIN_CREATE.name())
                                .requestMatchers(PUT, PROFESSOR_URL).hasAnyAuthority(PROFESSOR_UPDATE.name(), ADMIN_UPDATE.name())
                                .requestMatchers(DELETE, PROFESSOR_URL).hasAnyAuthority(PROFESSOR_DELETE.name(), ADMIN_DELETE.name())
                                // 기업 전용 접근 설정
                                .requestMatchers(COMPANY_URL).hasAnyRole(COMPANY.name(), ADMIN.name())
                                .requestMatchers(GET, COMPANY_URL).hasAnyAuthority(COMPANY_READ.name(), ADMIN.name())
                                .requestMatchers(POST, COMPANY_URL).hasAnyAuthority(COMPANY_CREATE.name(), ADMIN.name())
                                .requestMatchers(PUT, COMPANY_URL).hasAnyAuthority(COMPANY_UPDATE.name(), ADMIN.name())
                                .requestMatchers(DELETE, COMPANY_URL).hasAnyAuthority(COMPANY_DELETE.name(), ADMIN.name())
                                .anyRequest()
                                .authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .authenticationProvider(authenticationProvider)
                .logout(logout ->
                        logout.logoutUrl("/api/v1/auth/logout")
                                .addLogoutHandler(logoutHandler)
                                .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext())

                )
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling
                                .authenticationEntryPoint(customAuthenticationEntryPoint)
                                .accessDeniedHandler(customAccessDeniedHandler)
                );
        return http.build();

    }
}
