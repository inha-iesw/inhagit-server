package inha.git.field.api.mapper;

import inha.git.field.api.controller.request.CreateFieldRequest;
import inha.git.field.domain.Field;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * FieldMapper는 Field 엔티티와 관련된 데이터 변환 기능을 제공.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FieldMapper {

    /**
     * CreateFieldRequest를 Field 엔티티로 변환
     *
     * @param createFieldRequest 분야 생성 요청
     * @return Field 엔티티
     */
    Field createFieldRequestToField(CreateFieldRequest createFieldRequest);

}
