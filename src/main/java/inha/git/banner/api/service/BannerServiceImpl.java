package inha.git.banner.api.service;

import inha.git.banner.api.mapper.BannerMapper;
import inha.git.banner.domain.Banner;
import inha.git.banner.domain.repository.BannerJpaRepository;
import inha.git.user.domain.User;
import inha.git.utils.FilePath;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * BannerServiceImpl은 BannerService 인터페이스를 구현하는 클래스.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BannerServiceImpl implements BannerService{

    private final BannerJpaRepository bannerJpaRepository;
    private final BannerMapper bannerMapper;

    @Override
    @Transactional
    public String createBanner(User user, MultipartFile file) {
        Banner banner = bannerMapper.bannerPathToBanner(user, FilePath.storeFile(file, "banner"));
        bannerJpaRepository.save(banner);
        return "배너가 등록되었습니다.";
    }
}
