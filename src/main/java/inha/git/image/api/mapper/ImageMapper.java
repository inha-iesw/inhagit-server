package inha.git.image.api.mapper;

import inha.git.image.domain.Image;
import inha.git.image.api.controller.dto.response.ImageResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * ImageMapper Image 엔티티와 관련된 데이터 변환 기능을 제공.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ImageMapper {

    /**
     * 이미지 URL을 Image 엔티티로 변환.
     * @param imageUrl 이미지 URL
     * @return Image
     */
    @Mapping(target = "id", ignore = true)
    Image imagePathToImage(String imageUrl);

    /**
     * Image 엔티티를 ImageResponse로 변환.
     * @param image Image 엔티티
     * @return ImageResponse
     */
    @Mapping(target = "idx", source = "id")
    @Mapping(target = "imageUrl", source = "imageUrl")
    ImageResponse imageToImageResponse(Image image);
}
