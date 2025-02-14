package inha.git.project.api.service.patent;

import inha.git.common.exceptions.BaseException;
import inha.git.field.domain.Field;
import inha.git.mapping.domain.ProjectField;
import inha.git.project.api.controller.dto.request.CreatePatentInventorRequest;
import inha.git.project.api.controller.dto.request.CreatePatentRequest;
import inha.git.project.api.controller.dto.request.UpdatePatentInventorRequest;
import inha.git.project.api.controller.dto.request.UpdatePatentRequest;
import inha.git.project.api.controller.dto.response.PatentResponse;
import inha.git.project.api.controller.dto.response.SearchPatentResponse;
import inha.git.project.api.mapper.ProjectMapper;
import inha.git.project.domain.Project;
import inha.git.project.domain.ProjectPatent;
import inha.git.project.domain.ProjectPatentInventor;
import inha.git.project.domain.enums.PatentType;
import inha.git.project.domain.repository.ProjectJpaRepository;
import inha.git.project.domain.repository.ProjectPatentInventorJpaRepository;
import inha.git.project.domain.repository.ProjectPatentJpaRepository;
import inha.git.statistics.api.service.StatisticsService;
import inha.git.user.domain.User;
import inha.git.user.domain.enums.Role;
import inha.git.utils.file.FilePath;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static inha.git.common.BaseEntity.State.ACTIVE;
import static inha.git.common.BaseEntity.State.INACTIVE;
import static inha.git.common.Constant.PATENT;
import static inha.git.common.code.status.ErrorStatus.ALREADY_REGISTERED_PATENT;
import static inha.git.common.code.status.ErrorStatus.FILE_PROCESS_ERROR;
import static inha.git.common.code.status.ErrorStatus.INVALID_INVENTORS_SHARE;
import static inha.git.common.code.status.ErrorStatus.INVALID_USER_JWT;
import static inha.git.common.code.status.ErrorStatus.NOT_EXIST_PATENT;
import static inha.git.common.code.status.ErrorStatus.PROJECT_NOT_FOUND;
import static inha.git.common.code.status.ErrorStatus.USER_NOT_PROJECT_OWNER;

