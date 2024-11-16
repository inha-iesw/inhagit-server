package inha.git.utils;

import inha.git.common.exceptions.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static inha.git.common.Constant.IDEMPOTENT;
import static inha.git.common.Constant.TIME_LIMIT;
import static inha.git.common.code.status.ErrorStatus.DUPLICATION_REQUEST;

/**
 * IdempotentProvider는 Idempotency를 제공하는 서비스 클래스.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IdempotentProvider {

    private final RedisProvider redisProvider;

    /**
     * Idempotency 키의 유효성을 검증하는 메서드.
     *
     * @param keyElement 키를 구성하는 요소 리스트
     */
    public void isValidIdempotent(List<String> keyElement) {
        String idempotentKey = this.compactKey(keyElement);

        if (redisProvider.getValueOps(idempotentKey) == null) {
            redisProvider.setDataExpire(idempotentKey, IDEMPOTENT, TIME_LIMIT);
        } else {
            log.error("Idempotency key is duplicated. key: {}", idempotentKey);
            throw new BaseException(DUPLICATION_REQUEST);
        }
    }

    /**
     * 주어진 요소들을 조합하여 Idempotency 키 생성.
     *
     * @param keyElement 키 요소 리스트
     * @return 조합된 Idempotency 키
     */
    private String compactKey(List<String> keyElement) {
        return String.join("", keyElement);
    }
}
