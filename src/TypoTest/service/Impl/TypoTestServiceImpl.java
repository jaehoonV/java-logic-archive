package TypoTest.service.Impl;

import TypoTest.service.TypoTestService;
import Utils.HangulUtils;

import java.util.*;

public class TypoTestServiceImpl implements TypoTestService {

    @Override
    public String correctSentence(String input, Map<String, Integer> freqMap, double alpha, double beta) {
        String[] words = input.split("\\s+");
        List<String> correctedWords = new ArrayList<>();

        for (String word : words) {
            correctedWords.add(correctWord(word, freqMap, alpha, beta));
        }

        return String.join(" ", correctedWords);
    }

    @Override
    public String correctWord(String word, Map<String, Integer> freqMap, double alpha, double beta) {
        String bestMatch = word;
        double bestScore = -1.0;

        // 사전 단어들
        Set<String> dictionary = freqMap.keySet();
        int maxFreq = Collections.max(freqMap.values());

        for (String dictWord : dictionary) {
            int distance = getSmartEditDistance(word, dictWord);
            int frequency = freqMap.get(dictWord);

            // 거리 기반 유사도 (0~1)
            double simByDistance = 1.0 / (1 + distance);
            // 빈도 기반 유사도 (0~1)
            double simByFreq = (double) frequency / maxFreq;

            // 최종 점수 (가중 평균)
            double score = alpha * simByDistance + beta * simByFreq;

            if (score > bestScore) {
                bestScore = score;
                bestMatch = dictWord;
            }
        }

        return bestMatch;
    }

    @Override
    public int getEditDistance(String s1, String s2) {
        int n = s1.length();
        int m = s2.length();
        int[][] dp = new int[n + 1][m + 1];

        for (int i = 0; i <= n; i++) dp[i][0] = i;
        for (int j = 0; j <= m; j++) dp[0][j] = j;

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(
                            Math.min(dp[i - 1][j], dp[i][j - 1]),
                            dp[i - 1][j - 1]
                    );
                }
            }
        }
        return dp[n][m];
    }

    @Override
    public int getSmartEditDistance(String s1, String s2) {
        if (HangulUtils.containsHangul(s1) || HangulUtils.containsHangul(s2)) {
            s1 = HangulUtils.decomposeHangul(s1);
            s2 = HangulUtils.decomposeHangul(s2);
        }

        return getEditDistance(s1, s2);
    }

}
