package inha.git.utils;

import inha.git.common.exceptions.BaseException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static inha.git.common.Constant.IDEMPOTENT;
import static inha.git.common.Constant.TIME_LIMIT;
import static inha.git.common.code.status.ErrorStatus.DUPLICATION_REQUEST;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@DisplayName("IdempotentProvider 테스트")
@ExtendWith(MockitoExtension.class)
class IdempotentProviderTest {

    @InjectMocks
    private IdempotentProvider idempotentProvider;

    @Mock
    private RedisProvider redisProvider;


    @Test
    @DisplayName("유효한 Idempotency 키 검증 성공")
    void isValidIdempotent_Success() {
        // given
        List<String> keyElements = Arrays.asList("createRequest", "1", "사용자", "제목", "내용");
        String expectedKey = "createRequest1사용자제목내용";

        given(redisProvider.getValueOps(expectedKey))
                .willReturn(null);

        // when
        idempotentProvider.isValidIdempotent(keyElements);

        // then
        verify(redisProvider).getValueOps(expectedKey);
        verify(redisProvider).setDataExpire(expectedKey, IDEMPOTENT, TIME_LIMIT);
    }

    //@Test
    @DisplayName("중복된 Idempotency 키 검증 실패")
    void isValidIdempotent_Duplicated_ThrowsException() {
        // given
        List<String> keyElements = Arrays.asList("createRequest", "1", "사용자", "제목", "내용");
        String expectedKey = "createRequest1사용자제목내용";

        given(redisProvider.getValueOps(expectedKey))
                .willReturn(IDEMPOTENT);

        // when & then
        assertThatThrownBy(() -> idempotentProvider.isValidIdempotent(keyElements))
                .isInstanceOf(BaseException.class)
                .hasFieldOrPropertyWithValue("errorStatus", DUPLICATION_REQUEST);

        verify(redisProvider).getValueOps(expectedKey);
        verify(redisProvider, never()).setDataExpire(any(), any(), any());
    }

    @Test
    @DisplayName("빈 키 요소 리스트로 검증 시도")
    void isValidIdempotent_EmptyKeyElements() {
        // given
        List<String> emptyKeyElements = Collections.emptyList();
        String expectedKey = "";

        given(redisProvider.getValueOps(expectedKey))
                .willReturn(null);

        // when
        idempotentProvider.isValidIdempotent(emptyKeyElements);

        // then
        verify(redisProvider).getValueOps(expectedKey);
        verify(redisProvider).setDataExpire(expectedKey, IDEMPOTENT, TIME_LIMIT);
    }

    //@Test
    @DisplayName("null 값이 포함된 키 요소로 검증 시도")
    void isValidIdempotent_WithNullElement() {
        // given
        List<String> keyElements = Arrays.asList("createRequest", null, "사용자", "제목", "내용");
        String expectedKey = "createRequest사용자제목내용";  // null은 빈 문자열로 처리됨

        given(redisProvider.getValueOps(expectedKey))
                .willReturn(null);

        // when
        idempotentProvider.isValidIdempotent(keyElements);

        // then
        verify(redisProvider).getValueOps(expectedKey);
        verify(redisProvider).setDataExpire(expectedKey, IDEMPOTENT, TIME_LIMIT);
    }
}