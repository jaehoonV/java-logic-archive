package FolderSizeExporterWithTree;

import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMTArcDarkIJTheme;


import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class FolderSizeExporterGUI {

    private static JTextArea textArea;
    private static JProgressBar progressBar;
    private static JButton startButton;
    private static JButton stopButton;
    private static JFileChooser chooser;
    private static JComboBox<String> sortComboBox;
    private static File selectedFolder;
    private static ForkJoinPool pool;

    private static volatile boolean stopRequested = false;
    private static int totalFolders = 0;
    private static int processedFolders = 0;

    public static void main(String[] args) {
        try {
            FlatMTArcDarkIJTheme.setup();
            UIManager.put("Component.arc", 10);
            UIManager.put("Button.arc", 10);
            UIManager.put("ProgressBar.arc", 10);
            UIManager.put("TextComponent.arc", 10);
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(FolderSizeExporterGUI::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("📁 폴더 용량 분석기");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(950, 700);
        frame.setLocationRelativeTo(null);

        // 텍스트 영역
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Malgun Gothic", Font.PLAIN, 13));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ProgressBar
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setPreferredSize(new Dimension(frame.getWidth(), 25));

        // Buttons
        JButton selectButton = new JButton("📂 폴더 선택");
        startButton = new JButton("🔍 분석 시작");
        stopButton = new JButton("⛔ 중단");
        startButton.setEnabled(false);
        stopButton.setEnabled(false);

        sortComboBox = new JComboBox<>(new String[]{"용량 (내림차순)", "이름 (오름차순)", "수정일 (최신순)"});
        sortComboBox.setFocusable(false);

        JLabel sortLabel = new JLabel("정렬 기준:");
        sortLabel.setForeground(UIManager.getColor("Label.foreground"));

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        controlPanel.add(selectButton);
        controlPanel.add(startButton);
        controlPanel.add(stopButton);
        controlPanel.add(sortLabel);
        controlPanel.add(sortComboBox);

        frame.add(controlPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(progressBar, BorderLayout.SOUTH);

        chooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        // 이벤트 등록
        selectButton.addActionListener(e -> {
            int result = chooser.showOpenDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                selectedFolder = chooser.getSelectedFile();
                textArea.setText("");
                textArea.append("선택된 폴더: " + selectedFolder.getAbsolutePath() + "\n");
                startButton.setEnabled(true);
            }
        });

        startButton.addActionListener(e -> {
            stopRequested = false;
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            analyzeFolder();
        });

        stopButton.addActionListener(e -> {
            stopRequested = true;
            if (pool != null) pool.shutdownNow();
            SwingUtilities.invokeLater(() -> {
                textArea.append("\n⛔ 분석이 중단되었습니다.\n");
                progressBar.setValue(0);
                startButton.setEnabled(true);
                stopButton.setEnabled(false);
            });
        });

        frame.setVisible(true);
    }

    private static void analyzeFolder() {
        new Thread(() -> {
            try {
                SwingUtilities.invokeLater(() -> {
                    textArea.append("분석 시작...\n");
                    progressBar.setValue(0);
                });

                processedFolders = 0;
                pool = new ForkJoinPool();

                long startTime = System.currentTimeMillis();

                // 폴더 개수 미리 세기 (진행률 계산용)
                totalFolders = countFolders(selectedFolder);

                Map<File, Long> folderSizes = new ConcurrentHashMap<>();
                pool.invoke(new FolderSizeTask(selectedFolder, folderSizes));

                if (stopRequested) return;

                long totalSize = folderSizes.getOrDefault(selectedFolder, 0L);

                // CSV 생성
                File downloadFolder = new File(System.getProperty("user.home"), "Downloads");
                File csvFile = new File(downloadFolder, "folder_size_report.csv");

                try (OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(csvFile), StandardCharsets.UTF_8);
                     BufferedWriter bw = new BufferedWriter(osw);
                     PrintWriter writer = new PrintWriter(bw)) {

                    writer.write('\uFEFF'); // BOM 추가
                    writer.println("Depth,Folder Path,Size (bytes),Formatted Size,Last Modified");
                    traverseAndWrite(selectedFolder, writer, 0, folderSizes);
                    SwingUtilities.invokeLater(() -> progressBar.setValue(100));
                }

                // 완료 메시지
                SwingUtilities.invokeLater(() -> {
                    textArea.append("\n총 폴더 수: " + totalFolders + "개\n");
                    textArea.append("총 용량: " + formatSize(totalSize) + "\n");
                    textArea.append("CSV 파일 생성 완료: " + csvFile.getAbsolutePath() + "\n");
                    textArea.append(String.format("분석 완료 (%.2f초)\n", (System.currentTimeMillis() - startTime) / 1000.0));
                    progressBar.setValue(100);
                    startButton.setEnabled(true);
                    stopButton.setEnabled(false);
                });

            } catch (Exception ex) {
                ex.printStackTrace();
                SwingUtilities.invokeLater(() ->
                        textArea.append("오류 발생: " + ex.getMessage() + "\n"));
            } finally {
                startButton.setEnabled(true);
                stopButton.setEnabled(false);
            }
        }).start();
    }

    /** 병렬 폴더 크기 계산 */
    private static class FolderSizeTask extends RecursiveTask<Long> {
        private final File folder;
        private final Map<File, Long> folderSizes;

        FolderSizeTask(File folder, Map<File, Long> folderSizes) {
            this.folder = folder;
            this.folderSizes = folderSizes;
        }

        @Override
        protected Long compute() {
            File[] files = folder.listFiles();
            if (files == null) return 0L;

            long size = 0;
            List<FolderSizeTask> subTasks = new ArrayList<>();

            for (File f : files) {
                if (f.isFile()) {
                    size += f.length();
                } else if (f.isDirectory()) {
                    FolderSizeTask task = new FolderSizeTask(f, folderSizes);
                    task.fork();
                    subTasks.add(task);
                }
            }

            for (FolderSizeTask t : subTasks) {
                size += t.join();
            }

            folderSizes.put(folder, size);
            return size;
        }
    }

    /** 트리 + CSV 출력 */
    private static void traverseAndWrite(File folder, PrintWriter writer, int depth, Map<File, Long> folderSizes) {
        if (stopRequested) return;

        long size = folderSizes.getOrDefault(folder, 0L);
        String prefix = "  ".repeat(depth) + (depth == 0 ? "" : "└ ");
        writer.printf("%d,\"%s\",%d,%s,%tF %<tT%n", depth, folder.getAbsolutePath(), size, formatSize(size), new Date(folder.lastModified()));

        try {
            SwingUtilities.invokeLater(() -> {
                if (stopRequested) return;
                textArea.append(prefix + folder.getName() + " [" + formatSize(size) + "]\n");
                textArea.setCaretPosition(textArea.getDocument().getLength());

                processedFolders++;
                int percent = (int) ((processedFolders / (double) totalFolders) * 100);
                progressBar.setValue(Math.min(percent, 99));
            });
        } catch (Exception ignored) {}

        File[] subFolders = folder.listFiles(File::isDirectory);
        if (subFolders == null) return;

        List<File> sorted = new ArrayList<>(Arrays.asList(subFolders));
        sorted.sort(getComparator(folderSizes));

        for (File sub : sorted) {
            if (stopRequested) return;
            traverseAndWrite(sub, writer, depth + 1, folderSizes);
        }
    }

    /** 폴더 개수 미리 계산 */
    private static int countFolders(File folder) {
        File[] sub = folder.listFiles(File::isDirectory);
        if (sub == null) return 1;
        int count = 1;
        for (File f : sub) count += countFolders(f);
        return count;
    }

    /** 정렬 기준 선택 */
    private static Comparator<File> getComparator(Map<File, Long> folderSizes) {
        String selected = (String) sortComboBox.getSelectedItem();
        if (selected == null) return Comparator.comparing(File::getName, String.CASE_INSENSITIVE_ORDER);

        return switch (selected) {
            case "용량 (내림차순)" ->
                    Comparator.<File, Long>comparing(folderSizes::get, Comparator.nullsLast(Long::compare)).reversed();
            case "수정일 (최신순)" ->
                    Comparator.comparingLong(File::lastModified).reversed();
            default ->
                    Comparator.comparing(File::getName, String.CASE_INSENSITIVE_ORDER);
        };
    }

    /** 보기 좋은 크기 단위 변환 */
    private static String formatSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "B";
        return new DecimalFormat("#,##0.00").format(bytes / Math.pow(1024, exp)) + " " + pre;
    }
}
