package utils;

public class HangulUtils {
    private static final char HANGUL_BASE = 0xAC00;
    private static final char HANGUL_END = 0xD7A3;
    private static final char[] CHO = {
            'ㄱ','ㄲ','ㄴ','ㄷ','ㄸ','ㄹ','ㅁ','ㅂ','ㅃ',
            'ㅅ','ㅆ','ㅇ','ㅈ','ㅉ','ㅊ','ㅋ','ㅌ','ㅍ','ㅎ'
    };
    private static final char[] JUNG = {
            'ㅏ','ㅐ','ㅑ','ㅒ','ㅓ','ㅔ','ㅕ','ㅖ','ㅗ','ㅘ','ㅙ','ㅚ',
            'ㅛ','ㅜ','ㅝ','ㅞ','ㅟ','ㅠ','ㅡ','ㅢ','ㅣ'
    };
    private static final char[] JONG = {
            '\0','ㄱ','ㄲ','ㄳ','ㄴ','ㄵ','ㄶ','ㄷ','ㄹ','ㄺ','ㄻ','ㄼ','ㄽ','ㄾ','ㄿ','ㅀ',
            'ㅁ','ㅂ','ㅄ','ㅅ','ㅆ','ㅇ','ㅈ','ㅊ','ㅋ','ㅌ','ㅍ','ㅎ'
    };

    /**
     * 주어진 문자열에 한글이 포함되어 있는지 확인합니다.
     *
     * @param str 검사할 문자열
     * @return 한글이 포함되어 있으면 true, 아니면 false
     */
    public static boolean containsHangul(String str) {
        return str.chars().anyMatch(c -> c >= HANGUL_BASE && c <= HANGUL_END);
    }

    /**
     * 한글 문자열을 초성, 중성, 종성 단위의 자모 문자열로 분리합니다.
     * 비한글 문자는 그대로 유지됩니다.
     *
     * <p>예시:
     * <pre>
     *     decomposeHangul("재훈 Hoon") → "ㅈㅐㅎㅜㄴ Hoon"
     * </pre>
     *
     * @param str 분리할 문자열
     * @return 자모로 분리된 문자열
     */
    public static String decomposeHangul(String str) {
        StringBuilder sb = new StringBuilder();
        for (char ch : str.toCharArray()) {
            if (ch >= HANGUL_BASE && ch <= HANGUL_END) {
                int code = ch - HANGUL_BASE;
                int cho = code / (21 * 28);
                int jung = (code % (21 * 28)) / 28;
                int jong = code % 28;

                sb.append(CHO[cho]).append(JUNG[jung]);
                if (JONG[jong] != '\0') sb.append(JONG[jong]);
            } else {
                sb.append(ch); // 비한글은 그대로 유지
            }
        }
        return sb.toString();
    }

    /**
     * 초성, 중성, 종성으로 구성된 자모 문자열을 완성형 한글로 합성합니다.
     * 자모가 완성형 조합 규칙에 맞지 않으면 그대로 출력됩니다.
     *
     * <p>예시:
     * <pre>
     *     composeHangul("ㅈㅐㅎㅜㄴ Hoon") → "재훈 Hoon"
     * </pre>
     *
     * @param jamoStr 자모 문자열
     * @return 합성된 완성형 한글 문자열
     */
    public static String composeHangul(String jamoStr) {
        StringBuilder sb = new StringBuilder();
        int length = jamoStr.length();
        int i = 0;

        while (i < length) {
            char ch = jamoStr.charAt(i);

            // 초성인지 확인
            int choIndex = indexOf(CHO, ch);
            if (choIndex != -1 && i + 1 < length) {
                // 중성 확인
                int jungIndex = indexOf(JUNG, jamoStr.charAt(i + 1));
                if (jungIndex != -1) {
                    // 종성 확인 (선택적)
                    int jongIndex = 0;
                    if (i + 2 < length) {
                        jongIndex = indexOf(JONG, jamoStr.charAt(i + 2));
                        if (jongIndex != -1) {
                            i += 3;
                        } else {
                            jongIndex = 0;
                            i += 2;
                        }
                    } else {
                        i += 2;
                    }
                    char composed = (char) (HANGUL_BASE + (choIndex * 21 * 28) + (jungIndex * 28) + jongIndex);
                    sb.append(composed);
                    continue;
                }
            }

            // 초성-중성 조합이 아니면 그대로 추가
            sb.append(ch);
            i++;
        }

        return sb.toString();
    }

    /** 배열 내에서 문자 인덱스 검색 */
    private static int indexOf(char[] arr, char target) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == target) return i;
        }
        return -1;
    }
}
