package inha.git.problem.api.service;

import inha.git.problem.api.controller.dto.request.CreateProblemRequest;
import inha.git.problem.api.controller.dto.response.ProblemResponse;
import inha.git.problem.api.mapper.ProblemMapper;
import inha.git.problem.domain.Problem;
import inha.git.problem.domain.repository.ProblemJpaRepository;
import inha.git.user.domain.User;
import inha.git.utils.file.FilePath;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import static inha.git.common.Constant.PROBLEM_FILE;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProblemServiceImpl implements ProblemService {

    private final ProblemJpaRepository problemJpaRepository;
    private final ProblemMapper problemMapper;

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
}
