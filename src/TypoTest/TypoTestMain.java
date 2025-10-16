package TypoTest;

import TypoTest.service.Impl.TypoTestServiceImpl;
import TypoTest.service.FreqJsonLoader;
import TypoTest.service.TypoTestService;

import java.util.Map;
import java.util.Scanner;

public class TypoTestMain {

    public static void main(String[] args) {

        TypoTestService typoTestService = new TypoTestServiceImpl();

        // JSON 파일 경로
        String jsonPath = "src/typoTest/resources/freqData.json";

        // 표준 Java 기반 JSON 로더
        Map<String, Integer> freqMap = FreqJsonLoader.loadFreqData(jsonPath);

        String input = "불광 고록집";
        String input2 = "불광 고오케집";
        String input3 = "물광 고ㅈ집";
        String input4 = "불광역 고기집";

        // 거리 90% + 빈도 10%
        String corrected = typoTestService.correctSentence(input, freqMap, 0.9, 0.1);
        String corrected2 = typoTestService.correctSentence(input2, freqMap, 0.9, 0.1);
        String corrected3 = typoTestService.correctSentence(input3, freqMap, 0.9, 0.1);
        String corrected4 = typoTestService.correctSentence(input4, freqMap, 0.9, 0.1);

        // 거리 50% + 빈도 50%
        String corrected50 = typoTestService.correctSentence(input, freqMap, 0.5, 0.5);
        String corrected50_2 = typoTestService.correctSentence(input2, freqMap, 0.5, 0.5);
        String corrected50_3 = typoTestService.correctSentence(input3, freqMap, 0.5, 0.5);
        String corrected50_4 = typoTestService.correctSentence(input4, freqMap, 0.5, 0.5);

        System.out.println("=== 거리 90% + 빈도 10% ===");
        System.out.println("입력어: " + input);
        System.out.println("교정 결과: " + corrected);
        System.out.println("입력어: " + input2);
        System.out.println("교정 결과: " + corrected2);
        System.out.println("입력어: " + input3);
        System.out.println("교정 결과: " + corrected3);
        System.out.println("입력어: " + input4);
        System.out.println("교정 결과: " + corrected4);

        System.out.println("=== 거리 50% + 빈도 50% ===");
        System.out.println("입력어: " + input);
        System.out.println("교정 결과: " + corrected50);
        System.out.println("입력어: " + input2);
        System.out.println("교정 결과: " + corrected50_2);
        System.out.println("입력어: " + input3);
        System.out.println("교정 결과: " + corrected50_3);
        System.out.println("입력어: " + input4);
        System.out.println("교정 결과: " + corrected50_4);

        Scanner scanner = new Scanner(System.in);

        System.out.println("\n오타 교정 테스트 (종료 : exit)");

        while (true) {
            System.out.print("교정할 문장을 입력하세요: ");
            String scanner_input = scanner.nextLine();

            if ("exit".equalsIgnoreCase(scanner_input)) {
                System.out.println("프로그램을 종료합니다.");
                break;
            }

            // 거리 90% + 빈도 10%
            String correctedHighDistance = typoTestService.correctSentence(scanner_input, freqMap, 0.9, 0.1);

            // 거리 50% + 빈도 50%
            String correctedBalanced = typoTestService.correctSentence(scanner_input, freqMap, 0.5, 0.5);

            System.out.println("\n=== 결과 ===");
            System.out.println("입력어: " + scanner_input);
            System.out.println("거리 90% + 빈도 10% 교정 결과: " + correctedHighDistance);
            System.out.println("거리 50% + 빈도 50% 교정 결과: " + correctedBalanced);
        }

        scanner.close();
    }
}