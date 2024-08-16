package inha.git.project.api.service;


import inha.git.common.exceptions.BaseException;
import inha.git.mapping.domain.repository.FoundingRecommendJpaRepository;
import inha.git.mapping.domain.repository.PatentRecommendJpaRepository;
import inha.git.mapping.domain.repository.RegistrationRecommendJpaRepository;
import inha.git.project.api.controller.api.dto.request.RecommendRequest;
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
    private final PatentRecommendJpaRepository patentRecommendJpaRepository;
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
        return recommendRequest.idx() + "번 프로젝트 창업 추천 완료";
    }

    /**
     * 프로젝트 특허 추천
     *
     * @param user 로그인한 사용자 정보
     * @param recommendRequest 추천할 프로젝트 정보
     * @return 추천 성공 메시지
     */
    @Override
    public String createProjectPatentRecommend(User user, RecommendRequest recommendRequest) {
        Project project = getProject(recommendRequest);
        validRecommend(project, user, patentRecommendJpaRepository.existsByUserAndProject(user, project));
        patentRecommendJpaRepository.save(projectMapper.createProjectPatentRecommend(user, project));
        project.setPatentRecommendCount(project.getPatentRecommendCount() + 1);
        return recommendRequest.idx() + "번 프로젝트 특허 추천 완료";
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
        return recommendRequest.idx() + "번 프로젝트 창업 추천 취소 완료";
    }

    /**
     * 프로젝트 특허 추천 취소
     *
     * @param user 로그인한 사용자 정보
     * @param recommendRequest 추천할 프로젝트 정보
     * @return 추천 취소 성공 메시지
     */
    @Override
    public String cancelProjectPatentRecommend(User user, RecommendRequest recommendRequest) {
        Project project = getProject(recommendRequest);
        validRecommendCancel(project, user, patentRecommendJpaRepository.existsByUserAndProject(user, project));
        patentRecommendJpaRepository.deleteByUserAndProject(user, project);
        project.setPatentRecommendCount(project.getPatentRecommendCount() - 1);
        return recommendRequest.idx() + "번 프로젝트 특허 추천 취소 완료";
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
            throw new BaseException(MY_PROJECT_RECOMMEND);
        }
        if (patentRecommendJpaRepository) {
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
            throw new BaseException(MY_PROJECT_RECOMMEND);
        }
        if (!patentRecommendJpaRepository) {
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
