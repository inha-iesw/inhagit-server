package inha.git.auth.api.service;

import inha.git.auth.api.controller.dto.request.EmailCheckRequest;
import inha.git.auth.api.controller.dto.request.EmailRequest;
import inha.git.auth.api.controller.dto.request.FindPasswordRequest;

public interface MailService {

    String mailSend(EmailRequest emailRequest);

    Boolean mailSendCheck(EmailCheckRequest emailCheckRequest);

    void emailAuth(String email, String userPosition);

    String findPasswordMailSend(FindPasswordRequest findPasswordRequest);
}
