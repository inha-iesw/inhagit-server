package inha.git.utils;

import inha.git.common.exceptions.BaseException;

import static inha.git.common.code.status.ErrorStatus.INVALID_PAGE;
import static inha.git.common.code.status.ErrorStatus.UTILITY_CLASS;

/**
 * 페이징 처리를 위한 유틸리티 클래스입니다.
 * 페이지 번호와 크기의 유효성 검증 및 변환 기능을 제공합니다.
 */
public class PagingUtils {

    private static final Integer MIN_PAGE = 1;
    private static final Integer MIN_SIZE = 1;

    private PagingUtils() {
        throw new BaseException(UTILITY_CLASS);
    }

    /**
     * 페이지 번호와 크기의 유효성을 검증합니다.
     *
     * @param page 검증할 페이지 번호
     * @param size 검증할 페이지 크기
     * @throws BaseException INVALID_PAGE: 페이지 번호가 최소값보다 작은 경우
     *                      INVALID_SIZE: 페이지 크기가 최소값보다 작은 경우
     */
    public static void validatePage(Integer page, Integer size) {
        if (page < MIN_PAGE) {
            throw new BaseException(INVALID_PAGE);
        }
        if (size < MIN_SIZE) {
            throw new BaseException(INVALID_PAGE);
        }
    }

    /**
     * 페이지 번호의 유효성을 검증합니다.
     *
     * @param page 검증할 페이지 번호
     * @throws BaseException INVALID_PAGE: 페이지 번호가 최소값보다 작은 경우
     */
    public static void validatePage(Integer page) {
        if (page < MIN_PAGE) {
            throw new BaseException(INVALID_PAGE);
        }
    }

    /**
     * 사용자가 입력한 페이지 번호를 인덱스로 변환합니다.
     * (예: 페이지 1 → 인덱스 0)
     *
     * @param page 변환할 페이지 번호
     * @return 변환된 페이지 인덱스
     */
    public static Integer toPageIndex(Integer page) {
        return page - 1;
    }
}
