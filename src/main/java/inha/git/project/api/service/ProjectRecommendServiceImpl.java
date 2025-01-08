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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static inha.git.common.BaseEntity.State.ACTIVE;
import static inha.git.common.Constant.hasAccessToProject;
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
    @Transactional
    @Override
    public String createProjectFoundingRecommend(User user, RecommendRequest recommendRequest) {
        Project project = getProject(user, recommendRequest);
        try {
            if (!hasAccessToProject(project, user)) {
                throw new BaseException(PROJECT_NOT_PUBLIC);
            }
            validRecommend(project, user, foundingRecommendJpaRepository.existsByUserAndProject(user, project));
            foundingRecommendJpaRepository.save(projectMapper.createProjectFoundingRecommend(user, project));
            project.setFoundRecommendCount(project.getFoundingRecommendCount() + 1);
            log.info("프로젝트 창업 추천 성공 - 사용자: {} 프로젝트 ID: {} 추천 개수: {}", user.getName(), recommendRequest.idx(), project.getFoundingRecommendCount());
            return recommendRequest.idx() + "번 프로젝트 창업 추천 완료";
        } catch (DataIntegrityViolationException e) {
            log.error("프로젝트 창업 추천 중복 발생 - 사용자: {}, 프로젝트 ID: {}", user.getName(), recommendRequest.idx());
            throw new BaseException(ALREADY_RECOMMENDED);
        }
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
        Project project = getProject(user, recommendRequest);
        try {
            if (!hasAccessToProject(project, user)) {
                throw new BaseException(PROJECT_NOT_PUBLIC);
            }
            validLike(project, user, projectLikeJpaRepository.existsByUserAndProject(user, project));
            projectLikeJpaRepository.save(projectMapper.createProjectLike(user, project));
            project.setLikeCount(project.getLikeCount() + 1);
            log.info("프로젝트 좋아요 - 사용자: {} 프로젝트 ID: {} 좋아요 개수: {}", user.getName(), recommendRequest.idx(), project.getLikeCount());
            return recommendRequest.idx() + "번 프로젝트 창업 추천 완료";
        } catch (DataIntegrityViolationException e) {
            log.error("프로젝트 좋아요 추천 중복 발생 - 사용자: {}, 프로젝트 ID: {}", user.getName(), recommendRequest.idx());
            throw new BaseException(ALREADY_RECOMMENDED);
        }
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
        Project project = getProject(user, recommendRequest);
        try {
            if (!hasAccessToProject(project, user)) {
                throw new BaseException(PROJECT_NOT_PUBLIC);
            }
            validRecommend(project, user, registrationRecommendJpaRepository.existsByUserAndProject(user, project));
            registrationRecommendJpaRepository.save(projectMapper.createProjectRegistrationRecommend(user, project));
            project.setRegistrationRecommendCount(project.getRegistrationRecommendCount() + 1);
            log.info("프로젝트 등록 추천 - 사용자: {} 프로젝트 ID: {} 추천 개수: {}", user.getName(), recommendRequest.idx(), project.getRegistrationRecommendCount());
            return recommendRequest.idx() + "번 프로젝트 등록 추천 완료";
        } catch (DataIntegrityViolationException e) {
            log.error("프로젝트 등록 추천 중복 발생 - 사용자: {}, 프로젝트 ID: {}", user.getName(), recommendRequest.idx());
            throw new BaseException(ALREADY_RECOMMENDED);
        }
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
        Project project = getProject(user, recommendRequest);
        try {
            if (!hasAccessToProject(project, user)) {
                throw new BaseException(PROJECT_NOT_PUBLIC);
            }
            validRecommendCancel(project, user, foundingRecommendJpaRepository.existsByUserAndProject(user, project));
            foundingRecommendJpaRepository.deleteByUserAndProject(user, project);
            if (project.getFoundingRecommendCount() <= 0) {
                project.setFoundRecommendCount(0);
            }
            project.setFoundRecommendCount(project.getFoundingRecommendCount() - 1);
            log.info("프로젝트 창업 추천 취소 - 사용자: {} 프로젝트 ID: {} 추천 개수: {}", user.getName(), recommendRequest.idx(), project.getFoundingRecommendCount());
            return recommendRequest.idx() + "번 프로젝트 창업 추천 취소 완료";
        } catch (DataIntegrityViolationException e) {
            log.error("프로젝트 창업 추천 취소 중복 발생 - 사용자: {}, 프로젝트 ID: {}", user.getName(), recommendRequest.idx());
            throw new BaseException(ALREADY_RECOMMENDED);
        }
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
        Project project = getProject(user, recommendRequest);
        try {
            if (!hasAccessToProject(project, user)) {
                throw new BaseException(PROJECT_NOT_PUBLIC);
            }
            validLikeCancel(project, user, projectLikeJpaRepository.existsByUserAndProject(user, project));
            projectLikeJpaRepository.deleteByUserAndProject(user, project);
            if (project.getLikeCount() <= 0) {
                project.setLikeCount(0);
            }
            project.setLikeCount(project.getLikeCount() - 1);
            log.info("프로젝트 좋아요 취소 - 사용자: {} 프로젝트 ID: {} 좋아요 개수: {}", user.getName(), recommendRequest.idx(), project.getLikeCount());
            return recommendRequest.idx() + "번 프로젝트 좋아요 취소 완료";
        } catch (DataIntegrityViolationException e) {
            log.error("프로젝트 좋아요 추천 취소 중복 발생 - 사용자: {}, 프로젝트 ID: {}", user.getName(), recommendRequest.idx());
            throw new BaseException(ALREADY_RECOMMENDED);
        }
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
        Project project = getProject(user, recommendRequest);
        try {
            if (!hasAccessToProject(project, user)) {
                throw new BaseException(PROJECT_NOT_PUBLIC);
            }
            validRecommendCancel(project, user, registrationRecommendJpaRepository.existsByUserAndProject(user, project));
            registrationRecommendJpaRepository.deleteByUserAndProject(user, project);
            if (project.getRegistrationRecommendCount() <= 0) {
                project.setRegistrationRecommendCount(0);
            }
            project.setRegistrationRecommendCount(project.getRegistrationRecommendCount() - 1);
            log.info("프로젝트 등록 추천 취소 - 사용자: {} 프로젝트 ID: {} 추천 개수: {}", user.getName(), recommendRequest.idx(), project.getRegistrationRecommendCount());
            return recommendRequest.idx() + "번 프로젝트 등록 추천 취소 완료";
        } catch (DataIntegrityViolationException e) {
            log.error("프로젝트 등록 추천 취소 중복 발생 - 사용자: {}, 프로젝트 ID: {}", user.getName(), recommendRequest.idx());
            throw new BaseException(ALREADY_RECOMMENDED);
        }
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
     * 좋아요할 프로젝트가 유효한지 확인
     *
     * @param project 프로젝트 정보
     * @param user 로그인한 사용자 정보
     * @param patentRecommendJpaRepository 특허 추천 레포지토리
     */
    private void validLike(Project project, User user, boolean patentRecommendJpaRepository) {
        if (project.getUser().getId().equals(user.getId())) {
            log.error("내 프로젝트는 좋아요할 수 없습니다. - 사용자: {} 프로젝트 ID: {}", user.getName(), project.getId());
            throw new BaseException(MY_PROJECT_LIKE);
        }
        if (patentRecommendJpaRepository) {
            log.error("이미 좋아요한 프로젝트입니다.. - 사용자: {} 프로젝트 ID: {}", user.getName(), project.getId());
            throw new BaseException(PROJECT_ALREADY_LIKE);
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
     * 좋아요할 프로젝트가 유효한지 확인
     *
     * @param project 프로젝트 정보
     * @param user 로그인한 사용자 정보
     * @param patentRecommendJpaRepository 특허 추천 레포지토리
     */
    private void validLikeCancel(Project project, User user, boolean patentRecommendJpaRepository) {
        if (project.getUser().getId().equals(user.getId())) {
            log.error("내 프로젝트는 좋아요할 수 없습니다. - 사용자: {} 프로젝트 ID: {}", user.getName(), project.getId());
            throw new BaseException(MY_PROJECT_LIKE);
        }
        if (!patentRecommendJpaRepository) {
            log.error("좋아요하지 않은 프로젝트입니다. - 사용자: {} 프로젝트 ID: {}", user.getName(), project.getId());
            throw new BaseException(PROJECT_NOT_LIKE);
        }
    }

    /**
     * 프로젝트 정보 조회
     *
     * @param user 로그인한 사용자 정보
     * @param recommendRequest 추천할 프로젝트 정보
     * @return 프로젝트 정보
     */
    private Project getProject(User user, RecommendRequest recommendRequest) {
        Project project;
        try {
            project = projectJpaRepository.findByIdAndStateWithPessimisticLock(recommendRequest.idx(), ACTIVE)
                    .orElseThrow(() -> new BaseException(PROJECT_NOT_FOUND));
        } catch (PessimisticLockingFailureException e) {
            log.error("프로젝트 창업 추천 락 획득 실패 - 사용자: {}, 프로젝트 ID: {}",
                    user.getName(), recommendRequest.idx());
            throw new BaseException(TEMPORARY_UNAVAILABLE);
        }
        return project;
    }
}
