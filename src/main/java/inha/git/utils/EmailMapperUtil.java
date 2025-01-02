package inha.git.utils;

public class EmailMapperUtil {
    public static String maskEmail(String email) {
        int atIndex = email.indexOf("@");
        if (atIndex <= 1) {
            return email;  // 너무 짧은 경우 마스킹하지 않음
        }

        String prefix = email.substring(0, atIndex);  // @ 앞부분 추출
        int maskLength = prefix.length() / 2;  // 앞부분 절반 길이

        String maskedPrefix = prefix.substring(0, prefix.length() - maskLength)  // 앞부분 절반 남기기
                + "*".repeat(maskLength);  // 나머지 절반을 *로 대체
        String domain = email.substring(atIndex);  // @ 뒷부분 유지

        return maskedPrefix + domain;
    }
}
