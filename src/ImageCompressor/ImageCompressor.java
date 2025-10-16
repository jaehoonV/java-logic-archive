package ImageCompressor;

import javax.imageio.*;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.Iterator;

public class ImageCompressor {
    /**
     * JPEG 압축 + 리사이즈
     *
     * @param inputFile  입력 파일
     * @param outputFile 출력 파일 (확장자 .jpg 로 저장 권장)
     * @param maxWidth   최대 너비 (<=0 이면 원본 유지)
     * @param maxHeight  최대 높이 (<=0 이면 원본 유지)
     * @param quality    JPEG 품질 (0f ~ 1f). 0.75~0.85 권장.
     * @throws IOException
     */
    public static void compressJpeg(File inputFile, File outputFile, int maxWidth, int maxHeight, float quality) throws IOException {
        BufferedImage inputImage = ImageIO.read(inputFile);
        if (inputImage == null) throw new IOException("이미지를 읽을 수 없습니다: " + inputFile);

        BufferedImage resized = resizeKeepAspect(inputImage, maxWidth, maxHeight);

        // Convert to TYPE_INT_RGB (JPEG은 알파 채널을 지원하지 않으므로)
        BufferedImage rgbImage = new BufferedImage(resized.getWidth(), resized.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = rgbImage.createGraphics();
        // 고화질 리샘플링 옵션
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawImage(resized, 0, 0, null);
        g.dispose();

        // ImageWriter 찾기
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
        if (!writers.hasNext()) throw new IllegalStateException("No JPEG writers available");
        ImageWriter writer = writers.next();

        try (OutputStream os = new FileOutputStream(outputFile);
             ImageOutputStream ios = ImageIO.createImageOutputStream(os)) {
            writer.setOutput(ios);
            ImageWriteParam param = writer.getDefaultWriteParam();

            if (param.canWriteCompressed()) {
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(clampQuality(quality));
            }
            IIOImage iioImage = new IIOImage(rgbImage, null, null);
            writer.write(null, iioImage, param);
        } finally {
            writer.dispose();
        }
    }

    /**
     * 이미지를 주어진 최대크기로 비율 유지하여 리사이즈
     */
    public static BufferedImage resizeKeepAspect(BufferedImage src, int maxWidth, int maxHeight) {
        if (maxWidth <= 0 && maxHeight <= 0) return src;

        int ow = src.getWidth();
        int oh = src.getHeight();
        double scale = 1.0;

        if (maxWidth > 0 && maxHeight > 0) {
            scale = Math.min((double) maxWidth / ow, (double) maxHeight / oh);
        } else if (maxWidth > 0) {
            scale = (double) maxWidth / ow;
        } else if (maxHeight > 0) {
            scale = (double) maxHeight / oh;
        }

        if (scale >= 1.0) return src; // 확대하지 않음

        int nw = (int) Math.max(1, Math.round(ow * scale));
        int nh = (int) Math.max(1, Math.round(oh * scale));

        BufferedImage resized = new BufferedImage(nw, nh, src.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : src.getType());
        Graphics2D g = resized.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.drawImage(src, 0, 0, nw, nh, null);
        g.dispose();
        return resized;
    }

    private static float clampQuality(float q) {
        if (Float.isNaN(q)) return 0.85f;
        if (q < 0f) return 0f;
        if (q > 1f) return 1f;
        return q;
    }

    public static void main(String[] args) throws Exception {
        File input = new File("src/imageCompressor/image/input.jpg");
        File outJpeg = new File("src/imageCompressor/image/out_compressed.jpg");

        // JPEG 예: 원본 사이즈, 품질 0.75
        compressJpeg(input, outJpeg, 0, 0, 0.75f);

    }

}
