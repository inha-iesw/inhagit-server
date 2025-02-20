package inha.git.banner.api.mapper;

import inha.git.banner.api.controller.dto.response.BannerResponse;
import inha.git.banner.domain.Banner;
import inha.git.user.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * BannerMapper는 배너 정보를 변환하는 인터페이스.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BannerMapper {

    @Mapping(target = "idx", source = "id")
    @Mapping(target = "imgPath", source = "imgPath")
    BannerResponse bannerToBannerResponse(Banner banner);

    List<BannerResponse> bannersToBannerResponses(List<Banner> banners);

    @Mapping(target = "imgPath", source = "bannerPath")
    @Mapping(target = "user", source = "user")
    Banner bannerPathToBanner(User user, String bannerPath);
}
