package inha.git.field.api.service;

import inha.git.common.exceptions.BaseException;
import inha.git.field.api.controller.dto.request.CreateFieldRequest;
import inha.git.field.api.controller.dto.request.UpdateFieldRequest;
import inha.git.field.api.controller.dto.response.SearchFieldResponse;
import inha.git.field.api.mapper.FieldMapper;
import inha.git.field.domain.Field;
import inha.git.field.domain.repository.FieldJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static inha.git.common.BaseEntity.State.ACTIVE;
import static inha.git.common.BaseEntity.State.INACTIVE;
import static inha.git.common.code.status.ErrorStatus.FIELD_NOT_FOUND;

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
     * 분야 전체 조회
     *
     * @return 분야 전체 조회 결과
     */
    @Override
    public List<SearchFieldResponse> getFields() {
        return fieldMapper.fieldsToSearchFieldResponses(fieldJpaRepository.findAllByState(ACTIVE));
    }

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

    /**
     * 분야 이름 변경
     *
     * @param fieldIdx 분야 인덱스
     * @param updateFieldRequest 분야 이름 변경 요청
     * @return 변경된 분야 이름
     */
    @Override
    public String updateField(Integer fieldIdx, UpdateFieldRequest updateFieldRequest) {
        Field field = fieldJpaRepository.findByIdAndState(fieldIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(FIELD_NOT_FOUND));
        field.setName(updateFieldRequest.name());
        return field.getName() + " 분야가 수정되었습니다.";
    }

    /**
     * 분야 삭제
     *
     * @param fieldIdx 분야 인덱스
     * @return 삭제된 분야 이름
     */
    @Override
    @Transactional
    public String deleteField(Integer fieldIdx) {
        Field field = fieldJpaRepository.findByIdAndState(fieldIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(FIELD_NOT_FOUND));
        field.setState(INACTIVE);
        field.setDeletedAt();
        return field.getName() + " 분야가 삭제되었습니다.";
    }
}
