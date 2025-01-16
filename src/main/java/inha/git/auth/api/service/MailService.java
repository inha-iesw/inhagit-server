package inha.git.auth.api.service;

import inha.git.auth.api.controller.dto.request.EmailCheckRequest;
import inha.git.auth.api.controller.dto.request.EmailRequest;
import inha.git.auth.api.controller.dto.request.FindPasswordCheckRequest;
import inha.git.auth.api.controller.dto.request.FindPasswordRequest;

public interface MailService {
    String mailSend(EmailRequest emailRequest);
    String findPasswordMailSend(FindPasswordRequest findPasswordRequest);
    Boolean mailSendCheck(EmailCheckRequest emailCheckRequest);
    Boolean findPasswordMailSendCheck(FindPasswordCheckRequest findPasswordCheckRequest);
    void emailAuth(String email, String userPosition);





}
