package inha.git.banner.api.service;

import inha.git.user.domain.User;
import org.springframework.web.multipart.MultipartFile;

public interface BannerService {
    String createBanner(User user, MultipartFile file);
}
