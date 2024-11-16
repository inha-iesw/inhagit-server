package inha.git.auth.api.service;


import inha.git.auth.api.controller.dto.request.ChangePasswordRequest;
import inha.git.auth.api.controller.dto.request.FindEmailRequest;
import inha.git.auth.api.controller.dto.request.LoginRequest;
import inha.git.auth.api.controller.dto.response.FindEmailResponse;
import inha.git.auth.api.controller.dto.response.LoginResponse;
import inha.git.user.api.controller.dto.response.UserResponse;

public interface AuthService {

    LoginResponse login(LoginRequest loginRequest);

    FindEmailResponse findEmail(FindEmailRequest findEmailRequest);

    UserResponse changePassword(ChangePasswordRequest changePasswordRequest);
}
