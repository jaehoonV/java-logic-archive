import java.util.HashMap;
import java.util.Map;

import typoTest.Impl.TypoTestServiceImpl;
import typoTest.TypoTestService;

public class Main {

    public static void main(String[] args) {
        System.out.printf("Hello and welcome!");

        for (int i = 1; i <= 5; i++) {
            System.out.println("i = " + i);
        }

        TypoTestService typoTestService = new TypoTestServiceImpl();

        Map<String, Integer> freqMap = new HashMap<>();
        freqMap.put("불광", 1200);
        freqMap.put("골목집", 900);
        freqMap.put("고기집", 600);
        freqMap.put("고로케집", 150);
        freqMap.put("홍대", 1000);
        freqMap.put("감자탕", 800);

        String input = "불광 고록집";
        String input2 = "불광 고오케집";

        // 거리 90% + 빈도 10%
        String corrected = typoTestService.correctSentence(input, freqMap, 0.9, 0.1);
        String corrected2 = typoTestService.correctSentence(input2, freqMap, 0.9, 0.1);

        System.out.println("입력어: " + input);
        System.out.println("교정 결과: " + corrected);

        System.out.println("입력어2: " + input2);
        System.out.println("교정 결과2: " + corrected2);
    }
}