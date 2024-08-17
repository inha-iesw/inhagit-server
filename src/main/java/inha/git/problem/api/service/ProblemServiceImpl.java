package inha.git.problem.api.service;

import inha.git.common.exceptions.BaseException;
import inha.git.problem.api.controller.dto.request.CreateProblemRequest;
import inha.git.problem.api.controller.dto.request.UpdateProblemRequest;
import inha.git.problem.api.controller.dto.response.ProblemResponse;
import inha.git.problem.api.controller.dto.response.SearchProblemsResponse;
import inha.git.problem.api.mapper.ProblemMapper;
import inha.git.problem.domain.Problem;
import inha.git.problem.domain.repository.ProblemJpaRepository;
import inha.git.problem.domain.repository.ProblemQueryRepository;
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

import static inha.git.common.BaseEntity.State.INACTIVE;
import static inha.git.common.Constant.*;
import static inha.git.common.code.status.ErrorStatus.NOT_AUTHORIZED_PROBLEM;
import static inha.git.common.code.status.ErrorStatus.NOT_EXIST_PROBLEM;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProblemServiceImpl implements ProblemService {

    private final ProblemJpaRepository problemJpaRepository;
    private final ProblemMapper problemMapper;
    private final ProblemQueryRepository problemQueryRepository;

    @Override
    public Page<SearchProblemsResponse> getProblems(Integer page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, CREATE_AT));
        return problemQueryRepository.getProblems(pageable);
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
    public ProblemResponse updateProblem(User user, Integer problemIdx, UpdateProblemRequest updateProblemRequest, MultipartFile file) {
        Problem problem = problemJpaRepository.findById(problemIdx)
                .orElseThrow(() -> new BaseException(NOT_EXIST_PROBLEM));
        if (!problem.getUser().getId().equals(user.getId()) && user.getRole() != Role.ADMIN) {
            throw new BaseException(NOT_AUTHORIZED_PROBLEM);
        }
        if (file.isEmpty()) {
            problemMapper.updateProblemRequestToProblem(updateProblemRequest, problem);
        } else {
            if(FilePath.deleteFile(BASE_DIR_2 + problem.getFilePath())){
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
    public ProblemResponse deleteProblem(User user, Integer problemIdx) {
        Problem problem = problemJpaRepository.findById(problemIdx)
                .orElseThrow(() -> new BaseException(NOT_EXIST_PROBLEM));
        if (!problem.getUser().getId().equals(user.getId()) && user.getRole() != Role.ADMIN) {
            throw new BaseException(NOT_AUTHORIZED_PROBLEM);
        }
        problem.setDeletedAt();
        problem.setState(INACTIVE);
        return problemMapper.problemToProblemResponse(problem);
    }
}
