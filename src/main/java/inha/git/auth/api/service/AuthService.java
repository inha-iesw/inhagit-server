package inha.git.auth.api.service;


import inha.git.auth.api.controller.dto.request.FindEmailRequest;
import inha.git.auth.api.controller.dto.request.LoginRequest;
import inha.git.auth.api.controller.dto.response.FindEmailResponse;
import inha.git.auth.api.controller.dto.response.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest loginRequest);

    FindEmailResponse findEmail(FindEmailRequest findEmailRequest);
}
