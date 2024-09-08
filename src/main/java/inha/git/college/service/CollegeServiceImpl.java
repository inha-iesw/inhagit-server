package inha.git.college.service;

import inha.git.college.controller.dto.request.CreateCollegeRequest;
import inha.git.college.controller.dto.request.UpdateCollegeRequest;
import inha.git.college.controller.dto.response.SearchCollegeResponse;
import inha.git.college.domain.College;
import inha.git.college.domain.repository.CollegeJpaRepository;
import inha.git.college.mapper.CollegeMapper;
import inha.git.common.exceptions.BaseException;
import inha.git.statistics.domain.repository.CollegeStatisticsJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static inha.git.common.BaseEntity.State.ACTIVE;
import static inha.git.common.BaseEntity.State.INACTIVE;
import static inha.git.common.code.status.ErrorStatus.COLLEGE_NOT_FOUND;

/**
 * CollegeServiceImpl는 CollegeService 인터페이스를 구현하는 클래스.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CollegeServiceImpl implements CollegeService {

    private final CollegeJpaRepository collegeJpaRepository;
    private final CollegeStatisticsJpaRepository collegeStatisticsJpaRepository;
    private final CollegeMapper collegeMapper;

    /**
     * 단과대 전체 조회
     *
     * @return 단과대 전체 조회 결과
     */
    @Override
    public List<SearchCollegeResponse> getColleges() {
        return collegeMapper.collegesToSearchCollegeResponses(collegeJpaRepository.findAllByState(ACTIVE));
    }

    /**
     * 단과대 생성
     *
     * @param createDepartmentRequest 단과대 생성 요청
     * @return 생성된 단과대 이름
     */
    @Override
    @Transactional
    public String createCollege(CreateCollegeRequest createDepartmentRequest) {
        College college = collegeJpaRepository.save
                (collegeMapper.createCollegeRequestToCollege(createDepartmentRequest));
        collegeStatisticsJpaRepository.save(collegeMapper.toCollegeStatistics(college.getId()));
        return college.getName() + " 단과대가 생성되었습니다.";
    }


    /**
     * 단과대 이름 수정
     *
     * @param collegeIdx 단과대 인덱스
     * @param updateCollegeRequest 단과대 수정 요청
     * @return 수정된 단과대 이름
     */
    @Override
    @Transactional
    public String updateCollegeName(Integer collegeIdx ,UpdateCollegeRequest updateCollegeRequest) {
        College college = collegeJpaRepository.findByIdAndState(collegeIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(COLLEGE_NOT_FOUND));
        college.setName(updateCollegeRequest.name());
        return college.getName() + " 단과대 이름이 변경되었습니다.";
    }

    /**
     * 단과대 삭제
     *
     * @param collegeIdx 단과대 인덱스
     * @return 삭제된 단과대 이름
     */
    @Override
    @Transactional
    public String deleteCollege(Integer collegeIdx) {
        College college = collegeJpaRepository.findByIdAndState(collegeIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(COLLEGE_NOT_FOUND));
        college.setState(INACTIVE);
        college.setDeletedAt();
        return college.getName() + " 단과대가 삭제되었습니다.";
    }
}
