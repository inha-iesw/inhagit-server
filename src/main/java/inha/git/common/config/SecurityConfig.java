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
    private static final String[] WHITE_LIST_URL = {
            "/",
            "/api/v1/auth/**",
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
                        req.requestMatchers(WHITE_LIST_URL)
                                .permitAll()
                                // 관리자 전용 접근 설정
                                .requestMatchers(ADMIN_URL).hasRole(ADMIN.name())
                                .requestMatchers(GET, ADMIN_URL).hasAuthority(ADMIN_READ.name())
                                .requestMatchers(POST, ADMIN_URL).hasAuthority(ADMIN_CREATE.name())
                                .requestMatchers(PUT, ADMIN_URL).hasAuthority(ADMIN_UPDATE.name())
                                .requestMatchers(DELETE, ADMIN_URL).hasAuthority(ADMIN_DELETE.name())
                                // 조교 전용 접근 설정
                                .requestMatchers(ASSISTANT_URL).hasRole("ASSISTANT")
                                .requestMatchers(GET, ASSISTANT_URL).hasAuthority(ASSISTANT_READ.name())
                                .requestMatchers(POST, ASSISTANT_URL).hasAuthority(ASSISTANT_CREATE.name())
                                .requestMatchers(PUT, ASSISTANT_URL).hasAuthority(ASSISTANT_UPDATE.name())
                                .requestMatchers(DELETE, ASSISTANT_URL).hasAuthority(ASSISTANT_DELETE.name())
                                // 교수 전용 접근 설정
                                .requestMatchers(PROFESSOR_URL).hasRole("PROFESSOR")
                                .requestMatchers(GET, PROFESSOR_URL).hasAuthority(PROFESSOR_READ.name())
                                .requestMatchers(POST, PROFESSOR_URL).hasAuthority(PROFESSOR_CREATE.name())
                                .requestMatchers(PUT, PROFESSOR_URL).hasAuthority(PROFESSOR_UPDATE.name())
                                .requestMatchers(DELETE, PROFESSOR_URL).hasAuthority(PROFESSOR_DELETE.name())
                                // 기업 전용 접근 설정
                                .requestMatchers(COMPANY_URL).hasRole("COMPANY")
                                .requestMatchers(GET, COMPANY_URL).hasAuthority(COMPANY_READ.name())
                                .requestMatchers(POST, COMPANY_URL).hasAuthority(COMPANY_CREATE.name())
                                .requestMatchers(PUT, COMPANY_URL).hasAuthority(COMPANY_UPDATE.name())
                                .requestMatchers(DELETE, COMPANY_URL).hasAuthority(COMPANY_DELETE.name())
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
