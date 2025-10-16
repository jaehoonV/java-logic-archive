package Utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

public class FormatterUtils {

    /**
     * <b>Date 형식 검사 메소드</b>
     *
     * <br>- dateStr 값이 "uuuu-MM-dd" 형식이면 true 반환 (uuuu 는 절대적인 연도 값)
     */
    public static boolean isValidDate(String dateStr) {
        if (StringsUtils.isBlank(dateStr)) return false;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT);
        try {
            LocalDate.parse(dateStr, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * <b>DateTime 형식 검사 메소드</b>
     *
     * <br>- dateTimeStr 값이 "uuuu-MM-dd HH:mm:ss" 형식이면 true 반환
     */
    public static boolean isValidDateTime(String dateTimeStr) {
        if (StringsUtils.isBlank(dateTimeStr)) return false;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss")
                .withResolverStyle(ResolverStyle.STRICT);
        try {
            LocalDateTime.parse(dateTimeStr, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * <b>Double 형식 검사 메소드</b>
     *
     * <br>- str 값이 Double 형식으로 변환이 가능하면 true 반환
     */
    public static boolean isValidDouble(String str) {
        if (StringsUtils.isBlank(str)) return false;
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * <b>Integer 형식 검사 메소드</b>
     *
     * <br>- str 값이 Integer 형식으로 변환이 가능하면 true 반환
     */
    public static boolean isValidInteger(String str) {
        if (StringsUtils.isBlank(str)) return false;
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * <b>숫자 범위 검사 메소드</b>
     *
     * <br>- str 값이 Double 형식으로 변한 가능하고, min <= 값 <= max 범위이면 true 반환
     */
    public static boolean isValidDoubleRange(String str, double min, double max) {
        if (StringsUtils.isBlank(str)) return false;
        try {
            double val = Double.parseDouble(str);
            return val >= min && val <= max;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * <b>Integer 범위 검사 메소드</b>
     *
     * <br>- str 값이 Integer 형식으로 변환 가능하고, min <= 값 <= max 범위이면 true 반환
     */
    public static boolean isValidIntegerRange(String str, int min, int max) {
        if (StringsUtils.isBlank(str)) return false;
        try {
            int val = Integer.parseInt(str);
            return val >= min && val <= max;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * <b>Long 형식 검사 메소드</b>
     *
     * <br>- str 값이 Long 형식으로 변환이 가능하면 true 반환
     */
    public static boolean isValidLong(String str) {
        if (StringsUtils.isBlank(str)) return false;
        try {
            Long.parseLong(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * <b>Email 형식 검사 메소드</b>
     *
     * <br>- str 값이 이메일 형식이면 true 반환
     */
    public static boolean isValidEmail(String str) {
        if (StringsUtils.isBlank(str)) return false;
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return str.matches(regex);
    }

    /**
     * <b>Phone Number 형식 검사 메소드</b>
     *
     * <br>- str 값이 "010-1234-5678" 또는 "02-123-4567" 형식이면 true 반환
     */
    public static boolean isValidPhoneNumber(String str) {
        if (StringsUtils.isBlank(str)) return false;
        String regex = "^(\\d{2,3}-\\d{3,4}-\\d{4})$";
        return str.matches(regex);
    }

}
