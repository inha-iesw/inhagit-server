package inha.git.auth.api.service;


import dentlix.server.auth.api.controller.dto.request.LoginRequest;
import dentlix.server.auth.api.controller.dto.request.SignupRequest;
import dentlix.server.auth.api.controller.dto.response.LoginResponse;
import dentlix.server.auth.api.controller.dto.response.RefreshResponse;
import dentlix.server.auth.api.controller.dto.response.SignupResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {


    String fastAPIHealth();

    SignupResponse signup(SignupRequest signupRequest);

    LoginResponse login(LoginRequest loginRequest);

    RefreshResponse refreshToken(HttpServletRequest request, HttpServletResponse response);
}
