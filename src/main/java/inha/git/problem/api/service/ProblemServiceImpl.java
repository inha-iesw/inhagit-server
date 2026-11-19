package inha.git.problem.api.service;

import inha.git.common.exceptions.BaseException;
import inha.git.field.domain.Field;
import inha.git.field.domain.repository.FieldJpaRepository;
import inha.git.mapping.domain.ProblemField;
import inha.git.mapping.domain.id.ProblemFieldId;
import inha.git.mapping.domain.repository.ProblemFieldJpaRepository;
import inha.git.problem.api.controller.dto.request.CreateProblemRequest;
import inha.git.problem.api.controller.dto.request.UpdateProblemRequest;
import inha.git.problem.api.controller.dto.response.ProblemResponse;
import inha.git.problem.api.controller.dto.response.SearchProblemAttachmentResponse;
import inha.git.problem.api.controller.dto.response.SearchProblemResponse;
import inha.git.problem.api.controller.dto.response.SearchProblemsResponse;
import inha.git.problem.api.mapper.ProblemMapper;
import inha.git.problem.domain.Problem;
import inha.git.problem.domain.ProblemAttachment;
import inha.git.problem.domain.ProblemRequest;
import inha.git.problem.domain.enums.ProblemStatus;
import inha.git.problem.domain.repository.ProblemAttachmentJpaRepository;
import inha.git.problem.domain.repository.ProblemJpaRepository;
import inha.git.problem.domain.repository.ProblemQueryRepository;
import inha.git.project.api.controller.dto.response.SearchFieldResponse;
import inha.git.project.api.controller.dto.response.SearchUserResponse;
import inha.git.user.domain.User;
import inha.git.user.domain.enums.Role;
import inha.git.utils.IdempotentProvider;
import inha.git.utils.file.FilePath;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static inha.git.common.BaseEntity.State.ACTIVE;
import static inha.git.common.BaseEntity.State.INACTIVE;
import static inha.git.common.Constant.BASE_DIR_SOURCE_2;

