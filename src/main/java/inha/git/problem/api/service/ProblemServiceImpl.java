package inha.git.problem.api.service;

import inha.git.common.exceptions.BaseException;
import inha.git.field.domain.Field;
import inha.git.field.domain.repository.FieldJpaRepository;
import inha.git.mapping.domain.ProblemField;
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
import inha.git.problem.domain.enums.ProblemStatus;
import inha.git.problem.domain.repository.ProblemAttachmentJpaRepository;
import inha.git.problem.domain.repository.ProblemJpaRepository;
import inha.git.problem.domain.repository.ProblemQueryRepository;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static inha.git.common.BaseEntity.State.ACTIVE;
import static inha.git.common.BaseEntity.State.INACTIVE;
import static inha.git.common.Constant.ATTACHMENT;
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
    public SearchProblemResponse getProblem(Integer problemIdx) {
        Problem problem = problemJpaRepository.findByIdAndState(problemIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_EXIST_PROBLEM));
        SearchUserResponse author = new SearchUserResponse(problem.getUser().getId(), problem.getUser().getName(), mapRoleToPosition(problem.getUser().getRole()));
        List<SearchProblemAttachmentResponse> attachments = problem.getProblemAttachments().stream()
                .map(attachment -> new SearchProblemAttachmentResponse(attachment.getId(), attachment.getOriginalFileName(), attachment.getStoredFileUrl()))
                .toList();
        return problemMapper.problemToSearchProblemResponse(problem, author, attachments);
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
        problemMapper.updateProblemRequestToProblem(updateProblemRequest, problem);
        Problem savedProblem = problemJpaRepository.save(problem);
        // 기존 첨부파일들의 실제 파일 삭제 및 DB에서 삭제
        if (problem.getProblemAttachments() != null && !problem.getProblemAttachments().isEmpty()) {
            problem.setHasAttachment(false);
            problem.getProblemAttachments().forEach(attachment -> {
                FilePath.deleteFile(BASE_DIR_SOURCE_2 + attachment.getStoredFileUrl());
                problemAttachmentJpaRepository.delete(attachment);
            });
            problem.setProblemAttachments(new ArrayList<>());
        }
        if (files != null && !files.isEmpty()) {
            problem.setHasAttachment(true);
            problem.getProblemAttachments().addAll(
                    files.stream()
                            .map(file -> {
                                String originalFileName = file.getOriginalFilename();
                                String storedFileUrl = FilePath.storeFile(file, ATTACHMENT);
                                // 트랜잭션 롤백 시 파일 삭제를 위한 등록
                                registerRollbackCleanup(storedFileUrl);
                                ProblemAttachment attachment = problemMapper.createProblemAttachmentRequestToProblemAttachment(
                                        originalFileName,
                                        storedFileUrl,
                                        savedProblem
                                );
                                return problemAttachmentJpaRepository.save(attachment);
                            })
                            .toList()
            );
        }
        return problemMapper.problemToProblemResponse(savedProblem);
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
