package FolderSizeExporterWithTree;

import java.io.*;

public class FolderSizeExporterWithTree {

    public static void main(String[] args) {
        String targetPath = "C:\\Users\\user\\Documents";
        File root = new File(targetPath);

        if (!root.exists() || !root.isDirectory()) {
            System.out.println("올바른 폴더 경로가 아닙니다: " + targetPath);
            return;
        }

        File csvFile = new File("folder_size_report.csv");
        try (PrintWriter writer = new PrintWriter(new FileWriter(csvFile))) {
            writer.println("Depth,Folder Path,Size (bytes),Formatted Size");

            // 재귀 탐색 시작
            traverseFolders(root, writer, 0, "");

            System.out.println("\nCSV 파일 생성 완료: " + csvFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 폴더 순회 (재귀)
     * @param folder 현재 폴더
     * @param writer CSV 출력
     * @param depth 깊이
     * @param prefix 트리 구조 표시용 접두사
     */
    private static void traverseFolders(File folder, PrintWriter writer, int depth, String prefix) {
        long size = getFolderSize(folder);

        // CSV 기록
        writer.printf("%d,\"%s\",%d,%s%n", depth, folder.getAbsolutePath(), size, formatSize(size));

        // 콘솔 출력 (트리 형태)
        if (depth == 0) {
            System.out.printf("%s%s [%s]%n", prefix, folder.getName(), formatSize(size));
        } else {
            System.out.printf("%s├── %s [%s]%n", prefix, folder.getName(), formatSize(size));
        }

        File[] subFolders = folder.listFiles(File::isDirectory);
        if (subFolders == null) return;

        // 마지막 하위 폴더인지 체크
        for (int i = 0; i < subFolders.length; i++) {
            File sub = subFolders[i];
            boolean isLast = (i == subFolders.length - 1);
            String newPrefix = prefix + (depth == 0 ? "" : (isLast ? "    " : "│   "));

            traverseFolders(sub, writer, depth + 1, newPrefix);
        }
    }

    /** 폴더 전체 크기 계산 (재귀) */
    private static long getFolderSize(File folder) {
        long total = 0;
        File[] files = folder.listFiles();
        if (files == null) return 0;

        for (File f : files) {
            if (f.isFile()) total += f.length();
            else total += getFolderSize(f);
        }
        return total;
    }

    /** 보기 좋은 용량 단위 변환 */
    private static String formatSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "B";
        return String.format("%.2f %s", bytes / Math.pow(1024, exp), pre);
    }
}
