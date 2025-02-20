package inha.git.image.api.service;

import inha.git.image.domain.repository.ImageJpaRepository;
import inha.git.user.domain.User;
import inha.git.utils.file.FilePath;
import inha.git.image.api.controller.dto.response.ImageResponse;
import inha.git.image.api.mapper.ImageMapper;
import inha.git.image.domain.Image;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import static inha.git.common.Constant.IMAGE;

/**
 * ImageServiceImpl는 ImageService 인터페이스를 구현.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ImageServiceImpl implements ImageService {

    private final ImageMapper imageMapper;
    private final ImageJpaRepository imageJpaRepository;

    /**
     * 이미지 생성
     *
     * @param user 유저 정보
     * @param imageFile 이미지 파일
     * @return 생성된 이미지 정보
     */
    @Override
    public ImageResponse createImage(User user, MultipartFile imageFile) {
        Image image = imageMapper.imagePathToImage(FilePath.storeFile(imageFile, IMAGE));
        imageJpaRepository.save(image);
        log.info("이미지 생성 성공 - 유저: {}", user.getName());
        return imageMapper.imageToImageResponse(image);
    }
}
