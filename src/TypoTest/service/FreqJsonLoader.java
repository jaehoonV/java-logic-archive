package TypoTest.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class FreqJsonLoader {
    public static Map<String, Integer> loadFreqData(String jsonPath) {
        Map<String, Integer> freqMap = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(jsonPath))) {
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                sb.append(line.trim());
            }

            String json = sb.toString();

            // 양쪽 중괄호 제거
            json = json.substring(1, json.length() - 1).trim();

            // 항목 단위로 분리
            String[] entries = json.split(",");

            for (String entry : entries) {
                String[] parts = entry.split(":");
                if (parts.length == 2) {
                    String key = parts[0].trim().replaceAll("^\"|\"$", "");
                    String valueStr = parts[1].trim();

                    // 숫자 부분만 추출
                    int value = Integer.parseInt(valueStr.replaceAll("[^0-9]", ""));
                    freqMap.put(key, value);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return freqMap;
    }
}
