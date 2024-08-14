package inha.git.banner.api.service;

import inha.git.banner.api.controller.dto.response.BannerResponse;
import inha.git.user.domain.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BannerService {
    String createBanner(User user, MultipartFile file);

    List<BannerResponse> getBanners();
}
