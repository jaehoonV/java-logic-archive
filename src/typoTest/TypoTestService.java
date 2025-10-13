package typoTest;

import java.util.Map;

public interface TypoTestService {

    String correctSentence(String input, Map<String, Integer> freqMap, double alpha, double beta);

    String correctWord(String word, Map<String, Integer> freqMap, double alpha, double beta);

    int getEditDistance(String s1, String s2);
}
