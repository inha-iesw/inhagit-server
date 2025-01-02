package inha.git.admin.api.controller;

import inha.git.admin.api.controller.dto.request.AssistantDemotionRequest;
import inha.git.admin.api.controller.dto.request.AssistantPromotionRequest;
import inha.git.admin.api.controller.dto.request.CompanyAcceptRequest;
import inha.git.admin.api.controller.dto.request.CompanyCancelRequest;
import inha.git.admin.api.service.AdminApproveService;
import inha.git.common.BaseResponse;
import inha.git.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public BaseResponse<String> acceptCompany(@AuthenticationPrincipal User user,
                                              @Validated @RequestBody CompanyAcceptRequest companyAcceptRequest) {
        log.info("기업 승인 - 조교/교수/관리자: {}, 승인할 기업: {}", user.getName(), companyAcceptRequest.userIdx());
        return BaseResponse.of(COMPANY_ACCEPT_OK, adminApproveService.acceptCompany(user,companyAcceptRequest));
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
    public BaseResponse<String> cancelCompany(@AuthenticationPrincipal User user,
                                              @Validated @RequestBody CompanyCancelRequest companyCancelRequest) {
        log.info("기업 승인 취소 - 조교/교수/관리자: {}, 승인 취소할 기업: {}", user.getName(), companyCancelRequest.userIdx());
        return BaseResponse.of(COMPANY_CANCEL_OK, adminApproveService.cancelCompany(user, companyCancelRequest));
    }

    /**
     * 학생 승인 API
     *
     * <p>학생 승인을 합니다.</p>
     *
     * @param assistantPromotionRequest 조교 승인할 유저 인덱스
     * @return 승인된 학생 정보를 포함하는 BaseResponse<String>
     */
    @PostMapping("/promotion")
    @Operation(summary = "조교 승격 API(조교, 교수, 관리자 전용)", description = "학생을 조교로 승격합니다.")
    public BaseResponse<String> promotionStudent(@AuthenticationPrincipal User user,
                                                 @Validated @RequestBody AssistantPromotionRequest assistantPromotionRequest) {
        log.info("조교 승격 - 조교/교수/관리자: {}, 승격할 학생: {}", user.getName(), assistantPromotionRequest.userIdx());
        return BaseResponse.of(ASSISTANT_PROMOTION_OK, adminApproveService.promotionStudent(user, assistantPromotionRequest));
    }

    /**
     * 학생 승인 취소 API
     *
     * <p>학생 승인을 취소합니다.</p>
     *
     * @param assistantDemotionRequest 조교 승인 취소할 유저 인덱스
     * @return 승인 취소된 학생 정보를 포함하는 BaseResponse<String>
     */
    @PostMapping("/demotion")
    @Operation(summary = "조교 승격 취소 API(조교, 교수, 관리자 전용)", description = "조교 승격을 취소합니다.")
    public BaseResponse<String> demotionStudent(@AuthenticationPrincipal User user,
                                                @Validated @RequestBody AssistantDemotionRequest assistantDemotionRequest) {
        log.info("조교 승격 취소 - 조교/교수/관리자: {}, 승격 취소할 학생: {}", user.getName(), assistantDemotionRequest.userIdx());
        return BaseResponse.of(ASSISTANT_PROMOTION_CANCEL_OK, adminApproveService.demotionStudent(user, assistantDemotionRequest));
    }

}
