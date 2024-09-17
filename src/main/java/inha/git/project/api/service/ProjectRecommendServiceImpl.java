package inha.git.project.api.service;


import inha.git.common.exceptions.BaseException;
import inha.git.mapping.domain.repository.FoundingRecommendJpaRepository;
import inha.git.mapping.domain.repository.ProjectLikeJpaRepository;
import inha.git.mapping.domain.repository.RegistrationRecommendJpaRepository;
import inha.git.project.api.controller.dto.request.RecommendRequest;
import inha.git.project.api.mapper.ProjectMapper;
import inha.git.project.domain.Project;
import inha.git.project.domain.repository.ProjectJpaRepository;
import inha.git.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static inha.git.common.BaseEntity.State.ACTIVE;
import static inha.git.common.code.status.ErrorStatus.*;

/**
 * ProjectRecommendServiceImpl은 프로젝트 추천 관련 비즈니스 로직을 처리.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProjectRecommendServiceImpl implements ProjectRecommendService{

    private final ProjectJpaRepository projectJpaRepository;
    private final ProjectMapper projectMapper;
    private final ProjectLikeJpaRepository projectLikeJpaRepository;
    private final FoundingRecommendJpaRepository foundingRecommendJpaRepository;
    private final RegistrationRecommendJpaRepository registrationRecommendJpaRepository;


    /**
     * 프로젝트 창업 추천
     *
     * @param user 로그인한 사용자 정보
     * @param recommendRequest 추천할 프로젝트 정보
     * @return 추천 성공 메시지
     */
    @Override
    public String createProjectFoundingRecommend(User user, RecommendRequest recommendRequest) {
        Project project = getProject(recommendRequest);
        validRecommend(project, user, foundingRecommendJpaRepository.existsByUserAndProject(user, project));
        foundingRecommendJpaRepository.save(projectMapper.createProjectFoundingRecommend(user, project));
        project.setFoundRecommendCount(project.getFoundingRecommendCount() + 1);
        log.info("프로젝트 창업 추천 - 사용자: {} 프로젝트 ID: {} 추천 개수: {}", user.getName(), recommendRequest.idx(), project.getFoundingRecommendCount());
        return recommendRequest.idx() + "번 프로젝트 창업 추천 완료";
    }

    /**
     * 프로젝트 좋아요 추천
     *
     * @param user 로그인한 사용자 정보
     * @param recommendRequest 좋아요할 프로젝트 정보
     * @return 좋아요 성공 메시지
     */
    @Override
    public String createProjectLike(User user, RecommendRequest recommendRequest) {
        Project project = getProject(recommendRequest);
        validRecommend(project, user, projectLikeJpaRepository.existsByUserAndProject(user, project));
        projectLikeJpaRepository.save(projectMapper.createProjectLike(user, project));
        project.setLikeCount(project.getLikeCount() + 1);
        log.info("프로젝트 좋아요 - 사용자: {} 프로젝트 ID: {} 좋아요 개수: {}", user.getName(), recommendRequest.idx(), project.getLikeCount());
        return recommendRequest.idx() + "번 프로젝트 좋아요 완료";
    }

    /**
     * 프로젝트 등록 추천
     *
     * @param user 로그인한 사용자 정보
     * @param recommendRequest 추천할 프로젝트 정보
     * @return 추천 성공 메시지
     */
    @Override
    public String createProjectRegistrationRecommend(User user, RecommendRequest recommendRequest) {
        Project project = getProject(recommendRequest);
        validRecommend(project, user, registrationRecommendJpaRepository.existsByUserAndProject(user, project));
        registrationRecommendJpaRepository.save(projectMapper.createProjectRegistrationRecommend(user, project));
        project.setRegistrationRecommendCount(project.getRegistrationRecommendCount() + 1);
        log.info("프로젝트 등록 추천 - 사용자: {} 프로젝트 ID: {} 추천 개수: {}", user.getName(), recommendRequest.idx(), project.getRegistrationRecommendCount());
        return recommendRequest.idx() + "번 프로젝트 등록 추천 완료";
    }

    /**
     * 프로젝트 창업 추천 취소
     *
     * @param user 로그인한 사용자 정보
     * @param recommendRequest 추천할 프로젝트 정보
     * @return 추천 취소 성공 메시지
     */
    @Override
    public String cancelProjectFoundingRecommend(User user, RecommendRequest recommendRequest) {
        Project project = getProject(recommendRequest);
        validRecommendCancel(project, user, foundingRecommendJpaRepository.existsByUserAndProject(user, project));
        foundingRecommendJpaRepository.deleteByUserAndProject(user, project);
        project.setFoundRecommendCount(project.getFoundingRecommendCount() - 1);
        log.info("프로젝트 창업 추천 취소 - 사용자: {} 프로젝트 ID: {} 추천 개수: {}", user.getName(), recommendRequest.idx(), project.getFoundingRecommendCount());
        return recommendRequest.idx() + "번 프로젝트 창업 추천 취소 완료";
    }

    /**
     * 프로젝트 좋아요 취소
     *
     * @param user 로그인한 사용자 정보
     * @param recommendRequest 좋아요할 프로젝트 정보
     * @return 좋아요 취소 성공 메시지
     */
    @Override
    public String cancelProjectLike(User user, RecommendRequest recommendRequest) {
        Project project = getProject(recommendRequest);
        validRecommendCancel(project, user, projectLikeJpaRepository.existsByUserAndProject(user, project));
        projectLikeJpaRepository.deleteByUserAndProject(user, project);
        project.setLikeCount(project.getLikeCount() - 1);
        log.info("프로젝트 좋아요 취소 - 사용자: {} 프로젝트 ID: {} 좋아요 개수: {}", user.getName(), recommendRequest.idx(), project.getLikeCount());
        return recommendRequest.idx() + "번 프로젝트 좋아요 취소 완료";
    }

    /**
     * 프로젝트 등록 추천 취소
     *
     * @param user 로그인한 사용자 정보
     * @param recommendRequest 추천할 프로젝트 정보
     * @return 추천 취소 성공 메시지
     */
    @Override
    public String cancelProjectRegistrationRecommend(User user, RecommendRequest recommendRequest) {
        Project project = getProject(recommendRequest);
        validRecommendCancel(project, user, registrationRecommendJpaRepository.existsByUserAndProject(user, project));
        registrationRecommendJpaRepository.deleteByUserAndProject(user, project);
        project.setRegistrationRecommendCount(project.getRegistrationRecommendCount() - 1);
        log.info("프로젝트 등록 추천 취소 - 사용자: {} 프로젝트 ID: {} 추천 개수: {}", user.getName(), recommendRequest.idx(), project.getRegistrationRecommendCount());
        return recommendRequest.idx() + "번 프로젝트 등록 추천 취소 완료";
    }




    /**
     * 추천할 프로젝트가 유효한지 확인
     *
     * @param project 프로젝트 정보
     * @param user 로그인한 사용자 정보
     * @param patentRecommendJpaRepository 특허 추천 레포지토리
     */
    private void validRecommend(Project project, User user, boolean patentRecommendJpaRepository) {
        if (project.getUser().getId().equals(user.getId())) {
            log.error("내 프로젝트는 추천할 수 없습니다. - 사용자: {} 프로젝트 ID: {}", user.getName(), project.getId());
            throw new BaseException(MY_PROJECT_RECOMMEND);
        }
        if (patentRecommendJpaRepository) {
            log.error("이미 추천한 프로젝트입니다. - 사용자: {} 프로젝트 ID: {}", user.getName(), project.getId());
            throw new BaseException(PROJECT_ALREADY_RECOMMEND);
        }
    }

    /**
     * 추천 취소할 프로젝트가 유효한지 확인
     *
     * @param project 프로젝트 정보
     * @param user 로그인한 사용자 정보
     * @param patentRecommendJpaRepository 특허 추천 레포지토리
     */
    private void validRecommendCancel(Project project, User user, boolean patentRecommendJpaRepository) {
        if (project.getUser().getId().equals(user.getId())) {
            log.error("내 프로젝트는 추천할 수 없습니다. - 사용자: {} 프로젝트 ID: {}", user.getName(), project.getId());
            throw new BaseException(MY_PROJECT_RECOMMEND);
        }
        if (!patentRecommendJpaRepository) {
            log.error("추천하지 않은 프로젝트입니다. - 사용자: {} 프로젝트 ID: {}", user.getName(), project.getId());
            throw new BaseException(PROJECT_NOT_RECOMMEND);
        }
    }
    /**
     * 추천할 프로젝트 정보 조회
     *
     * @param recommendRequest 추천할 프로젝트 정보
     * @return 추천할 프로젝트 정보
     */
    private Project getProject(RecommendRequest recommendRequest) {
        return projectJpaRepository.findByIdAndState(recommendRequest.idx(), ACTIVE)
                .orElseThrow(() -> new BaseException(PROJECT_NOT_FOUND));
    }



}
