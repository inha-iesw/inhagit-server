package inha.git.field.api.controller;

import inha.git.common.BaseResponse;
import inha.git.common.exceptions.BaseException;
import inha.git.field.api.controller.dto.request.CreateFieldRequest;
import inha.git.field.api.controller.dto.request.UpdateFieldRequest;
import inha.git.field.api.controller.dto.response.SearchFieldResponse;
import inha.git.field.api.service.FieldService;
import inha.git.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static inha.git.common.code.status.SuccessStatus.*;

/**
 * 분야 관련 API를 처리하는 컨트롤러입니다.
 * 분야의 조회, 생성, 수정, 삭제 기능을 제공합니다.
 */
@Slf4j
@Tag(name = "field controller", description = "field 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/fields")
public class FieldController {

    private final FieldService fieldService;

    /**
     * <p>
     * 전체 분야 목록을 조회합니다.<br>
     * 활성화된 모든 분야의 정보를 조회하여 반환합니다.<br>
     * </p>
     *
     * @return 분야 목록을 포함한 응답
     */
    @GetMapping
    @Operation(summary = "분야 전체 조회 API", description = "분야 전체를 조회합니다.")
    public BaseResponse<List<SearchFieldResponse>> getFields() {
        return BaseResponse.of(FIELD_SEARCH_OK, fieldService.getFields());
    }

    /**
     * </p>
     * 새로운 분야를 생성합니다.<br>
     * 관리자 권한을 가진 사용자만 접근 가능합니다.<br>
     * 관리자는 새로운 분야를 생성할 수 있으며, 생성된 분야는 활성화 상태가 됩니다.<br>
     * </p>
     *
     * @param user 현재 인증된 관리자 정보
     * @param createFieldRequest 생성할 분야 정보 (분야명)
     * @return 분야 생성 결과 메시지
     */
    @PostMapping
    @PreAuthorize("hasAuthority('admin:create')")
    @Operation(summary = "분야 생성(관리자 전용) API", description = "분야를 생성합니다.")
    public BaseResponse<String> createField(@AuthenticationPrincipal User user,
                                            @Validated @RequestBody CreateFieldRequest createFieldRequest) {
        log.info("분야 생성 - 관리자: {} 분야명: {}", user.getName(), createFieldRequest.name());
        return BaseResponse.of(FIELD_CREATE_OK, fieldService.createField(user, createFieldRequest));
    }

    /**
     * <p>
     * 분야명을 수정합니다.<br>
     * 관리자 권한을 가진 사용자만 접근 가능합니다.<br>
     * 관리자는 기존 분야의 이름을 새로운 이름으로 변경할 수 있습니다.<br>
     * </p>
     *
     * @param user 현재 인증된 관리자 정보
     * @param fieldIdx 수정할 분야의 식별자
     * @param updateFieldRequest 새로운 분야명
     * @return 분야명 수정 결과 메시지
     * @throws BaseException FIELD_NOT_FOUND: 분야를 찾을 수 없는 경우
     */
    @PutMapping("/{fieldIdx}")
    @PreAuthorize("hasAuthority('admin:update')")
    @Operation(summary = "분야 수정(관리자 전용) API", description = "분야를 수정합니다.")
    public BaseResponse<String> updateField(@AuthenticationPrincipal User user,
                                            @PathVariable("fieldIdx") Integer fieldIdx,
                                            @Validated @RequestBody UpdateFieldRequest updateFieldRequest) {
        log.info("분야 수정 - 관리자: {} 분야명: {}", user.getName(), updateFieldRequest.name());
        return BaseResponse.of(FIELD_UPDATE_OK, fieldService.updateField(user, fieldIdx, updateFieldRequest));
    }

    /**
     * <p>
     * 분야를 삭제(비활성화) 처리합니다.<br>
     * 관리자 권한을 가진 사용자만 접근 가능합니다.<br>
     * 실제 삭제가 아닌 소프트 삭제로 처리됩니다.<br>
     * 삭제된 분야는 비활성화 상태로 변경되며, 삭제 시간이 기록됩니다.<br>
     * </p>
     *
     * @param user 현재 인증된 관리자 정보
     * @param fieldIdx 삭제할 분야의 식별자
     * @return 분야 삭제 결과 메시지
     * @throws BaseException FIELD_NOT_FOUND: 분야를 찾을 수 없는 경우
     */
    @DeleteMapping("/{fieldIdx}")
    @PreAuthorize("hasAuthority('admin:delete')")
    @Operation(summary = "분야 삭제(관리자 전용) API", description = "분야를 삭제합니다.")
    public BaseResponse<String> deleteField(@AuthenticationPrincipal User user,
                                            @PathVariable("fieldIdx") Integer fieldIdx) {
        log.info("분야 삭제 - 관리자: {} 분야명: {}", user.getName(), fieldIdx);
        return BaseResponse.of(FIELD_DELETE_OK, fieldService.deleteField(user, fieldIdx));
    }


}