/**
 * ProjectPatentServiceImpl은 프로젝트 특허 관련 비즈니스 로직을 처리합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProjectPatentServiceImpl implements ProjectPatentService {

    private final StatisticsService statisticsService;
    private final ProjectPatentJpaRepository projectPatentJpaRepository;
    private final ProjectPatentInventorJpaRepository projectPatentInventorJpaRepository;
    private final ProjectJpaRepository projectJpaRepository;
    private final ProjectMapper projectMapper;

    @Override
    @Transactional(readOnly = true)
    public SearchPatentResponse searchPatent(User user, Integer projectIdx, PatentType type) {
        // 프로젝트 조회
        Project project = projectJpaRepository.findByIdAndState(projectIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(PROJECT_NOT_FOUND));

        // 특허 정보 확인
        ProjectPatent projectPatent = project.getProjectPatents().stream()
                .filter(p -> p.getPatentType() == type)
                .findFirst()
                .orElseThrow(() -> new BaseException(NOT_EXIST_PATENT));

        if(projectPatent.getState().equals(INACTIVE)) {
            throw new BaseException(NOT_EXIST_PATENT);
        }

        if (projectPatent.getAcceptAt() == null) {
            validateAuthorizationForUnacceptedPatent(user, project);
        }

        List<ProjectPatentInventor> inventors = projectPatentInventorJpaRepository
                .findAllByProjectPatentOrderByMainInventorDesc(projectPatent);
        return projectMapper.toSearchPatentResponse(projectPatent, inventors);
    }

    /**
     * 특허 정보를 등록합니다.
     *
     * @param user 등록을 요청한 사용자 정보
     * @param createPatentRequest 등록할 특허 정보
     * @param file 증빙자료 파일
     * @return 등록된 특허 정보
     * @throws BaseException
     *         PROJECT_NOT_FOUND: 프로젝트를 찾을 수 없는 경우
     *         USER_NOT_PROJECT_OWNER: 등록 권한이 없는 경우
     *         ALREADY_REGISTERED_PATENT: 이미 등록된 특허가 있는 경우
     */
    @Override
    public PatentResponse createPatent(User user, CreatePatentRequest createPatentRequest, MultipartFile file) {
        Project project = projectJpaRepository.findByIdAndState(createPatentRequest.projectIdx(), ACTIVE)
                .orElseThrow(() -> new BaseException(PROJECT_NOT_FOUND));
        if(user.getId() != project.getUser().getId()) {
            throw new BaseException(USER_NOT_PROJECT_OWNER);
        }

        boolean isAlreadyRegistered = project.getProjectPatents().stream()
                .anyMatch(patent -> patent.getPatentType() == createPatentRequest.patentType());
        if (isAlreadyRegistered) {
            throw new BaseException(ALREADY_REGISTERED_PATENT);
        }

        validateInventorsShare(createPatentRequest.inventors().stream()
                .map(CreatePatentInventorRequest::share)
                .toList());
        String evidence = null;
        String evidenceName = null;
        if (file != null && !file.isEmpty()) {
            evidence = FilePath.storeFile(file, PATENT);
            evidenceName = file.getOriginalFilename();
        }
        ProjectPatent savePatent = projectPatentJpaRepository.save(projectMapper.toProjectPatent(createPatentRequest, evidence, evidenceName, project));
        List<ProjectPatentInventor> inventors = projectMapper.toPatentInventor(createPatentRequest.inventors(), savePatent);
        inventors.forEach(projectPatentInventorJpaRepository::save);

        List<Field> field = project.getProjectFields()
                .stream()
                .map(ProjectField::getField)
                .toList();
        statisticsService.adjustCount(user, field, project.getSemester(), project.getCategory(),  4, true);
        return projectMapper.toPatentResponse(savePatent);
    }

    /**
     * 특허 정보를 수정합니다.
     *
     * @param user 수정을 요청한 사용자 정보
     * @param patentIdx 수정할 특허 ID
     * @param updatePatentRequest 수정할 특허 정보
     * @param file 새로운 증빙자료 파일 (선택적)
     * @return 수정된 특허 정보
     * @throws BaseException
     *         NOT_EXIST_PATENT: 특허를 찾을 수 없는 경우
     *         USER_NOT_PROJECT_OWNER: 수정 권한이 없는 경우
     */
    @Override
    public PatentResponse updatePatent(User user, Integer patentIdx,
                                       UpdatePatentRequest updatePatentRequest, MultipartFile file) {

        ProjectPatent projectPatent = findPatentAndValidate(user, patentIdx);
        updatePatentInformation(projectPatent, updatePatentRequest);
        if (file != null && !file.isEmpty()) {
            updatePatentFile(projectPatent, file);
        }
        updateInventors(projectPatent, updatePatentRequest.inventors());

        log.info("특허 수정 완료 - 사용자: {}, 특허명: {}", user.getName(), projectPatent.getInventionTitle());
        return projectMapper.toPatentResponse(projectPatentJpaRepository.save(projectPatent));
    }

    /**
     * 특허 삭제 메서드
     *
     * @param user 로그인한 사용자 정보
     * @param patentIdx 삭제할 특허 ID
     * @return PatentResponse 특허 정보
     */
    @Override
    public PatentResponse deletePatent(User user, Integer patentIdx) {
        // 프로젝트 조회 및 권한 검증
        ProjectPatent projectPatent = projectPatentJpaRepository.findByIdAndState(patentIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_EXIST_PATENT));

        Project project = projectPatent.getProject();
        if(user.getId() != project.getUser().getId() && user.getRole() != Role.ADMIN) {
            throw new BaseException(USER_NOT_PROJECT_OWNER);
        }

        // 증빙 파일이 있다면 삭제
        if (projectPatent.getEvidence() != null) {
            FilePath.deleteFile(projectPatent.getEvidence());
        }

        // 프로젝트와의 관계 제거
        project.getProjectPatents().remove(projectPatent);

        // 발명자 정보 삭제
        projectPatentInventorJpaRepository.deleteAllByProjectPatent(projectPatent);

        // 특허 삭제
        projectPatentJpaRepository.delete(projectPatent);



        List<Field> field = project.getProjectFields()
                .stream()
                .map(ProjectField::getField)
                .toList();
        statisticsService.adjustCount(user, field, project.getSemester(), project.getCategory(),  4, false);
        return projectMapper.toPatentResponse(projectPatent);
    }

    private ProjectPatent findPatentAndValidate(User user, Integer patentIdx) {
        ProjectPatent projectPatent = projectPatentJpaRepository.findByIdAndState(patentIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_EXIST_PATENT));

        Project project = projectPatent.getProject();
        if (user.getId() != project.getUser().getId() && user.getRole() != Role.ADMIN) {
            throw new BaseException(USER_NOT_PROJECT_OWNER);
        }
        return projectPatent;
    }

    private void updatePatentInformation(ProjectPatent projectPatent, UpdatePatentRequest request) {
        projectPatent.updatePatent(
                request.applicationNumber(),
                request.patentType(),
                request.applicationDate(),
                request.inventionTitle(),
                request.inventionTitleEnglish(),
                request.applicantName(),
                request.applicantEnglishName()
        );
    }

    private void updatePatentFile(ProjectPatent projectPatent, MultipartFile file) {
        try {
            // 기존 파일이 있다면 삭제
            if (projectPatent.getEvidence() != null) {
                FilePath.deleteFile(projectPatent.getEvidence());
            }

            // 새로운 파일 저장
            String storedFileUrl = FilePath.storeFile(file, PATENT);
            String storedFileName = file.getOriginalFilename();
            projectPatent.setEvidence(storedFileUrl, storedFileName);

            // 트랜잭션 롤백 시 파일 삭제를 위한 콜백 등록
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronizationAdapter() {
                        @Override
                        public void afterCompletion(int status) {
                            if (status == STATUS_ROLLED_BACK) {
                                FilePath.deleteFile(storedFileUrl);
                            }
                        }
                    }
            );
        } catch (Exception e) {
            log.error("파일 처리 중 오류 발생", e);
            throw new BaseException(FILE_PROCESS_ERROR);
        }
    }

    private void updateInventors(ProjectPatent projectPatent, List<UpdatePatentInventorRequest> inventorRequests) {
        // 기존 발명자 정보 삭제
        projectPatentInventorJpaRepository.deleteAllByProjectPatent(projectPatent);

        // 새로운 발명자 정보 등록
        if (inventorRequests != null && !inventorRequests.isEmpty()) {
            validateInventorsShare(inventorRequests.stream()
                    .map(UpdatePatentInventorRequest::share)
                    .toList());

            List<ProjectPatentInventor> inventors = inventorRequests.stream()
                    .map(request -> ProjectPatentInventor.builder()
                            .name(request.name())
                            .englishName(request.englishName())
                            .affiliation(request.affiliation())
                            .share(request.share())
                            .mainInventor(request.mainInventor())
                            .email(request.email())
                            .userNumber(request.userNumber())
                            .projectPatent(projectPatent)
                            .build())
                    .toList();

            inventors.forEach(projectPatentInventorJpaRepository::save);
        }
    }

    private void validateInventorsShare(List<String> shares) {
        double totalShare = shares.stream()
                .mapToDouble(Double::parseDouble) // 문자열을 double로 변환
                .sum(); // 합계 계산

        if (Math.abs(totalShare - 100.0) > 0.01) { // 부동소수점 오차를 고려한 비교
            throw new BaseException(INVALID_INVENTORS_SHARE);
        }
    }

    private void validateAuthorizationForUnacceptedPatent(User user, Project project) {
        boolean isOwner = project.getUser().getId().equals(user.getId());
        boolean isAdmin = user.getRole() == Role.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new BaseException(INVALID_USER_JWT);
        }
    }
}
