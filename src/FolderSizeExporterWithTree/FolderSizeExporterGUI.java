package FolderSizeExporterWithTree;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class FolderSizeExporterGUI {

    private static JTextArea textArea;
    private static JProgressBar progressBar;
    private static JButton startButton;
    private static JFileChooser chooser;
    private static JComboBox<String> sortComboBox;
    private static File selectedFolder;
    private static ForkJoinPool pool;

    private static int totalFolders = 0;
    private static int processedFolders = 0;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FolderSizeExporterGUI::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("폴더 용량 분석기");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 700);
        frame.setLocationRelativeTo(null);

        // 텍스트 영역
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Malgun Gothic", Font.PLAIN, 13));
        JScrollPane scrollPane = new JScrollPane(textArea);

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);

        JButton selectButton = new JButton("폴더 선택");
        startButton = new JButton("분석 시작");
        startButton.setEnabled(false);

        sortComboBox = new JComboBox<>(new String[]{"용량 (내림차순)", "이름 (오름차순)", "수정일 (최신순)"});

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.add(selectButton);
        controlPanel.add(startButton);
        controlPanel.add(new JLabel("정렬 기준:"));
        controlPanel.add(sortComboBox);

        frame.add(controlPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(progressBar, BorderLayout.SOUTH);

        chooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        selectButton.addActionListener(e -> {
            int result = chooser.showOpenDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                selectedFolder = chooser.getSelectedFile();
                textArea.append("선택된 폴더: " + selectedFolder.getAbsolutePath() + "\n");
                startButton.setEnabled(true);
            }
        });

        startButton.addActionListener(e -> {
            startButton.setEnabled(false);
            analyzeFolder();
        });

        frame.setVisible(true);
    }

    private static void analyzeFolder() {
        new Thread(() -> {
            try {
                textArea.append("\n분석 시작...\n");
                progressBar.setValue(0);
                processedFolders = 0;

                pool = new ForkJoinPool();

                // 전체 폴더 개수 계산
                totalFolders = countFolders(selectedFolder);
                textArea.append(String.format("총 폴더 수: %d개\n\n", totalFolders));

                // 전체 폴더 크기 계산 (한 번만)
                Map<File, Long> folderSizes = new HashMap<>();
                long totalSize = calculateFolderSizes(selectedFolder, folderSizes);

                String userHome = System.getProperty("user.home");
                File downloadFolder = new File(userHome, "Downloads");
                File csvFile = new File(downloadFolder, "folder_size_report.csv");
                try (OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(csvFile), StandardCharsets.UTF_8);
                     BufferedWriter bw = new BufferedWriter(osw);
                     PrintWriter writer = new PrintWriter(bw)) {

                    writer.write('\uFEFF'); // BOM 추가
                    writer.println("Depth,Folder Path,Size (bytes),Formatted Size,Last Modified");
                    traverseAndWrite(selectedFolder, writer, 0, folderSizes);
                }

                // 완료 메시지 출력 (트리 출력이 끝난 후)
                SwingUtilities.invokeLater(() -> {
                    textArea.append("\n총 폴더 크기: " + formatSize(totalSize) + "\n");
                    textArea.append("CSV 파일 생성 완료: " + csvFile.getAbsolutePath() + "\n");
                    progressBar.setValue(100);
                    startButton.setEnabled(true);
                });

            } catch (Exception ex) {
                ex.printStackTrace();
                textArea.append("오류 발생: " + ex.getMessage() + "\n");
            } finally {
                progressBar.setValue(100);
                startButton.setEnabled(true);
            }
        }).start();
    }

    /** 재귀적으로 폴더 크기 계산 후 Map에 저장 */
    private static long calculateFolderSizes(File folder, Map<File, Long> folderSizes) {
        File[] files = folder.listFiles();
        if (files == null) return 0L;

        long size = 0;
        for (File f : files) {
            if (f.isFile()) size += f.length();
        }

        for (File f : files) {
            if (f.isDirectory()) size += calculateFolderSizes(f, folderSizes);
        }

        folderSizes.put(folder, size);
        return size;
    }

    /** 병렬 폴더 크기 계산 */
    private static class FolderSizeTask extends RecursiveTask<Long> {
        private final File folder;

        FolderSizeTask(File folder) {
            this.folder = folder;
        }

        @Override
        protected Long compute() {
            File[] files = folder.listFiles();
            if (files == null) return 0L;

            long size = 0;
            var subTasks = Arrays.stream(files)
                    .filter(File::isDirectory)
                    .map(FolderSizeTask::new)
                    .toList();

            for (File f : files) {
                if (f.isFile()) size += f.length();
            }

            invokeAll(subTasks);
            for (FolderSizeTask t : subTasks) {
                size += t.join();
            }

            return size;
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

    /** CSV + 트리 출력 (정렬 + 진행률 반영) */
    private static void traverseAndWrite(File folder, PrintWriter writer, int depth, Map<File, Long> folderSizes) {
        long size = folderSizes.get(folder);
        String prefix = "  ".repeat(depth) + (depth == 0 ? "" : "└ ");
        writer.printf("%d,\"%s\",%d,%s,%tF %<tT%n", depth, folder.getAbsolutePath(), size, formatSize(size), new Date(folder.lastModified()));

        SwingUtilities.invokeLater(() -> {
            processedFolders++;
            progressBar.setValue((int) ((processedFolders / (double) totalFolders) * 100));
            textArea.append(prefix + folder.getName() + " [" + formatSize(size) + "]\n");

            // 자동 스크롤
            textArea.setCaretPosition(textArea.getDocument().getLength());
        });

        File[] subFolders = folder.listFiles(File::isDirectory);
        if (subFolders == null || subFolders.length == 0) return;

        List<File> sorted = new ArrayList<>(Arrays.asList(subFolders));
        sorted.sort(getComparator());

        for (int i = 0; i < sorted.size(); i++) {
            traverseAndWrite(sorted.get(i), writer, depth + 1, folderSizes);
        }
    }

    /** 정렬 기준 선택 */
    private static Comparator<File> getComparator() {
        String selected = (String) sortComboBox.getSelectedItem();
        if (selected == null) return Comparator.comparing(File::getName);

        return switch (selected) {
            case "용량 (내림차순)" -> (a, b) -> Long.compare(pool.invoke(new FolderSizeTask(b)), pool.invoke(new FolderSizeTask(a)));
            case "수정일 (최신순)" -> Comparator.comparingLong(File::lastModified).reversed();
            default -> Comparator.comparing(File::getName, String.CASE_INSENSITIVE_ORDER);
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
