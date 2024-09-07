package inha.git.college.service;

import inha.git.college.domain.repository.CollegeJpaRepository;
import inha.git.college.mapper.CollegeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * FieldServiceImpl는 FieldService 인터페이스를 구현하는 클래스.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CollegeServiceImpl implements CollegeService {

    private final CollegeJpaRepository fieldJpaRepository;
    private final CollegeMapper fieldMapper;

}
