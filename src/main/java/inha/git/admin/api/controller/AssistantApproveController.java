package inha.git.admin.api.controller;

import inha.git.admin.api.controller.dto.request.*;
import inha.git.admin.api.service.AdminApproveService;
import inha.git.common.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static inha.git.common.code.status.SuccessStatus.*;

/**
 * AssistantApproveController는 조교/교수/관리자 전용 계정 조회 관련 엔드포인트를 처리.
 */
@Slf4j
@Tag(name = "assistant approve controller", description = "assistant approve 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/assistant")
public class AssistantApproveController {

    private final AdminApproveService adminApproveService;

    /**
     * 기업 승인 API
     *
     * <p>기업 승인을 합니다.</p>
     *
     * @param companyAcceptRequest 기업 승인할 유저 인덱스
     * @return 승인된 기업 정보를 포함하는 BaseResponse<String>
     */
    @PostMapping("/company/accept")
    @Operation(summary = "기업 승인 API(조교, 교수, 관리자 전용)", description = "기업 승인을 합니다.")
    public BaseResponse<String> acceptCompany(@Validated @RequestBody CompanyAcceptRequest companyAcceptRequest) {
        return BaseResponse.of(COMPANY_ACCEPT_OK, adminApproveService.acceptCompany(companyAcceptRequest));
    }

    /**
     * 기업 승인 취소 API
     *
     * <p>기업 승인을 취소합니다.</p>
     *
     * @param companyCancelRequest 기업 승인 취소할 유저 인덱스
     * @return 승인 취소된 기업 정보를 포함하는 BaseResponse<String>
     */
    @PostMapping("/company/cancel")
    @Operation(summary = "기업 승인 취소 API(조교, 교수, 관리자 전용)", description = "기업 승인을 취소합니다.")
    public BaseResponse<String> cancelCompany(@Validated @RequestBody CompanyCancelRequest companyCancelRequest) {
        return BaseResponse.of(COMPANY_CANCEL_OK, adminApproveService.cancelCompany(companyCancelRequest));
    }

}
