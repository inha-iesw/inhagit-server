package inha.git.project.api.service;


import inha.git.common.exceptions.BaseException;
import inha.git.mapping.domain.repository.FoundingRecommendJpaRepository;
import inha.git.mapping.domain.repository.PatentRecommendJpaRepository;
import inha.git.mapping.domain.repository.RegistrationRecommendJpaRepository;
import inha.git.project.api.controller.api.request.RecommendRequest;
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
    @Override
    public String createProjectFoundingRecommend(User user, RecommendRequest recommendRequest) {
        Project project = projectJpaRepository.findByIdAndState(recommendRequest.idx(), ACTIVE)
                .orElseThrow(() -> new BaseException(PROJECT_NOT_FOUND));
        if(project.getUser().getId().equals(user.getId())) {
            throw new BaseException(MY_PROJECT_RECOMMEND);
        }

        if (foundingRecommendJpaRepository.existsByUserAndProject(user, project)) {
            throw new BaseException(PROJECT_ALREADY_RECOMMEND);
        }
        foundingRecommendJpaRepository.save(projectMapper.createProjectFoundingRecommend(user, project));
        project.setRecommendCount(project.getFoundingRecommendCount() + 1);
        return recommendRequest.idx() + "번 프로젝트 창업 추천 완료";
    }
}
