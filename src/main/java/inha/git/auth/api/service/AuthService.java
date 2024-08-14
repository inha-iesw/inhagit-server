package inha.git.auth.api.service;


import inha.git.auth.api.controller.dto.request.LoginRequest;
import inha.git.auth.api.controller.dto.response.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest loginRequest);
}
