package inha.git.field.api.controller;

import inha.git.common.BaseResponse;
import inha.git.field.api.controller.request.CreateFieldRequest;
import inha.git.field.api.controller.request.UpdateFieldRequest;
import inha.git.field.api.controller.response.SearchFieldResponse;
import inha.git.field.api.service.FieldService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static inha.git.common.code.status.SuccessStatus.*;

/**
 * FieldController는 field 관련 엔드포인트를 처리.
 */
@Slf4j
@Tag(name = "field controller", description = "field 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/fields")
public class FieldController {

    private final FieldService fieldService;

    /**
     * 분야 전체 조회 API
     *
     * <p>분야 전체를 조회합니다.</p>
     *
     * @return 분야 전체 조회 결과를 포함하는 BaseResponse<List<SearchFieldResponse>>
     */
    @GetMapping
    @Operation(summary = "분야 전체 조회 API", description = "분야 전체를 조회합니다.")
    public BaseResponse<List<SearchFieldResponse>> getDepartments() {
        return BaseResponse.of(FIELD_SEARCH_OK, fieldService.getFields());
    }

    /**
     * 분야 생성 API
     *
     * <p>ADMIN계정만 호출 가능 -> 분야를 생성.</p>
     *
     * @param createFieldRequest 분야 생성 요청 정보
     *
     * @return 분야 생성 결과를 포함하는 BaseResponse<String>
     */
    @PostMapping
    @PreAuthorize("hasAuthority('admin:create')")
    @Operation(summary = "분야 생성(관리자 전용)", description = "분야를 생성합니다.")
    public BaseResponse<String> createField(@Validated @RequestBody CreateFieldRequest createFieldRequest) {
        return BaseResponse.of(FIELD_CREATE_OK, fieldService.createField(createFieldRequest));
    }

    /**
     * 분야 수정 API
     *
     * <p>ADMIN계정만 호출 가능 -> 분야를 수정.</p>
     *
     * @param fieldIdx 분야 인덱스
     * @param updateFieldRequest 분야 수정 요청 정보
     *
     * @return 분야 수정 결과를 포함하는 BaseResponse<String>
     */
    @PutMapping("/{fieldIdx}")
    @PreAuthorize("hasAuthority('admin:update')")
    @Operation(summary = "분야 수정(관리자 전용)", description = "분야를 수정합니다.")
    public BaseResponse<String> updateField(@PathVariable("fieldIdx") Integer fieldIdx,
                                            @Validated @RequestBody UpdateFieldRequest updateFieldRequest) {
        return BaseResponse.of(FIELD_UPDATE_OK, fieldService.updateField(fieldIdx, updateFieldRequest));
    }

    @DeleteMapping("/{fieldIdx}")
    @PreAuthorize("hasAuthority('admin:delete')")
    @Operation(summary = "분야 삭제(관리자 전용)", description = "분야를 삭제합니다.")
    public BaseResponse<String> deleteField(@PathVariable("fieldIdx") Integer fieldIdx) {
        return BaseResponse.of(FIELD_DELETE_OK, fieldService.deleteField(fieldIdx));
    }


}
