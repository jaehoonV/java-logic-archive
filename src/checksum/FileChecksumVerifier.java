package checksum;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class FileChecksumVerifier {
    /**
     * 파일의 체크섬을 계산하여 반환합니다.
     */
    public String getChecksum(String filePath, ChecksumAlgorithm algorithm) {
        try {
            return ChecksumUtils.calculateChecksum(filePath, algorithm);
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Checksum 계산 실패: " + e.getMessage(), e);
        }
    }

    /**
     * 원본 해시와 비교하여 파일 무결성을 검증합니다.
     */
    public boolean verify(String filePath, String expectedChecksum, ChecksumAlgorithm algorithm) {
        String actualChecksum = getChecksum(filePath, algorithm);
        return expectedChecksum.equalsIgnoreCase(actualChecksum);
    }
}
