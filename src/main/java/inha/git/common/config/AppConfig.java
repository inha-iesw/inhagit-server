package inha.git.common.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import inha.git.statistics.api.service.PatentExcelService;
import inha.git.statistics.api.service.ProjectExcelService;
import inha.git.statistics.api.service.QuestionExcelService;
import inha.git.statistics.api.service.StatisticsExcelService;
import inha.git.statistics.domain.enums.ExcelType;
import inha.git.user.domain.repository.UserJpaRepository;
import inha.git.utils.ApplicationAuditAware;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;

import static inha.git.common.BaseEntity.State.ACTIVE;

@Configuration
@RequiredArgsConstructor
public class AppConfig {

  private final UserJpaRepository userJpaRepository;

  @Bean
  public UserDetailsService userDetailsService() {
    return email -> userJpaRepository.findByEmailAndState(email, ACTIVE)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
  }

  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService());
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
  }

  @Bean
  public AuditorAware<Integer> auditorAware() {
    return new ApplicationAuditAware();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  JPAQueryFactory jpaQueryFactory(EntityManager em) {
    return new JPAQueryFactory(em);
  }

  @Bean
  public Map<ExcelType, StatisticsExcelService> excelServices(
          ProjectExcelService projectExcelService,
          PatentExcelService patentExcelService,
          QuestionExcelService questionExcelService
  ) {
    return Map.of(
            ExcelType.PROJECT, projectExcelService,
            ExcelType.PATENT, patentExcelService,
            ExcelType.QUESTION, questionExcelService
    );
  }
}
