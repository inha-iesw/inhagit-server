package inha.git.auth.api.mapper;

import inha.git.auth.api.controller.dto.response.FindEmailResponse;
import inha.git.auth.api.controller.dto.response.LoginResponse;
import inha.git.user.api.controller.dto.response.UserResponse;
import inha.git.user.domain.User;
import inha.git.utils.EmailMapperUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, imports = { EmailMapperUtil.class })
public interface AuthMapper {

    /**
     * User 엔티티를 LoginResponse로 변환
     *
     * @param user        회원 정보
     * @param accessToken  JWT Access Token
     * @return LoginResponse
     */
    @Mapping(source = "user.id", target = "userId")
    LoginResponse userToLoginResponse(User user, String accessToken);

    /**
     * User 엔티티를 FindEmailResponse로 변환
     *
     * @param user 회원 정보
     * @return FindEmailResponse
     */
    @Mapping(expression = "java(EmailMapperUtil.maskEmail(user.getEmail()))", target = "email")
    FindEmailResponse userToFindEmailResponse(User user);

    /**
     * User 엔티티를 UserResponse로 변환
     *
     * @param user 회원 정보
     * @return UserResponse
     */
    @Mapping(source = "user.id", target = "idx")
    UserResponse userToUserResponse(User user);
}
