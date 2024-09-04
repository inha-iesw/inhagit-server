package inha.git.problem.api.service;

import inha.git.common.exceptions.BaseException;
import inha.git.problem.api.controller.dto.request.*;
import inha.git.problem.api.controller.dto.response.*;
import inha.git.problem.api.mapper.ProblemMapper;
import inha.git.problem.domain.Problem;
import inha.git.problem.domain.ProblemPersonalRequest;
import inha.git.problem.domain.ProblemRequest;
import inha.git.problem.domain.ProblemTeamRequest;
import inha.git.problem.domain.repository.*;
import inha.git.project.api.controller.dto.response.SearchUserResponse;
import inha.git.team.domain.Team;
import inha.git.team.domain.repository.TeamJpaRepository;
import inha.git.user.domain.User;
import inha.git.user.domain.enums.Role;
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
import java.time.format.DateTimeFormatter;
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
    private final ProblemMapper problemMapper;
    private final ProblemQueryRepository problemQueryRepository;
    private final ProblemRequestJpaRepository problemRequestJpaRepository;
    private final ProblemPersonalRequestJpaRepository problemPersonalRequestJpaRepository;
    private final ProblemTeamRequestJpaRepository problemTeamRequestJpaRepository;
    private final TeamJpaRepository teamJpaRepository;




    /**
     * 문제 목록 조회
     *
     * @param page 페이지
     * @return 문제 목록
     */
    @Override
    public Page<SearchProblemsResponse> getProblems(Integer page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, CREATE_AT));
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
        return problemMapper.problemToSearchProblemResponse(problem, problem.getUser());
    }

    /**
     * 문제 생성
     *
     * @param user 유저 정보
     * @param createProblemRequest 문제 생성 요청 정보
     * @param file 문제 파일
     * @return 생성된 문제 정보
     */
    @Override
    @Transactional
    public ProblemResponse createProblem(User user, CreateProblemRequest createProblemRequest, MultipartFile file) {
        Problem problem = problemMapper.createProblemRequestToProblem(createProblemRequest, FilePath.storeFile(file, PROBLEM_FILE), user);
        problemJpaRepository.save(problem);
        return problemMapper.problemToProblemResponse(problem);
    }

    /**
     * 문제 수정
     *
     * @param user 유저 정보
     * @param problemIdx 문제 인덱스
     * @param updateProblemRequest 문제 수정 요청 정보
     * @param file 문제 파일
     * @return 수정된 문제 정보
     */
    @Override
    @Transactional
    public ProblemResponse updateProblem(User user, Integer problemIdx, UpdateProblemRequest updateProblemRequest, MultipartFile file) {
        Problem problem = problemJpaRepository.findByIdAndState(problemIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_EXIST_PROBLEM));
        if (!problem.getUser().getId().equals(user.getId()) && user.getRole() != Role.ADMIN) {
            throw new BaseException(NOT_AUTHORIZED_PROBLEM);
        }
        if (file.isEmpty()) {
            problemMapper.updateProblemRequestToProblem(updateProblemRequest, problem);
        } else {
            if(FilePath.deleteFile(BASE_DIR_SOURCE_2 + problem.getFilePath())){
                log.info("기존 파일 삭제 성공");
            }else {
                log.info("기존 파일 삭제 실패");
            }
            String newFilePath = FilePath.storeFile(file, PROBLEM_FILE);
            problemMapper.updateProblemRequestToProblem(updateProblemRequest, newFilePath, problem);
        }
        return problemMapper.problemToProblemResponse(problem);
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
     * 문제 신청 목록 조회
     *
     * @param problemIdx 문제 인덱스
     * @param page 페이지
     * @return 문제 신청 목록
     */
    @Override
    public Page<SearchRequestProblemResponse> getRequestProblems(Integer problemIdx, Integer page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, CREATE_AT));
        return problemQueryRepository.getRequestProblems(problemIdx, pageable);
    }

    /**
     * 문제 개인 참여
     *
     * @param user 유저 정보
     * @param createRequestProblemRequest 문제 참여 요청 정보
     * @return 참여된 문제 정보
     */
    @Override
    @Transactional
    public RequestProblemResponse requestUser(User user, CreateRequestProblemRequest createRequestProblemRequest) {
        Problem problem = problemJpaRepository.findByIdAndState(createRequestProblemRequest.problemIdx(), ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_EXIST_PROBLEM));
        if(problem.getUser().getId().equals(user.getId())){
            throw new BaseException(NOT_ALLOWED_PARTICIPATE);
        }
        if (LocalDate.parse(problem.getDuration(), DateTimeFormatter.ofPattern("yyyy-MM-dd")).isBefore(LocalDate.now())) {
            throw new BaseException(PROBLEM_DEADLINE_PASSED);
        }
        problemPersonalRequestJpaRepository.findByProblemAndUser(problem, user)
                .ifPresent(problemPersonalRequest -> {
                    if(problemPersonalRequest.getProblemRequest().getAcceptAt() != null){
                        throw new BaseException(ALREADY_PARTICIPATED_PROBLEM);
                    }
                    else {
                        throw new BaseException(ALREADY_REQUESTED_PROBLEM);
                    }
                });
        ProblemRequest problemRequest = problemMapper.createProblemRequestToProblemRequest(problem, 1);
        problemRequestJpaRepository.save(problemRequest);
        ProblemPersonalRequest problemPersonalRequest = problemMapper.createRequestProblemRequestToProblemPersonalRequest(user, problemRequest);
        problemPersonalRequestJpaRepository.save(problemPersonalRequest);
        return problemMapper.problemRequestToRequestProblemResponse(problemRequest);

    }

    /**
     * 문제 팀 참여
     *
     * @param user 유저 정보
     * @param createTeamRequestProblemRequest 팀 참여 요청 정보
     * @return 참여된 문제 정보
     */
    @Override
    @Transactional
    public RequestProblemResponse requestTeam(User user, CreateTeamRequestProblemRequest createTeamRequestProblemRequest) {
        Problem problem = problemJpaRepository.findByIdAndState(createTeamRequestProblemRequest.problemIdx(), ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_EXIST_PROBLEM));
        Team team = teamJpaRepository.findByIdAndState(createTeamRequestProblemRequest.teamIdx(), ACTIVE)
                .orElseThrow(() -> new BaseException(TEAM_NOT_FOUND));
        if(!team.getUser().getId().equals(user.getId())){
            throw new BaseException(ONLY_LEADER_CAN_APPLY);
        }
        if(problem.getUser().getId().equals(user.getId())){
            throw new BaseException(NOT_ALLOWED_PARTICIPATE);
        }
        if (LocalDate.parse(problem.getDuration(), DateTimeFormatter.ofPattern("yyyy-MM-dd")).isBefore(LocalDate.now())) {
            throw new BaseException(PROBLEM_DEADLINE_PASSED);
        }
        problemTeamRequestJpaRepository.findByProblemAndTeam(problem, team)
                .ifPresent(problemTeamRequest -> {
                    if(problemTeamRequest.getProblemRequest().getAcceptAt() != null){
                        throw new BaseException(ALREADY_PARTICIPATED_PROBLEM);
                    } else {
                        throw new BaseException(ALREADY_REQUESTED_PROBLEM);
                    }
                });
        ProblemRequest problemRequest = problemMapper.createProblemRequestToProblemRequest(problem, 2); // 2는 팀 신청임을 나타냄
        problemRequestJpaRepository.save(problemRequest);
        ProblemTeamRequest problemTeamRequest = problemMapper.createTeamRequestProblemRequestToProblemTeamRequest(team, problemRequest);
        problemTeamRequestJpaRepository.save(problemTeamRequest);
        return problemMapper.problemRequestToRequestProblemResponse(problemRequest);
    }

    /**
     * 문제 참여 승인
     *
     * @param user 유저 정보
     * @param createProblemApproveRequest 문제 참여 승인 요청 정보
     * @return 승인된 문제 정보
     */
    @Override
    @Transactional
    public RequestProblemResponse approveRequest(User user, CreateProblemApproveRequest createProblemApproveRequest) {
        ProblemRequest problemRequest = problemRequestJpaRepository.findByIdAndState(createProblemApproveRequest.requestIdx(), ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_EXIST_REQUEST_PROBLEM));
        if(!problemRequest.getProblem().getUser().getId().equals(user.getId())){
            throw new BaseException(NOT_ALLOWED_APPROVE);
        }
        if(problemRequest.getAcceptAt() != null){
            throw new BaseException(ALREADY_APPROVED_REQUEST);
        }
        problemRequest.setAcceptAt();
        return problemMapper.problemRequestToRequestProblemResponse(problemRequest);
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
        Problem problem = problemJpaRepository.findByIdAndState(problemIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_EXIST_PROBLEM));

        if (!problem.getUser().getId().equals(user.getId())) {
            throw new BaseException(NOT_ALLOWED_VIEW_PARTICIPANT);
        }
        // 문제 요청 가져오기
        List<ProblemRequest> problemRequests = problemRequestJpaRepository.findByProblemIdAndAcceptAtIsNotNullAndState(problemIdx, ACTIVE);
        // 반환할 List 생성
        List<ProblemParticipantsResponse> participantsResponses = new ArrayList<>();
        problemRequests.forEach(request -> {
            if (request.getType() == 1) { // 개인 신청일 경우
                ProblemPersonalRequest personalRequest = problemPersonalRequestJpaRepository.findByProblemRequestId(request.getId())
                        .orElseThrow(() -> new BaseException(NOT_EXIST_PERSONAL_REQUEST));
                User personalRequestUser = personalRequest.getUser();
                participantsResponses.add(
                        new ProblemParticipantsResponse(
                                personalRequest.getId(), // 문제 참여 인덱스
                                request.getAcceptAt(), // 승인 날짜
                                request.getCreatedAt(), // 생성 날짜
                                1, // 타입은 개인 유저
                                null, // ProblemSubmitResponse는 따로 정의 필요
                                new SearchUserResponse(personalRequestUser.getId(), personalRequestUser.getName()), // 유저 정보
                                null // 팀 정보는 null
                        )
                );
            } else if (request.getType() == 2) { // 팀 신청일 경우
                ProblemTeamRequest teamRequest = problemTeamRequestJpaRepository.findByProblemRequestId(request.getId())
                        .orElseThrow(() -> new BaseException(NOT_EXIST_TEAM_REQUEST));
                Team team = teamRequest.getTeam();
                participantsResponses.add(
                        new ProblemParticipantsResponse(
                                teamRequest.getId(), // 문제 참여 인덱스
                                request.getAcceptAt(), // 승인 날짜
                                request.getCreatedAt(), // 생성 날짜
                                2, // 타입은 팀
                                null, // ProblemSubmitResponse는 따로 정의 필요
                                null, // 유저 정보는 null
                                new SearchTeamRequestProblemResponse(team.getId(), team.getName(),
                                        new SearchUserResponse(team.getUser().getId(), team.getUser().getName()),
                                        team.getTeamUsers().stream()
                                                .map(tu -> new SearchUserResponse(tu.getUser().getId(), tu.getUser().getName()))
                                                .toList()
                                        ) // 팀 정보
                        )
                );
            }
        });
        return participantsResponses;
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
        Problem problem = problemJpaRepository.findByIdAndState(problemIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_EXIST_PROBLEM));
        if (problem.getUser().getId().equals(user.getId())) {
            throw new BaseException(NOT_ALLOWED_CHECK_SUBMIT);
        }
        List<ProblemRequest> problemRequests = problemRequestJpaRepository.findByProblemIdAndState(problemIdx, ACTIVE);

        List<SearchRequestProblemResponse> submitResponses = new ArrayList<>();
        problemRequests.forEach(request -> {
            if (request.getType() == 1) {
                ProblemPersonalRequest personalRequest = problemPersonalRequestJpaRepository.findByProblemRequestId(request.getId())
                        .orElseThrow(() -> new BaseException(NOT_EXIST_PERSONAL_REQUEST));
                User personalRequestUser = personalRequest.getUser();
                submitResponses.add(
                        new SearchRequestProblemResponse(
                                personalRequest.getId(),
                                request.getType(),
                                request.getCreatedAt(),
                                request.getAcceptAt(),
                                new SearchUserRequestProblemResponse(personalRequestUser.getId(), personalRequestUser.getName()),
                                null
                        )
                );
            } else if (request.getType() == 2) {
                ProblemTeamRequest teamRequest = problemTeamRequestJpaRepository.findByProblemRequestId(request.getId())
                        .orElseThrow(() -> new BaseException(NOT_EXIST_TEAM_REQUEST));
                Team team = teamRequest.getTeam();
                if(team.getUser().getId().equals(user.getId())) {
                    submitResponses.add(
                            new SearchRequestProblemResponse(
                                    teamRequest.getId(),
                                    request.getType(),
                                    request.getCreatedAt(),
                                    request.getAcceptAt(),
                                    null,
                                    new SearchTeamRequestProblemResponse(team.getId(), team.getName(),
                                            new SearchUserResponse(team.getUser().getId(), team.getUser().getName()),
                                            team.getTeamUsers().stream()
                                                    .map(tu -> new SearchUserResponse(tu.getUser().getId(), tu.getUser().getName()))
                                                    .toList()
                                    )
                            )
                    );
                }
            }
        });
        return submitResponses;
    }


}
