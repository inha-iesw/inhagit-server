package inha.git.banner.api.service;

import inha.git.banner.api.controller.dto.request.CreateBannerRequest;
import inha.git.banner.api.controller.dto.response.BannerResponse;
import inha.git.user.domain.User;

import java.util.List;

public interface BannerService {
    String createBanner(User user, CreateBannerRequest createBannerRequest);
    List<BannerResponse> getBanners();
}
