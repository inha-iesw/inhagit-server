package inha.git.problem.api.service;

import inha.git.common.exceptions.BaseException;
import inha.git.department.domain.repository.DepartmentJpaRepository;
import inha.git.problem.api.controller.dto.request.CreateProblemApproveRequest;
import inha.git.problem.api.controller.dto.request.CreateRequestProblemRequest;
import inha.git.problem.api.controller.dto.request.UpdateRequestProblemRequest;
import inha.git.problem.api.controller.dto.response.ProblemParticipantsResponse;
import inha.git.problem.api.controller.dto.response.RequestProblemResponse;
import inha.git.problem.api.controller.dto.response.SearchRequestProblemResponse;
import inha.git.problem.api.controller.dto.response.SearchRequestProblemsResponse;
import inha.git.problem.api.controller.dto.response.SearchUserRequestProblemResponse;
import inha.git.problem.api.mapper.ProblemRequestMapper;
import inha.git.problem.domain.Problem;
import inha.git.problem.domain.ProblemParticipant;
import inha.git.problem.domain.ProblemRequest;
import inha.git.problem.domain.enums.ProblemRequestStatus;
import inha.git.problem.domain.repository.ProblemJpaRepository;
import inha.git.problem.domain.repository.ProblemParticipantJpaRepository;
import inha.git.problem.domain.repository.ProblemRequestJpaRepository;
import inha.git.user.domain.User;
import inha.git.utils.file.FilePath;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static inha.git.common.BaseEntity.State.ACTIVE;
import static inha.git.common.BaseEntity.State.INACTIVE;
import static inha.git.common.Constant.PROBLEM_REQUEST;
import static inha.git.common.code.status.ErrorStatus.ALREADY_REQUESTED_PROBLEM;
import static inha.git.common.code.status.ErrorStatus.DEPARTMENT_NOT_FOUND;
import static inha.git.common.code.status.ErrorStatus.NOT_ALLOWED_PARTICIPATE;
import static inha.git.common.code.status.ErrorStatus.NOT_ALLOWED_VIEW_REQUESTS_PROBLEM;
import static inha.git.common.code.status.ErrorStatus.NOT_ALLOWED_VIEW_REQUEST_PROBLEM;
import static inha.git.common.code.status.ErrorStatus.NOT_AUTHORIZED_PROBLEM_REQUEST;
import static inha.git.common.code.status.ErrorStatus.NOT_EXIST_PROBLEM;
import static inha.git.common.code.status.ErrorStatus.NOT_EXIST_REQUEST_PROBLEM;
import static inha.git.common.code.status.ErrorStatus.PROBLEM_DEADLINE_PASSED;
import static inha.git.common.code.status.ErrorStatus.PROBLEM_REQUEST_CANNOT_BE_DELETED;
import static inha.git.common.code.status.ErrorStatus.PROBLEM_REQUEST_CANNOT_BE_MODIFIED;
import static inha.git.problem.domain.enums.ProblemStatus.PROGRESS;
import static inha.git.user.domain.enums.Role.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProblemRequestServiceImpl implements ProblemRequestService {

    private final ProblemJpaRepository problemJpaRepository;
    private final ProblemRequestJpaRepository problemRequestJpaRepository;
    private final ProblemParticipantJpaRepository problemParticipantJpaRepository;
    private final DepartmentJpaRepository departmentRepository;
    private final ProblemRequestMapper problemRequestMapper;

    /**
     * 문제 신청 목록 조회
     *
     * @param problemIdx 문제 인덱스
     * @param page 페이지
     * @param size 사이즈
     * @return 문제 신청 목록
     */
    @Override
    public Page<SearchRequestProblemsResponse> getRequestProblems(User user, Integer problemIdx, Integer page, Integer size) {
        Problem problem = problemJpaRepository.findByIdAndState(problemIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_EXIST_PROBLEM));
        if (!problem.getUser().getId().equals(user.getId()) && !user.getRole().equals(ADMIN)) {
            throw new BaseException(NOT_ALLOWED_VIEW_REQUESTS_PROBLEM);
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ProblemRequest> problemRequests = problemRequestJpaRepository.findByProblemAndState(problem, ACTIVE, pageable);
        return problemRequests.map(this::convertToSearchRequestProblemResponse);
    }

    /**
     * 문제 신청 조회
     *
     * @param user 유저 정보
     * @param problemIdx 문제 인덱스
     * @param problemRequestIdx 문제 신청 인덱스
     * @return 문제 신청 정보
     */
    @Override
    public SearchRequestProblemResponse getRequestProblem(User user, Integer problemIdx, Integer problemRequestIdx) {
        ProblemRequest problemRequest = problemRequestJpaRepository.findByIdAndState(problemRequestIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_EXIST_REQUEST_PROBLEM));

        if (!problemRequest.getProblem().getId().equals(problemIdx)) {
            throw new BaseException(NOT_EXIST_REQUEST_PROBLEM);
        }

        if (!problemRequest.getUser().getId().equals(user.getId()) && !problemRequest.getProblem().getUser().getId().equals(user.getId()) && !user.getRole().equals(ADMIN)) {
            throw new BaseException(NOT_ALLOWED_VIEW_REQUEST_PROBLEM);
        }
        return problemRequestMapper.toSearchRequestProblemResponse(problemRequest);
    }

    /**
     * 문제 신청
     *
     * @param user 유저 정보
     * @param createRequestProblemRequest 문제 신청 요청 정보
     * @param file 파일
     * @return 신청된 문제 정보
     */
    @Override
    @Transactional
    public RequestProblemResponse requestProblem(User user, Integer problemIdx, CreateRequestProblemRequest createRequestProblemRequest, MultipartFile file) {
        Problem problem = problemJpaRepository.findByIdAndState(problemIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_EXIST_REQUEST_PROBLEM));

        if (problem.getUser().getId().equals(user.getId())) {
            throw new BaseException(NOT_ALLOWED_PARTICIPATE);
        }

        if (LocalDateTime.of(LocalDate.parse(problem.getDuration(), DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                LocalTime.MAX).isBefore(LocalDateTime.now()) || !problem.getStatus().equals(PROGRESS)) {
            throw new BaseException(PROBLEM_DEADLINE_PASSED);
        }

        boolean exists = problemRequestJpaRepository.existsByProblemAndUserAndState(problem, user, ACTIVE);
        if (exists) {
            throw new BaseException(ALREADY_REQUESTED_PROBLEM);
        }

        ProblemRequest problemRequest = problemRequestMapper.toProblemRequest(createRequestProblemRequest, problem, user);

        if (file != null && !file.isEmpty()) {
            String filePath = FilePath.storeFile(file, PROBLEM_REQUEST);
            problemRequest.setFile(file.getOriginalFilename(), filePath);
        }

        List<ProblemParticipant> participants = createRequestProblemRequest.participants().stream()
                .map(participantRequest -> {
                    ProblemParticipant problemParticipant = problemRequestMapper.toProblemParticipant(participantRequest, problemRequest);

                    problemParticipant.setDepartment(departmentRepository.findById(participantRequest.departmentId())
                            .orElseThrow(() -> new BaseException(DEPARTMENT_NOT_FOUND)));

                    return problemParticipant;
                })
                .toList();

        problemParticipantJpaRepository.saveAll(participants);
        problemRequest.getProblemParticipants().addAll(participants);

        ProblemRequest savedProblemRequest = problemRequestJpaRepository.save(problemRequest);
        problem.increaseParticipantCount();
        return problemRequestMapper.toRequestProblemResponse(savedProblemRequest);
    }

    /**
     * 문제 신청 수정
     *
     * @param user 유저 정보
     * @param problemRequestIdx 문제 신청 인덱스
     * @param updateRequestProblemRequest 문제 신청 수정 요청 정보
     * @param file 파일
     * @return 수정된 문제 정보
     */
    @Override
    @Transactional
    public RequestProblemResponse updateRequestProblem(User user, Integer problemRequestIdx, UpdateRequestProblemRequest updateRequestProblemRequest, MultipartFile file) {
        ProblemRequest problemRequest = problemRequestJpaRepository.findByIdAndState(problemRequestIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_EXIST_REQUEST_PROBLEM));

        if (!problemRequest.getUser().getId().equals(user.getId())) {
            throw new BaseException(NOT_AUTHORIZED_PROBLEM_REQUEST);
        }

        if (!problemRequest.getProblemRequestStatus().equals(ProblemRequestStatus.REQUEST)) {
            throw new BaseException(PROBLEM_REQUEST_CANNOT_BE_MODIFIED);
        }

        problemRequest.updateRequestProblem(updateRequestProblemRequest.title(), updateRequestProblemRequest.contents());

        if (file != null && !file.isEmpty()) {
            if (problemRequest.getStoredFileUrl() != null) {
                FilePath.deleteFile(problemRequest.getStoredFileUrl());
            }
            String filePath = FilePath.storeFile(file, PROBLEM_REQUEST);
            problemRequest.setFile(file.getOriginalFilename(), filePath);
        }

        problemParticipantJpaRepository.deleteAll(problemRequest.getProblemParticipants());
        List<ProblemParticipant> participants = updateRequestProblemRequest.participants().stream()
                .map(participantRequest -> {
                    ProblemParticipant problemParticipant = problemRequestMapper.toProblemParticipant(participantRequest, problemRequest);
                    problemParticipant.setDepartment(departmentRepository.findById(participantRequest.departmentId())
                            .orElseThrow(() -> new BaseException(DEPARTMENT_NOT_FOUND)));
                    return problemParticipant;
                })
                .toList();

        problemParticipantJpaRepository.saveAll(participants);
        problemRequest.getProblemParticipants().clear();
        problemRequest.getProblemParticipants().addAll(participants);

        ProblemRequest updatedProblemRequest = problemRequestJpaRepository.save(problemRequest);

        return problemRequestMapper.toRequestProblemResponse(updatedProblemRequest);
    }

    @Override
    @Transactional
    public RequestProblemResponse deleteRequestProblem(User user, Integer problemRequestIdx) {
        ProblemRequest problemRequest = problemRequestJpaRepository.findById(problemRequestIdx)
                .orElseThrow(() -> new BaseException(NOT_EXIST_REQUEST_PROBLEM));

        if (!problemRequest.getUser().getId().equals(user.getId()) && !user.getRole().equals(ADMIN)) {
            throw new BaseException(NOT_AUTHORIZED_PROBLEM_REQUEST);
        }

        if (!problemRequest.getProblemRequestStatus().equals(ProblemRequestStatus.REQUEST)) {
            throw new BaseException(PROBLEM_REQUEST_CANNOT_BE_DELETED);
        }

        problemRequest.setState(INACTIVE);
        problemRequest.setDeletedAt();

        problemRequest.getProblem().decreaseParticipantCount();

        ProblemRequest deletedProblemRequest = problemRequestJpaRepository.save(problemRequest);
        return problemRequestMapper.toRequestProblemResponse(deletedProblemRequest);
    }

    /**
     * 문제 신청 상태 변경
     *
     * @param user 유저 정보
     * @param problemIdx 문제 인덱스
     * @param problemRequestIdx 문제 신청 인덱스
     * @param problemRequestStatus 문제 신청 상태
     * @return 변경된 문제 정보
     */
    @Override
    @Transactional
    public RequestProblemResponse updateproblemRequestStatus(User user, Integer problemIdx, Integer problemRequestIdx, ProblemRequestStatus problemRequestStatus) {
        ProblemRequest problemRequest = problemRequestJpaRepository.findByIdAndState(problemRequestIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_EXIST_REQUEST_PROBLEM));

        if (!problemRequest.getProblem().getId().equals(problemIdx)) {
            throw new BaseException(NOT_EXIST_REQUEST_PROBLEM);
        }

        if (!problemRequest.getProblem().getUser().getId().equals(user.getId()) && !user.getRole().equals(ADMIN)) {
            throw new BaseException(NOT_AUTHORIZED_PROBLEM_REQUEST);
        }

        problemRequest.setProblemRequestStatus(problemRequestStatus);

        ProblemRequest updatedProblemRequest = problemRequestJpaRepository.save(problemRequest);
        return problemRequestMapper.toRequestProblemResponse(updatedProblemRequest);
    }

    /**
     * 문제 참여자 목록 조회
     * 나중에 mapper 쓰는 것으로 변경해야함
     *
     * @param user 유저 정보
     * @param problemIdx 문제 인덱스
     * @return 참여자 목록
     */
    @Override
    public List<ProblemParticipantsResponse> getParticipants(User user, Integer problemIdx) {
//        Problem problem = problemJpaRepository.findByIdAndState(problemIdx, ACTIVE)
//                .orElseThrow(() -> new BaseException(NOT_EXIST_PROBLEM));
//
//        if (!problem.getUser().getId().equals(user.getId())) {
//            throw new BaseException(NOT_ALLOWED_VIEW_PARTICIPANT);
//        }
//        // 문제 요청 가져오기
//        List<ProblemRequest> problemRequests = problemRequestJpaRepository.findByProblemIdAndAcceptAtIsNotNullAndState(problemIdx, ACTIVE);
//
//        // 반환할 List 생성
//        List<ProblemParticipantsResponse> participantsResponses = new ArrayList<>();
//        problemRequests.forEach(request -> {
//            // problemSubmitResponse를 Optional로 받기
//            ProblemSubmitResponse problemSubmitResponse = problemSubmitJpaRepository.findByProblemRequestAndState(request, ACTIVE)
//                    .map(problemSubmit -> problemMapper.problemSubmitToProblemSubmitResponse(problemSubmit))
//                    .orElse(null);  // 없으면 null
//
//            if (request.getType() == 1) { // 개인 신청일 경우
//                ProblemPersonalRequest personalRequest = problemPersonalRequestJpaRepository.findByProblemRequestId(request.getId())
//                        .orElseThrow(() -> new BaseException(NOT_EXIST_PERSONAL_REQUEST));
//                User personalRequestUser = personalRequest.getUser();
//                participantsResponses.add(
//                        new ProblemParticipantsResponse(
//                                personalRequest.getId(), // 문제 참여 인덱스
//                                request.getAcceptAt(), // 승인 날짜
//                                request.getCreatedAt(), // 생성 날짜
//                                1, // 타입은 개인 유저
//                                problemSubmitResponse, // ProblemSubmitResponse 추가
//                                new SearchUserResponse(personalRequestUser.getId(), personalRequestUser.getName(),mapRoleToPosition(personalRequestUser.getRole())), // 유저 정보
//                                null // 팀 정보는 null
//                        )
//                );
//            } else if (request.getType() == 2) { // 팀 신청일 경우
//                ProblemTeamRequest teamRequest = problemTeamRequestJpaRepository.findByProblemRequestId(request.getId())
//                        .orElseThrow(() -> new BaseException(NOT_EXIST_TEAM_REQUEST));
//                Team team = teamRequest.getTeam();
//                participantsResponses.add(
//                        new ProblemParticipantsResponse(
//                                teamRequest.getId(), // 문제 참여 인덱스
//                                request.getAcceptAt(), // 승인 날짜
//                                request.getCreatedAt(), // 생성 날짜
//                                2, // 타입은 팀
//                                problemSubmitResponse, // ProblemSubmitResponse 추가
//                                null, // 유저 정보는 null
//                                new SearchTeamRequestProblemResponse(team.getId(), team.getName(),
//                                        new SearchUserResponse(team.getUser().getId(), team.getUser().getName(), mapRoleToPosition(team.getUser().getRole())),
//                                        team.getTeamUsers().stream()
//                                                .map(tu -> new SearchUserResponse(tu.getUser().getId(), tu.getUser().getName(), mapRoleToPosition(tu.getUser().getRole()))
//                                                ).toList()
//                                ) // 팀 정보
//                        )
//                );
//            }
//        });
        return null;
    }

    private SearchRequestProblemsResponse convertToSearchRequestProblemResponse(ProblemRequest problemRequest) {
        return new SearchRequestProblemsResponse(
                problemRequest.getId(),
                problemRequest.getTitle(),
                problemRequest.getProblemRequestStatus(),
                new SearchUserRequestProblemResponse(
                        problemRequest.getUser().getId(),
                        problemRequest.getUser().getName()
                ),
                problemRequest.getCreatedAt()
        );
    }
}
