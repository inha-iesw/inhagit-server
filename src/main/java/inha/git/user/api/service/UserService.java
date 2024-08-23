package inha.git.user.api.service;


import inha.git.user.api.controller.dto.response.SearchUserResponse;
import inha.git.user.domain.User;

public interface UserService {
    SearchUserResponse getUser(User user);
}
