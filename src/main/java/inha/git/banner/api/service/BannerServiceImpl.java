package inha.git.banner.api.service;

import inha.git.banner.api.controller.dto.request.CreateBannerRequest;
import inha.git.banner.api.controller.dto.response.BannerResponse;
import inha.git.banner.api.mapper.BannerMapper;
import inha.git.banner.domain.Banner;
import inha.git.banner.domain.repository.BannerJpaRepository;
import inha.git.user.domain.User;
import inha.git.utils.file.FilePath;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static inha.git.common.BaseEntity.State.ACTIVE;
import static inha.git.common.Constant.BANNER;

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

    /**
     * 배너 목록 조회
     *
     * @return 배너 목록
     */
    @Override
    public List<BannerResponse> getBanners() {
        return bannerMapper.bannersToBannerResponses(bannerJpaRepository.findAllByState(ACTIVE));
    }

    /**
     * 배너 생성
     *
     * @param user 유저 정보
     * @param createBannerRequest 배너 생성 요청 정보
     * @return 생성된 배너 ID
     */
    @Override
    @Transactional
    public String createBanner(User user, CreateBannerRequest createBannerRequest) {
        Banner banner = bannerMapper.bannerPathToBanner(user, FilePath.storeFile(createBannerRequest.file(), BANNER));
        bannerJpaRepository.save(banner);
        return "배너가 등록되었습니다.";
    }


}
