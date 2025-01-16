package inha.git.auth.api.service;

import inha.git.auth.api.controller.dto.request.EmailCheckRequest;
import inha.git.auth.api.controller.dto.request.EmailRequest;
import inha.git.auth.api.controller.dto.request.FindPasswordCheckRequest;
import inha.git.auth.api.controller.dto.request.FindPasswordRequest;
import inha.git.common.exceptions.BaseException;
import inha.git.user.api.service.EmailDomainService;
import inha.git.user.domain.repository.UserJpaRepository;
import inha.git.utils.RedisProvider;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

import static inha.git.common.Constant.*;
import static inha.git.common.code.status.ErrorStatus.*;

/**
 * MailServiceImpl은 이메일 인증 관련 비즈니스 로직을 처리하는 서비스 클래스.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;
    private final EmailDomainService emailDomainService;
    private final UserJpaRepository userJpaRepository;
    private final RedisProvider redisProvider;

    @Value("${spring.mail.username}")
    private String username;

    /**
     * 이메일 인증번호를 발송합니다.
     * @param emailRequest 이메일 주소와 인증 타입을 포함한 요청
     * @return 이메일 전송 완료 메시지
     * @throws BaseException INVALID_EMAIL_DOMAIN: 유효하지 않은 이메일 도메인인 경우,
     *                      EMAIL_SEND_FAIL: 이메일 전송 실패한 경우
     */
    public String mailSend(EmailRequest emailRequest) {
        if(emailRequest.type() == 1 || emailRequest.type() == 3) {
            log.info("이메일 도메인 검증 : {}", emailRequest.email());
            emailDomainService.validateEmailDomain(emailRequest.email(), emailRequest.type());
        }
        String oldAuthNum = redisProvider.getValueOps(emailRequest.email() + "-" + emailRequest.type());
        if (oldAuthNum != null) {
            log.info("기존 인증번호 삭제 : {}", oldAuthNum);
            redisProvider.deleteValueOps(emailRequest.email()+ "-" + emailRequest.type());
        }
        int authNumber = makeRandomNumber();
        String emailContent = String.format(EMAIL_CONTENT, authNumber);
        postMailSend(username, emailRequest.email(), EMAIL_TITLE, emailContent, authNumber, emailRequest.type());
        log.info("{}이메일 전송 완료", emailRequest.email());
        return "이메일 전송 완료";
    }

    /**
     * 비밀번호 찾기를 위한 인증 이메일을 전송합니다.
     *
     * @param findPasswordRequest 비밀번호 찾기 이메일 전송 요청 정보
     * @return 이메일 전송 완료 메시지
     * @throws BaseException EMAIL_NOT_FOUND: 존재하지 않는 이메일인 경우
     *                      EMAIL_SEND_FAIL: 이메일 전송 실패한 경우
     */
    @Override
    public String findPasswordMailSend(FindPasswordRequest findPasswordRequest) {
        userJpaRepository.findByEmail(findPasswordRequest.email())
                .orElseThrow(() -> new BaseException(EMAIL_NOT_FOUND));
        String oldAuthNum = redisProvider.getValueOps(findPasswordRequest.email() + "-" + PASSWORD_TYPE);
        if (oldAuthNum != null) {
            log.info("기존 인증번호 삭제 : {}", oldAuthNum);
            redisProvider.deleteValueOps(findPasswordRequest.email() + "-" + PASSWORD_TYPE);
        }
        int authNumber = makeRandomNumber();
        String emailContent = String.format(FIND_PASSWORD_CONTENT, authNumber);
        postMailSend(username, findPasswordRequest.email(), FIND_PASSWORD_TITLE, emailContent, authNumber, PASSWORD_TYPE);
        log.info("{} 이메일 전송 완료", findPasswordRequest.email());
        return "이메일 전송 완료";
    }

    /**
     * 이메일 인증번호의 유효성을 검증합니다.
     *
     * @param emailCheckRequest 이메일 주소, 인증번호, 인증 타입을 포함한 요청
     * @return 인증 성공 여부
     * @throws BaseException EMAIL_AUTH_EXPIRED: 인증번호가 만료된 경우,
     *                      EMAIL_AUTH_NOT_MATCH: 인증번호가 일치하지 않는 경우,
     *                      INVALID_EMAIL_DOMAIN: 유효하지 않은 이메일 도메인인 경우
     */
    @Override
    public Boolean mailSendCheck(EmailCheckRequest emailCheckRequest) {
        if(emailCheckRequest.type() == 1 || emailCheckRequest.type() == 3) {
            log.info("이메일 도메인 검증 : {}", emailCheckRequest.email());
            emailDomainService.validateEmailDomain(emailCheckRequest.email(), emailCheckRequest.type());
        }
        String storedAuthNum = redisProvider.getValueOps(emailCheckRequest.email() + "-" + emailCheckRequest.type());
        if(storedAuthNum == null) {
            log.info("{} 이메일 인증 만료", emailCheckRequest.email());
            throw new BaseException(EMAIL_AUTH_EXPIRED);
        }
        if (storedAuthNum.equals(emailCheckRequest.number())) {
            log.info("{} 이메일 인증 성공", emailCheckRequest.email());
            redisProvider.setDataExpire("verification-"+ emailCheckRequest.email() + "-" + emailCheckRequest.type(), emailCheckRequest.type().toString(), 60*60L);
            return true;
        } else {
            log.info("{} 이메일 인증 실패", emailCheckRequest.email());
            throw new BaseException(EMAIL_AUTH_NOT_MATCH);
        }
    }

    /**
     * 비밀번호 찾기 이메일 인증번호를 검증합니다.
     *
     * @param findPasswordCheckRequest 비밀번호 찾기 인증번호 확인 요청 정보
     * @return 인증 성공 여부
     * @throws BaseException EMAIL_NOT_FOUND: 존재하지 않는 이메일인 경우
     *                      EMAIL_AUTH_EXPIRED: 인증번호가 만료된 경우
     *                      EMAIL_AUTH_NOT_MATCH: 인증번호가 일치하지 않는 경우
     */
    @Override
    public Boolean findPasswordMailSendCheck(FindPasswordCheckRequest findPasswordCheckRequest) {
        userJpaRepository.findByEmail(findPasswordCheckRequest.email())
                .orElseThrow(() -> new BaseException(EMAIL_NOT_FOUND));
        String storedAuthNum = redisProvider.getValueOps(findPasswordCheckRequest.email() + "-" + PASSWORD_TYPE);
        if(storedAuthNum == null) {
            log.info("{} 이메일 인증 만료", findPasswordCheckRequest.email());
            throw new BaseException(EMAIL_AUTH_EXPIRED);
        }

        if (storedAuthNum.equals(findPasswordCheckRequest.number())) {
            log.info("{} 이메일 인증 성공", findPasswordCheckRequest.email());
            redisProvider.setDataExpire("verification-"+ findPasswordCheckRequest.email() + "-" + PASSWORD_TYPE, PASSWORD_TYPE.toString(), 60*60L);
            return true;
        } else {
            log.info("{} 이메일 인증 실패", findPasswordCheckRequest.email());
            throw new BaseException(EMAIL_AUTH_NOT_MATCH);
        }
    }

    /**
     * 이메일을 전송합니다.
     *
     * @param setFrom
     * @param toMail
     * @param title
     * @param content
     * @param authNumber
     * @param type
     * @throws BaseException EMAIL_SEND_FAIL: 이메일 전송 실패
     */
    public void postMailSend(String setFrom, String toMail, String title, String content, int authNumber, Integer type) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
            helper.setFrom(setFrom);
            helper.setTo(toMail);
            helper.setSubject(title);
            helper.setText(content, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            log.error("이메일 전송 실패 - 수신자: {}", toMail);
            throw new BaseException(EMAIL_SEND_FAIL);
        }
        redisProvider.setDataExpire(toMail + "-" + type, Integer.toString(authNumber), 60*3L);
    }

    private int makeRandomNumber() {
        Random r = new Random();
        return  r.ints(100000,999999)
                .findFirst()
                .getAsInt();
    }

    /**
     * 이메일 인증을 처리합니다.
     *
     * @param email 이메일 주소
     * @param userPosition 사용자 포지션
     * @throws BaseException EMAIL_AUTH_NOT_FOUND: 이메일 인증 실패
     */
    public void emailAuth(String email, String userPosition) {
        String verificationKey = "verification-" + email + "-" + userPosition;
        String verificationStatus = redisProvider.getValueOps(verificationKey);
        if (verificationStatus == null || !verificationStatus.equals(userPosition)) {
            log.error("이메일 인증 실패 - 이메일: {}", email);
            throw new BaseException(EMAIL_AUTH_NOT_FOUND);
        }
    }
}
