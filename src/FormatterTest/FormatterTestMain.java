package FormatterTest;

import Utils.FormatterUtils;

public class FormatterTestMain {
    public static void main(String[] args) {
        System.out.println("FormatterTestMain");

        String date_1 = "2025-09-30";
        String date_2 = "2025-09-31";
        String date_3 = "20250930";

        System.out.println("\nisValidDate(String) : \"uuuu-MM-dd\" 형식이면 true 반환");
        System.out.println("Value = " + date_1 + " / isValidDate(\"" + date_1 + "\") = " + FormatterUtils.isValidDate(date_1));
        System.out.println("Value = " + date_2 + " / isValidDate(\"" + date_2 + "\") = " + FormatterUtils.isValidDate(date_2));
        System.out.println("Value = " + date_3 + " / isValidDate(\"" + date_3 + "\") = " + FormatterUtils.isValidDate(date_3));

        String dateTime_1 = "2025-09-30 12:30:30";
        String dateTime_2 = "2025-09-31 12:30:30";
        String dateTime_3 = "2025-09-30 12:30:80";

        System.out.println("\nisValidDateTime(String) : \"uuuu-MM-dd HH:mm:ss\" 형식이면 true 반환");
        System.out.println("Value = " + dateTime_1 + " / isValidDateTime(\"" + dateTime_1 + "\") = " + FormatterUtils.isValidDateTime(dateTime_1));
        System.out.println("Value = " + dateTime_2 + " / isValidDateTime(\"" + dateTime_2 + "\") = " + FormatterUtils.isValidDateTime(dateTime_2));
        System.out.println("Value = " + dateTime_3 + " / isValidDateTime(\"" + dateTime_3 + "\") = " + FormatterUtils.isValidDateTime(dateTime_3));

        String double_1 = "2.12";
        String double_2 = "2.a1";
        String double_3 = ".12";
        String double_4 = "4.32";

        System.out.println("\nisValidDouble(String) : Double 형식으로 변환이 가능하면 true 반환");
        System.out.println("Value = " + double_1 + " / isValidDouble(\"" + double_1 + "\") = " + FormatterUtils.isValidDouble(double_1));
        System.out.println("Value = " + double_2 + " / isValidDouble(\"" + double_2 + "\") = " + FormatterUtils.isValidDouble(double_2));
        System.out.println("Value = " + double_3 + " / isValidDouble(\"" + double_3 + "\") = " + FormatterUtils.isValidDouble(double_3));

        System.out.println("\nisValidDoubleRange(String, double, double) : Double 형식으로 변한 가능하고, min <= 값 <= max 범위이면 true 반환");
        System.out.println("Value = " + double_1 + " / isValidDoubleRange(\"" + double_1 + "\", 1, 4) = " + FormatterUtils.isValidDoubleRange(double_1, 1, 4));
        System.out.println("Value = " + double_4 + " / isValidDoubleRange(\"" + double_4 + "\", 1, 4) = " + FormatterUtils.isValidDoubleRange(double_4, 1, 4));


        String Integer_1 = "3";
        String Integer_2 = "3.1";
        String Integer_3 = "a";
        String Integer_4 = "5";

        System.out.println("\nisValidInteger(String) : Integer 형식으로 변환이 가능하면 true 반환");
        System.out.println("Value = " + Integer_1 + " / isValidInteger(\"" + Integer_1 + "\") = " + FormatterUtils.isValidInteger(Integer_1));
        System.out.println("Value = " + Integer_2 + " / isValidInteger(\"" + Integer_2 + "\") = " + FormatterUtils.isValidInteger(Integer_2));
        System.out.println("Value = " + Integer_3 + " / isValidInteger(\"" + Integer_3 + "\") = " + FormatterUtils.isValidInteger(Integer_3));

        System.out.println("\nisValidIntegerRange(String, int, int) : Integer 형식으로 변환 가능하고, min <= 값 <= max 범위이면 true 반환");
        System.out.println("Value = " + Integer_1 + " / isValidIntegerRange(\"" + Integer_1 + "\", 1, 4) = " + FormatterUtils.isValidIntegerRange(Integer_1, 1, 4));
        System.out.println("Value = " + Integer_4 + " / isValidIntegerRange(\"" + Integer_4 + "\", 1, 4) = " + FormatterUtils.isValidIntegerRange(Integer_4, 1, 4));

        String Long_1 = "3";
        String Long_2 = "3.1";
        String Long_3 = "a";

        System.out.println("\nisValidLong(String) : Long 형식으로 변환이 가능하면 true 반환");
        System.out.println("Value = " + Long_1 + " / isValidLong(\"" + Long_1 + "\") = " + FormatterUtils.isValidLong(Long_1));
        System.out.println("Value = " + Long_2 + " / isValidLong(\"" + Long_2 + "\") = " + FormatterUtils.isValidLong(Long_2));
        System.out.println("Value = " + Long_3 + " / isValidLong(\"" + Long_3 + "\") = " + FormatterUtils.isValidLong(Long_3));

        String Email_1 = "abc123@abc.com";
        String Email_2 = "abc123abc.com";
        String Email_3 = "abc123@abccom";
        String Email_4 = "@abc.com";

        System.out.println("\nisValidEmail(String) : 이메일 형식이면 true 반환");
        System.out.println("Value = " + Email_1 + " / isValidEmail(\"" + Email_1 + "\") = " + FormatterUtils.isValidEmail(Email_1));
        System.out.println("Value = " + Email_2 + " / isValidEmail(\"" + Email_2 + "\") = " + FormatterUtils.isValidEmail(Email_2));
        System.out.println("Value = " + Email_3 + " / isValidEmail(\"" + Email_3 + "\") = " + FormatterUtils.isValidEmail(Email_3));
        System.out.println("Value = " + Email_4 + " / isValidEmail(\"" + Email_4 + "\") = " + FormatterUtils.isValidEmail(Email_4));

        String PhoneNumber_1 = "010-2233-4455";
        String PhoneNumber_2 = "02-2233-4455";
        String PhoneNumber_3 = "aaa-bbb-ccc";
        String PhoneNumber_4 = "010-2233-445566";

        System.out.println("\nisValidPhoneNumber(String) : \"010-1234-5678\" 또는 \"02-123-4567\" 형식이면 true 반환");
        System.out.println("Value = " + PhoneNumber_1 + " / isValidPhoneNumber(\"" + PhoneNumber_1 + "\") = " + FormatterUtils.isValidPhoneNumber(PhoneNumber_1));
        System.out.println("Value = " + PhoneNumber_2 + " / isValidPhoneNumber(\"" + PhoneNumber_2 + "\") = " + FormatterUtils.isValidPhoneNumber(PhoneNumber_2));
        System.out.println("Value = " + PhoneNumber_3 + " / isValidPhoneNumber(\"" + PhoneNumber_3 + "\") = " + FormatterUtils.isValidPhoneNumber(PhoneNumber_3));
        System.out.println("Value = " + PhoneNumber_4 + " / isValidPhoneNumber(\"" + PhoneNumber_4 + "\") = " + FormatterUtils.isValidPhoneNumber(PhoneNumber_4));

    }
}
