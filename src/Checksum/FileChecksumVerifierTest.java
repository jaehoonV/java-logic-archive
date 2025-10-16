package Checksum;

public class FileChecksumVerifierTest {
    public static void main(String[] args) {
        FileChecksumVerifier verifier = new FileChecksumVerifier();

        String filePath = "C:/test/sample.txt";

        // 파일 체크섬 계산
        String checksum = verifier.getChecksum(filePath, ChecksumAlgorithm.SHA256);
        System.out.println("SHA-256 checksum: " + checksum);

        // 무결성 검증
        boolean valid = verifier.verify(filePath, checksum, ChecksumAlgorithm.SHA256);
        System.out.println("무결성 검증 결과: " + valid);
    }
}
