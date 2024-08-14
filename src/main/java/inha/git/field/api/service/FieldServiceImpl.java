package inha.git.field.api.service;

import inha.git.field.api.controller.request.CreateFieldRequest;
import inha.git.field.api.mapper.FieldMapper;
import inha.git.field.domain.Field;
import inha.git.field.domain.repository.FieldJpaRepository;
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
public class FieldServiceImpl implements FieldService {

    private final FieldJpaRepository fieldJpaRepository;
    private final FieldMapper fieldMapper;

    /**
     * 분야 생성
     *
     * @param createFieldRequest 분야 생성 요청
     * @return 생성된 분야 이름
     */
    @Override
    @Transactional
    public String createField(CreateFieldRequest createFieldRequest) {
        Field field = fieldMapper.createFieldRequestToField(createFieldRequest);
        return fieldJpaRepository.save(field).getName() + " 분야가 생성되었습니다.";
    }
}
