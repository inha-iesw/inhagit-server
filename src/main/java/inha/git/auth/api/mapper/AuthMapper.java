package inha.git.auth.api.mapper;

import inha.git.auth.api.controller.dto.request.SignupRequest;
import inha.git.auth.api.controller.dto.response.LoginResponse;
import inha.git.auth.api.controller.dto.response.SignupResponse;
import inha.git.user.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AuthMapper {

    /**
     * SignupRequest를 User 엔티티로 변환
     *
     * @param signupRequest 회원가입 요청 정보
     * @return User 엔티티
     */
    @Mapping(target = "role", constant = "USER")
    User signupRequestToUser(SignupRequest signupRequest);

    /**
     * User 엔티티를 SignupResponse로 변환
     *
     * @param user        회원 정보
     * @param accessToken  JWT Access Token
     * @param refreshToken JWT Refresh Token
     * @return SignupResponse
     */
    @Mapping(source = "user.id", target = "userId")
    SignupResponse userToSignupResponse(User user, String accessToken, String refreshToken);

    /**
     * User 엔티티를 LoginResponse로 변환
     *
     * @param user        회원 정보
     * @param accessToken  JWT Access Token
     * @param refreshToken JWT Refresh Token
     * @return LoginResponse
     */
    @Mapping(source = "user.id", target = "userId")
    LoginResponse userToLoginResponse(User user, String accessToken, String refreshToken);


}
