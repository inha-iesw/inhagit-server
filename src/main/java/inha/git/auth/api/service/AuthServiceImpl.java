package inha.git.auth.api.service;

import inha.git.auth.api.controller.dto.request.EmailCheckRequest;
import inha.git.auth.api.controller.dto.request.EmailRequest;
import inha.git.auth.api.mapper.AuthMapper;
import inha.git.common.exceptions.BaseException;
import inha.git.user.domain.repository.UserJpaRepository;
import inha.git.utils.RedisProvider;
import inha.git.utils.jwt.JwtProvider;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Random;

import static inha.git.common.code.status.ErrorStatus.EMAIL_SEND_FAIL;
import static inha.git.utils.jwt.JwtProvider.*;


/**
 * AuthServiceImpl은 인증 관련 비즈니스 로직을 처리하는 서비스 클래스.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final UserJpaRepository userJpaRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final AuthMapper authMapper;
    private final JwtProvider jwtProvider;
    private final RedisProvider redisProvider;
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String username;



    public String mailSend(EmailRequest emailRequest) {
        String oldAuthNum = redisProvider.getValueOps(emailRequest.email());
        if (oldAuthNum != null) {
            log.info("기존 인증번호 삭제 : {}", oldAuthNum);
            redisProvider.deleteValueOps(emailRequest.email());
        }
        int authNumber = makeRandomNumber();
        String emailContent = String.format(EMAIL_CONTENT, authNumber);
        postMailSend(username, emailRequest.email(), EMAIL_TITLE, emailContent, authNumber);
        return "이메일 전송 완료";
    }

    @Override
    public Boolean mailSendCheck(EmailCheckRequest emailCheckRequest) {
        return null;
    }

    public void postMailSend(String setFrom, String toMail, String title, String content, int authNumber) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
            helper.setFrom(setFrom);
            helper.setTo(toMail);
            helper.setSubject(title);
            helper.setText(content, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new BaseException(EMAIL_SEND_FAIL);
        }
        redisProvider.setDataExpire(toMail, Integer.toString(authNumber), 60*5L);
    }
    //임의의 6자리 양수를 반환
    private int makeRandomNumber() {
        Random r = new Random();
        return  r.ints(100000,999999)
                .findFirst()
                .getAsInt();
    }
}

