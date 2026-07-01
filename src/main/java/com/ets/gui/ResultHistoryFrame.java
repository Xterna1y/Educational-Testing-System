package com.ets.gui;

import com.ets.model.Result;
import com.ets.repo.JsonResultRepository;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Shows a student's past quiz results, most recent first.
 *
 * <p>Reads from the same results.json file that {@link QuizSelectionFrame}
 * writes to via {@link JsonResultRepository}.</p>
 */
public class ResultHistoryFrame extends JFrame {

    private static final Color BG_DARK      = Color.BLACK;
    private static final Color CARD_BG      = Color.DARK_GRAY;
    private static final Color TEXT_PRIMARY = Color.WHITE;
    private static final Color TEXT_MUTED   = Color.LIGHT_GRAY;
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

    private final String username;

    public ResultHistoryFrame(String username) {
        this.username = username;
        buildUI();
    }

    private void buildUI() {
        setTitle("ETS – My Results");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(720, 520);
        setMinimumSize(new Dimension(600, 400));
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_DARK);
        setContentPane(root);

        root.add(buildHeader(), BorderLayout.NORTH);

        // TODO: same as QuizSelectionFrame — replace hardcoded path with a
        // shared ResultRepository instance injected from Main.java.
        JsonResultRepository repo =
                new JsonResultRepository(Path.of("src/main/resources/results.json"));
        List<Result> myResults = repo.getResultsByUsername(username);

        JPanel centre = new JPanel(new BorderLayout());
        centre.setBackground(BG_DARK);
        centre.setBorder(new EmptyBorder(24, 24, 24, 24));

        if (myResults.isEmpty()) {
            JLabel empty = new JLabel("You haven't completed any quizzes yet.", SwingConstants.CENTER);
            empty.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            empty.setForeground(TEXT_MUTED);
            centre.add(empty, BorderLayout.CENTER);
        } else {
            centre.add(buildTable(myResults), BorderLayout.CENTER);
        }

        root.add(centre, BorderLayout.CENTER);
        root.add(buildFooter(), BorderLayout.SOUTH);
    }

    private JScrollPane buildTable(List<Result> results) {
        String[] columns = {"Quiz", "Score", "Percentage", "Completed"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        for (Result r : results) {
            model.addRow(new Object[]{
                    r.getQuizId(),
                    r.getScore() + " / " + r.getTotalPoints(),
                    String.format("%.0f%%", r.getPercentage()),
                    formatTimestamp(r.getCompletedAt())
            });
        }

        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(28);
        table.setBackground(CARD_BG);
        table.setForeground(TEXT_PRIMARY);
        table.setGridColor(Color.GRAY);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(CARD_BG);
        table.getTableHeader().setForeground(TEXT_PRIMARY);
        table.setFillsViewportHeight(true);

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(CARD_BG);
        return scroll;
    }

    /**
     * Formats the raw ISO-8601 completedAt string into a friendlier display
     * format. Falls back to the raw string if parsing fails for any reason.
     */
    private String formatTimestamp(String isoTimestamp) {
        try {
            return LocalDateTime.parse(isoTimestamp).format(DISPLAY_FORMAT);
        } catch (DateTimeParseException e) {
            return isoTimestamp;
        }
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(CARD_BG);
        header.setBorder(new EmptyBorder(16, 24, 16, 24));

        JLabel appName = new JLabel("📚 Educational Testing System");
        appName.setFont(new Font("Segoe UI", Font.BOLD, 16));
        appName.setForeground(TEXT_PRIMARY);

        JLabel title = new JLabel("📊 My Results");
        title.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        title.setForeground(TEXT_MUTED);

        header.add(appName, BorderLayout.WEST);
        header.add(title, BorderLayout.EAST);
        return header;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setBackground(BG_DARK);
        footer.setBorder(new EmptyBorder(0, 0, 16, 0));

        JButton backBtn = new JButton("← Back to Dashboard");
        backBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        backBtn.setForeground(TEXT_MUTED);
        backBtn.setBackground(CARD_BG);
        backBtn.setBorderPainted(false);
        backBtn.setFocusPainted(false);
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> {
            DashboardFrame dashboard = new DashboardFrame(username);
            dashboard.setVisible(true);
            dispose();
        });

        footer.add(backBtn);
        return footer;
    }
}