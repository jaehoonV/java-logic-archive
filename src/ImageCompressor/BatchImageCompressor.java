package ImageCompressor;

import java.io.*;

public class BatchImageCompressor {

    public static void main(String[] args) throws IOException {
        File inputDir = new File("src/imageCompressor/image");
        File outputDir = new File("src/imageCompressor/compressedImage");

        // compressImage 폴더가 없으면 생성
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        // image 폴더 안 모든 jpg/jpeg 파일 가져오기
        File[] imageFiles = inputDir.listFiles((dir, name) -> {
            String lower = name.toLowerCase();
            return lower.endsWith(".jpg") || lower.endsWith(".jpeg");
        });

        if (imageFiles == null || imageFiles.length == 0) {
            System.out.println("압축할 이미지 파일이 없습니다.");
            return;
        }

        // 반복 처리
        for (File imgFile : imageFiles) {
            System.out.println("처리 중: " + imgFile.getName());

            File outFile = new File(outputDir, imgFile.getName());

            try {
                ImageCompressor.compressJpeg(imgFile, outFile, 0, 0, 0.7f);
                System.out.println("완료: " + outFile.getPath());
            } catch (Exception e) {
                System.out.println("압축 실패: " + imgFile.getName());
                e.printStackTrace();
            }
        }

        System.out.println("모든 이미지 처리 완료!");
    }
}
