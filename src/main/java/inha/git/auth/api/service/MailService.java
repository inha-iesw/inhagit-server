package inha.git.auth.api.service;

import inha.git.auth.api.controller.dto.request.EmailCheckRequest;
import inha.git.auth.api.controller.dto.request.EmailRequest;

public interface MailService {

    String mailSend(EmailRequest emailRequest);

    Boolean mailSendCheck(EmailCheckRequest emailCheckRequest);
}
