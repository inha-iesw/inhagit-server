package inha.git.image.api.service;


import inha.git.user.domain.User;
import inha.git.image.api.controller.dto.response.ImageResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    ImageResponse createImage(User user, MultipartFile image);
}
