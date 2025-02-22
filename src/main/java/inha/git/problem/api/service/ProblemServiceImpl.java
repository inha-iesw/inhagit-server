package inha.git.problem.api.service;

import inha.git.common.exceptions.BaseException;
import inha.git.problem.api.controller.dto.request.*;
import inha.git.problem.api.controller.dto.response.*;
import inha.git.problem.api.mapper.ProblemMapper;
import inha.git.problem.domain.*;
import inha.git.problem.domain.enums.ProblemStatus;
import inha.git.problem.domain.repository.*;
import inha.git.project.api.controller.dto.response.SearchUserResponse;
import inha.git.user.domain.User;
import inha.git.user.domain.enums.Role;
import inha.git.utils.IdempotentProvider;
import inha.git.utils.file.FilePath;
import inha.git.utils.file.UnZip;
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
import static inha.git.common.Constant.*;

import static inha.git.common.code.status.ErrorStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProblemServiceImpl implements ProblemService {

    private final ProblemJpaRepository problemJpaRepository;
    private final ProblemAttachmentJpaRepository problemAttachmentJpaRepository;
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



    /**
     * 문제 제출 가능 목록 조회
     *
     * @param user 유저 정보
     * @param problemIdx 문제 인덱스
     * @return 제출 가능 목록
     */
    @Override
    public List<SearchRequestProblemResponse> getAvailableSubmits(User user, Integer problemIdx) {
//        Problem problem = problemJpaRepository.findByIdAndState(problemIdx, ACTIVE)
//                .orElseThrow(() -> new BaseException(NOT_EXIST_PROBLEM));
//        if (problem.getUser().getId().equals(user.getId())) {
//            throw new BaseException(NOT_ALLOWED_CHECK_SUBMIT);
//        }
//        List<ProblemRequest> problemRequests = problemRequestJpaRepository.findByProblemIdAndState(problemIdx, ACTIVE);
//
//        List<SearchRequestProblemResponse> submitResponses = new ArrayList<>();
//        problemRequests.forEach(request -> {
//            if (request.getType() == 1) {
//                ProblemPersonalRequest personalRequest = problemPersonalRequestJpaRepository.findByProblemRequestId(request.getId())
//                        .orElseThrow(() -> new BaseException(NOT_EXIST_PERSONAL_REQUEST));
//                User personalRequestUser = personalRequest.getUser();
//                submitResponses.add(
//                        new SearchRequestProblemResponse(
//                                personalRequest.getId(),
//                                request.getType(),
//                                request.getCreatedAt(),
//                                request.getAcceptAt(),
//                                new SearchUserRequestProblemResponse(personalRequestUser.getId(), personalRequestUser.getName()),
//                                null
//                        )
//                );
//            } else if (request.getType() == 2) {
//                ProblemTeamRequest teamRequest = problemTeamRequestJpaRepository.findByProblemRequestId(request.getId())
//                        .orElseThrow(() -> new BaseException(NOT_EXIST_TEAM_REQUEST));
//                Team team = teamRequest.getTeam();
//                if(team.getUser().getId().equals(user.getId())) {
//                    submitResponses.add(
//                            new SearchRequestProblemResponse(
//                                    teamRequest.getId(),
//                                    request.getType(),
//                                    request.getCreatedAt(),
//                                    request.getAcceptAt(),
//                                    null,
//                                    new SearchTeamRequestProblemResponse(team.getId(), team.getName(),
//                                            new SearchUserResponse(team.getUser().getId(), team.getUser().getName(), mapRoleToPosition(team.getUser().getRole())),
//                                            team.getTeamUsers().stream()
//                                                    .map(tu -> new SearchUserResponse(tu.getUser().getId(), tu.getUser().getName(), mapRoleToPosition(tu.getUser().getRole())))
//                                                    .toList()
//                                    )
//                            )
//                    );
//                }
//            }
//        });
        return null;
    }

    /**
     * 문제 제출
     *
     * @param user 유저 정보
     * @param personalIdx 개인 문제 인덱스
     * @param file 제출 파일
     * @return 제출 정보
     */
    @Override
    @Transactional
    public ProblemSubmitResponse submitPersonal(User user, Integer personalIdx, MultipartFile file) {
//        ProblemPersonalRequest problemPersonalRequest = problemPersonalRequestJpaRepository.findById(personalIdx)
//                .orElseThrow(() -> new BaseException(NOT_EXIST_PERSONAL_REQUEST));
//        ProblemRequest problemRequest = problemPersonalRequest.getProblemRequest();
//        problemSubmitJpaRepository.findByProblemRequestAndState(problemRequest, ACTIVE)
//                .ifPresent(problemSubmit -> {
//                    throw new BaseException(ALREADY_SUBMITTED);
//                });
//        Problem problem = problemRequest.getProblem();
//        if(!problemRequest.getType().equals(1)) {
//            throw new BaseException(NOT_PERSONAL_REQUEST);
//        }
//        if (problem.getUser().getId().equals(user.getId())) {
//            throw new BaseException(NOT_ALLOWED_SUBMIT);
//        }
//        if(!problemPersonalRequest.getUser().getId().equals(user.getId())){
//            throw new BaseException(NOT_PARTICIPANT);
//        }
//        if (LocalDate.parse(problem.getDuration(), DateTimeFormatter.ofPattern("yyyy-MM-dd")).isBefore(LocalDate.now())) {
//            throw new BaseException(PROBLEM_DEADLINE_PASSED);
//        }
//        ProblemPersonalRequest personalRequest = problemPersonalRequestJpaRepository.findByProblemAndUser(problem, user)
//                .orElseThrow(() -> new BaseException(NOT_EXIST_PERSONAL_REQUEST));
//        if (personalRequest.getProblemRequest().getAcceptAt() == null) {
//            throw new BaseException(NOT_ALLOWED_SUBMIT_PERSONAL);
//        }
//
//        String[] paths = storeAndUnzipFile(file);
//        String zipFilePath = paths[0];
//        String folderName = paths[1];
//
//        registerRollbackCleanup(zipFilePath, folderName);
//        ProblemSubmit problemSubmit = problemSubmitJpaRepository.save(problemMapper.createProblemSubmitRequestToProblemSubmit(problemRequest, zipFilePath, folderName));
//        return problemMapper.problemSubmitToProblemSubmitResponse(problemSubmit);
        return null;
    }

    /**
     * 문제 팀 제출
     *
     * @param user 유저 정보
     * @param teamIdx 팀 문제 인덱스
     * @param file 제출 파일
     * @return 제출 정보
     */
    @Override
    @Transactional
    public ProblemSubmitResponse submitTeam(User user, Integer teamIdx, MultipartFile file) {
//        ProblemTeamRequest problemTeamRequest = problemTeamRequestJpaRepository.findById(teamIdx)
//                .orElseThrow(() -> new BaseException(NOT_EXIST_TEAM_REQUEST));
//        ProblemRequest problemRequest = problemTeamRequest.getProblemRequest();
//        Problem problem = problemRequest.getProblem();
//        Team team = problemTeamRequest.getTeam();
//        problemSubmitJpaRepository.findByProblemRequestAndState(problemRequest, ACTIVE)
//                .ifPresent(problemSubmit -> {
//                    throw new BaseException(ALREADY_SUBMITTED);
//                });
//        if(!problemRequest.getType().equals(2)) {
//            throw new BaseException(NOT_TEAM_REQUEST);
//        }
//        if (problem.getUser().getId().equals(user.getId())) {
//            throw new BaseException(NOT_TEAM_PARTICIPANT);
//        }
//        if(!team.getUser().getId().equals(user.getId())){
//            throw new BaseException(NOT_TEAM_LEADER);
//        }
//        if (LocalDate.parse(problem.getDuration(), DateTimeFormatter.ofPattern("yyyy-MM-dd")).isBefore(LocalDate.now())) {
//            throw new BaseException(PROBLEM_DEADLINE_PASSED);
//        }
//        ProblemTeamRequest teamRequest = problemTeamRequestJpaRepository.findByProblemAndTeam(problem, team)
//                .orElseThrow(() -> new BaseException(NOT_EXIST_TEAM_REQUEST));
//        if (teamRequest.getProblemRequest().getAcceptAt() == null) {
//            throw new BaseException(NOT_ALLOWED_SUBMIT_TEAM);
//        }
//
//        String[] paths = storeAndUnzipFile(file);
//        String zipFilePath = paths[0];
//        String folderName = paths[1];
//
//        registerRollbackCleanup(zipFilePath, folderName);
//        ProblemSubmit problemSubmit = problemSubmitJpaRepository.save(problemMapper.createProblemSubmitRequestToProblemSubmit(problemRequest, zipFilePath, folderName));
//        return problemMapper.problemSubmitToProblemSubmitResponse(problemSubmit);
        return null;
    }

    /**
     * 파일 저장 및 압축 해제
     *
     * @param file 저장할 파일
     * @return 압축 해제된 폴더명
     */
    private String[] storeAndUnzipFile(MultipartFile file) {
        String zipFilePath = FilePath.storeFile(file, PROBLEM_ZIP);
        String folderName = zipFilePath.substring(zipFilePath.lastIndexOf("/") + 1, zipFilePath.lastIndexOf(".zip"));
        UnZip.unzipFile(BASE_DIR_SOURCE + zipFilePath, BASE_DIR_SOURCE + PROBLEM + '/' + folderName);
        log.info("압축 해제될 폴더 경로 " + BASE_DIR_SOURCE + zipFilePath + " " + folderName);
        return new String[] { zipFilePath, folderName };
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
}
