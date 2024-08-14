package inha.git.banner.api.mapper;

import inha.git.banner.domain.Banner;
import inha.git.user.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BannerMapper {

    @Mapping(target = "imgPath", source = "bannerPath")
    @Mapping(target = "user", source = "user")
    Banner bannerPathToBanner(User user, String bannerPath);

}