import static inha.git.common.Constant.CREATE_AT;
import static inha.git.common.Constant.PROBLEM;
import static inha.git.common.Constant.mapRoleToPosition;
import static inha.git.common.code.status.ErrorStatus.FIELD_NOT_FOUND;
import static inha.git.common.code.status.ErrorStatus.NOT_AUTHORIZED_PROBLEM;
import static inha.git.common.code.status.ErrorStatus.NOT_EXIST_PROBLEM;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProblemServiceImpl implements ProblemService {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private final ProblemJpaRepository problemJpaRepository;
    private final ProblemAttachmentJpaRepository problemAttachmentJpaRepository;
    private final ProblemFieldJpaRepository problemFieldJpaRepository;
    private final FieldJpaRepository fieldJpaRepository;
    private final ProblemMapper problemMapper;
    private final ProblemQueryRepository problemQueryRepository;
    private final IdempotentProvider idempotentProvider;

    /**
     * 문제 목록 조회
     *
     * @param page 페이지
     * @param size 사이즈
     * @return 문제 목록
     */
    @Override
    public Page<SearchProblemsResponse> getProblems(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, CREATE_AT));
        return problemQueryRepository.getProblems(pageable);
    }

    /**
     * 문제 상세 조회
     *
     * @param problemIdx 문제 인덱스
     * @return 문제 상세 정보
     */
    @Override
    public SearchProblemResponse getProblem(User user, Integer problemIdx) {
        Problem problem = problemJpaRepository.findByIdAndState(problemIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_EXIST_PROBLEM));
        SearchUserResponse author = new SearchUserResponse(problem.getUser().getId(), problem.getUser().getName(), mapRoleToPosition(problem.getUser().getRole()));
        List<SearchProblemAttachmentResponse> attachments = problem.getProblemAttachments().stream()
                .map(attachment -> new SearchProblemAttachmentResponse(attachment.getId(), attachment.getOriginalFileName(), attachment.getStoredFileUrl()))
                .toList();

        List<SearchFieldResponse> fieldList = problem.getProblemFields().stream()
                .map(problemField -> new SearchFieldResponse(problemField.getField().getId(), problemField.getField().getName()))
                .toList();

        Integer problemRequestIdx = problem.getProblemRequests().stream()
                .filter(request -> request.getUser().getId().equals(user.getId())
                        && request.getState().equals(ACTIVE))
                .findFirst()
                .map(ProblemRequest::getId)
                .orElse(null);
        return problemMapper.problemToSearchProblemResponse(problem, author, attachments, fieldList, problemRequestIdx);
    }

    /**
     * 문제 생성
     *
     * @param user 유저 정보
     * @param createProblemRequest 문제 생성 요청 정보
     * @param files 문제 파일들
     * @return 생성된 문제 정보
     */
    @Override
    @Transactional
    public ProblemResponse createProblem(User user, CreateProblemRequest createProblemRequest, List<MultipartFile> files) {
        idempotentProvider.isValidIdempotent(List.of("createNoticeRequest", user.getId().toString(), user.getName(), createProblemRequest.title()));

        Problem problem = problemMapper.createProblemRequestToProblem(user, createProblemRequest);
        if (files != null && !files.isEmpty()) {
            problem.setHasAttachment(true);
        }
        else {
            problem.setHasAttachment(false);
        }
        Problem savedProblem = problemJpaRepository.save(problem);

        if (files != null && !files.isEmpty()) {
            List<ProblemAttachment> problemAttachments = new ArrayList<>();
            files.forEach(file -> {
                String filePath = FilePath.storeFile(file, PROBLEM);
                problemAttachments.add(ProblemAttachment.builder()
                        .storedFileUrl(filePath)
                        .originalFileName(file.getOriginalFilename())
                        .problem(savedProblem)
                        .build());
            });
            problemAttachmentJpaRepository.saveAll(problemAttachments);
        }

        List<ProblemField> problemFields = createAndSaveProblemFields(createProblemRequest.fieldIdxList(), savedProblem);
        problemFieldJpaRepository.saveAll(problemFields);

        return problemMapper.problemToProblemResponse(savedProblem);
    }

    /**
     * 문제 수정
     *
     * @param user 유저 정보
     * @param problemIdx 문제 인덱스
     * @param updateProblemRequest 문제 수정 요청 정보
     * @param files 문제 파일들
     * @return 수정된 문제 정보
     */
    @Override
    @Transactional
    public ProblemResponse updateProblem(User user, Integer problemIdx, UpdateProblemRequest updateProblemRequest, List<MultipartFile> files) {
        Problem problem = problemJpaRepository.findByIdAndState(problemIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_EXIST_PROBLEM));

        if (!problem.getUser().getId().equals(user.getId()) && user.getRole() != Role.ADMIN) {
            throw new BaseException(NOT_AUTHORIZED_PROBLEM);
        }

        Set<Integer> existingFieldIds = problem.getProblemFields().stream()
                .map(pf -> pf.getField().getId())
                .collect(Collectors.toSet());

        String previousDuration = problem.getDuration();
        problemMapper.updateProblemRequestToProblem(updateProblemRequest, problem);
        reopenIfDeadlineExtended(problem, previousDuration, updateProblemRequest.duration());
        Problem savedProblem = problemJpaRepository.saveAndFlush(problem);

        handleAttachments(problem, files);

        Set<Integer> newFieldIdSet = new HashSet<>(updateProblemRequest.fieldIdxList());

        problem.getProblemFields().removeIf(problemField ->
                !newFieldIdSet.contains(problemField.getField().getId()));
        problemJpaRepository.saveAndFlush(problem);

        newFieldIdSet.stream()
                .filter(id -> !existingFieldIds.contains(id))
                .forEach(id -> {
                    Field field = fieldJpaRepository.findById(id)
                            .orElseThrow(() -> new BaseException(FIELD_NOT_FOUND));
                    ProblemField newProblemField = new ProblemField(
                            new ProblemFieldId(problem.getId(), field.getId()),
                            problem,
                            field
                    );
                    problem.getProblemFields().add(newProblemField);
                });

        Problem finalSavedProblem = problemJpaRepository.saveAndFlush(problem);
        return problemMapper.problemToProblemResponse(finalSavedProblem);
    }

    /**
     * 마감일 연장 시 문제 상태 되돌리기
     *
     * <p>마감일이 기존보다 늦어졌고 새 마감일이 오늘(KST) 이후이며, 현재 상태가 COMPLETED이면 PROGRESS로 되돌린다.
     * 마감일이 바뀌지 않았거나 앞당겨진 경우에는 출제자가 수동으로 설정한 상태를 유지한다.</p>
     *
     * @param problem          문제
     * @param previousDuration 수정 전 마감일 ("yyyy-MM-dd")
     * @param newDuration      수정 후 마감일 ("yyyy-MM-dd")
     */
    private void reopenIfDeadlineExtended(Problem problem, String previousDuration, String newDuration) {
        if (problem.getStatus() != ProblemStatus.COMPLETED || newDuration == null) {
            return;
        }
        boolean isExtended = previousDuration == null || newDuration.compareTo(previousDuration) > 0;
        boolean isNotExpired = newDuration.compareTo(today()) >= 0;
        if (isExtended && isNotExpired) {
            problem.setStatus(ProblemStatus.PROGRESS);
            log.info("마감일 연장으로 문제 상태 PROGRESS 변경 (문제 ID: {}, 마감일: {} -> {})", problem.getId(), previousDuration, newDuration);
        }
    }

    /**
     * 마감일 지난 문제 상태 COMPLETED로 일괄 변경 - 하루에 한번 (00:00 KST)
     *
     * <p>마감일(duration)이 오늘보다 이전이고 PROGRESS 상태인 활성 문제를 COMPLETED로 변경한다.
     * 마감일 당일은 23:59:59까지 참여 가능하므로 변경 대상에서 제외된다.</p>
     */
    @Override
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    @Transactional
    public void completeExpiredProblems() {
        int updatedCount = problemJpaRepository.bulkUpdateStatusByDurationBefore(
                today(), ProblemStatus.PROGRESS, ProblemStatus.COMPLETED, ACTIVE, LocalDateTime.now(KST));
        log.info("마감일 지난 문제 상태 COMPLETED 변경 완료 (변경된 문제 수: {})", updatedCount);
    }

    /**
     * 오늘 날짜(KST)를 duration과 같은 "yyyy-MM-dd" 형식으로 반환
     */
    private String today() {
        return LocalDate.now(KST).format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    private void handleAttachments(Problem problem, List<MultipartFile> files) {
        List<ProblemAttachment> existingAttachments = new ArrayList<>(problem.getProblemAttachments());
        problem.getProblemAttachments().clear();

        if (!existingAttachments.isEmpty()) {
            existingAttachments.forEach(attachment -> {
                FilePath.deleteFile(BASE_DIR_SOURCE_2 + attachment.getStoredFileUrl());
                problemAttachmentJpaRepository.delete(attachment);
            });
        }

        if (files != null && !files.isEmpty()) {
            problem.setHasAttachment(true);
            files.forEach(file -> {
                String originalFileName = file.getOriginalFilename();
                String storedFileUrl = FilePath.storeFile(file, PROBLEM);
                registerRollbackCleanup(storedFileUrl);

                ProblemAttachment attachment = problemMapper.createProblemAttachmentRequestToProblemAttachment(
                        originalFileName, storedFileUrl, problem
                );
                ProblemAttachment savedAttachment = problemAttachmentJpaRepository.save(attachment);
                problem.getProblemAttachments().add(savedAttachment);
            });
        } else {
            problem.setHasAttachment(false);
        }
        problemJpaRepository.save(problem);
    }

    /**
     * 문제 상태 변경
     *
     * @param user 유저 정보
     * @param problemIdx 문제 인덱스
     * @param status 변경할 상태
     * @return 변경된 문제 정보
     */
    @Override
    @Transactional
    public ProblemResponse updateProblemStatus(User user, Integer problemIdx, ProblemStatus status) {
        Problem problem = problemJpaRepository.findByIdAndState(problemIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_EXIST_PROBLEM));
        if (!problem.getUser().getId().equals(user.getId()) && user.getRole() != Role.ADMIN) {
            throw new BaseException(NOT_AUTHORIZED_PROBLEM);
        }
        problem.setStatus(status);
        Problem saveldProblem = problemJpaRepository.save(problem);
        return problemMapper.problemToProblemResponse(saveldProblem);
    }

    /**
     * 문제 삭제
     *
     * @param user 유저 정보
     * @param problemIdx 문제 인덱스
     * @return 삭제된 문제 정보
     */
    @Override
    @Transactional
    public ProblemResponse deleteProblem(User user, Integer problemIdx) {
        Problem problem = problemJpaRepository.findByIdAndState(problemIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_EXIST_PROBLEM));
        if (!problem.getUser().getId().equals(user.getId()) && user.getRole() != Role.ADMIN) {
            throw new BaseException(NOT_AUTHORIZED_PROBLEM);
        }
        problem.setDeletedAt();
        problem.setState(INACTIVE);
        return problemMapper.problemToProblemResponse(problem);
    }

    private void registerRollbackCleanup(String zipFilePath) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
            @Override
            public void afterCompletion(int status) {
                if (status == STATUS_ROLLED_BACK) {
                    log.info("트랜잭션 롤백 시 파일 삭제 로직 실행");
                    log.info(BASE_DIR_SOURCE_2 + zipFilePath);
                    boolean isFileDeleted = FilePath.deleteFile(BASE_DIR_SOURCE_2 + zipFilePath);
                    if (isFileDeleted ) {
                        log.info("파일이 성공적으로 삭제되었습니다.");
                    } else {
                        log.error("파일 또는 디렉토리 삭제에 실패했습니다.");
                    }
                }
            }
        });
    }

    private List<ProblemField>  createAndSaveProblemFields(List<Integer> fieldIdxList, Problem problem) {
        return fieldIdxList.stream()
                .map(fieldIdx -> {
                    Field field = fieldJpaRepository.findByIdAndState(fieldIdx, ACTIVE)
                            .orElseThrow(() -> new BaseException(FIELD_NOT_FOUND));
                    return problemMapper.createProblemField(problem, field);
                }).toList();
    }
}
