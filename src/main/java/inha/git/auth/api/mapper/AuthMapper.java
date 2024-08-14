package inha.git.auth.api.mapper;

import inha.git.auth.api.controller.dto.response.LoginResponse;
import inha.git.user.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
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


}
