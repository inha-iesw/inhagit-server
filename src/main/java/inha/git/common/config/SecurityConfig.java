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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

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
    private static final String PROFESSOR_URL = "/api/v1/professor/**";
    // 기업 전용 경로
    private static final String COMPANY_URL = "/api/v1/company/**";

    private static final String[] GET_ONLY_WHITE_LIST_URL = {
            "/prometheus/**",
            "/actuator/**",
            "/api/v1/departments",
            "/api/v1/colleges",
            "/api/v1/semesters",
            "/api/v1/fields",
            "/api/v1/banners",
            "/banner/**",
            "/evidence/**",
            "/problem-file/**",
            "/project/**",
            "project-zip/**",
            "/api/v1/questions",
            "/api/v1/notices",
            "/api/v1/teams/posts",
            "/api/v1/statistics/**",
            "/api/v1/statistics",
            "/api/v1/problems",
            "/api/v1/projects",
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
                .csrf(AbstractHttpConfigurer::disable)

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
