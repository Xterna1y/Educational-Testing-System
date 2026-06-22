package com.ets.gui;

import com.ets.model.Result;

import javax.swing.*;
import java.awt.*;

/**
 * Displays the result of a completed quiz attempt.
 *
 * <p>Used as a {@link JPanel} card inside {@link QuizFrame}'s CardLayout.
 * Call {@link #setResult(Result)} to populate it before showing it.</p>
 */
public class ResultFrame extends JPanel {

    private final JLabel scoreLabel;
    private final JLabel percentLabel;
    private final JLabel gradeLabel;

    public ResultFrame() {
        setLayout(new BorderLayout());
        setBackground(Color.DARK_GRAY);

        // ── Centre card ───────────────────────────────────────────────────
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.DARK_GRAY);
        card.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        // Icon
        JLabel icon = new JLabel("🏆", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 52));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Title
        JLabel title = new JLabel("Quiz Complete!", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Score line (e.g. "Score: 70 / 100")
        scoreLabel = new JLabel("Score: —", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        scoreLabel.setForeground(Color.LIGHT_GRAY);
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Large percentage
        percentLabel = new JLabel("—", SwingConstants.CENTER);
        percentLabel.setFont(new Font("Segoe UI", Font.BOLD, 52));
        percentLabel.setForeground(Color.GREEN);
        percentLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Grade message
        gradeLabel = new JLabel(" ", SwingConstants.CENTER);
        gradeLabel.setFont(new Font("Segoe UI", Font.ITALIC, 15));
        gradeLabel.setForeground(Color.LIGHT_GRAY);
        gradeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // "Back to Dashboard" button — disposes the parent QuizFrame window
        JButton doneBtn = new JButton("Back to Dashboard");
        doneBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        doneBtn.setForeground(Color.WHITE);
        doneBtn.setBackground(Color.BLUE);
        doneBtn.setOpaque(true);
        doneBtn.setFocusPainted(false);
        doneBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        doneBtn.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(ResultFrame.this);
            if (window != null) window.dispose();
        });

        card.add(icon);
        card.add(Box.createVerticalStrut(12));
        card.add(title);
        card.add(Box.createVerticalStrut(28));
        card.add(scoreLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(percentLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(gradeLabel);
        card.add(Box.createVerticalStrut(32));
        card.add(doneBtn);

        // Centre the card vertically
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(Color.DARK_GRAY);
        wrapper.add(card);
        add(wrapper, BorderLayout.CENTER);
    }

    /**
     * Populates the panel with the quiz result data.
     * Must be called before this panel becomes visible.
     *
     * @param result the completed quiz result
     */
    public void setResult(Result result) {
        int score      = result.getScore();
        int total      = result.getTotalPoints();
        double pct     = result.getPercentage();

        scoreLabel.setText("Score: " + score + " / " + total);
        percentLabel.setText(String.format("%.0f%%", pct));

        // Colour and message change with performance
        if (pct >= 80) {
            percentLabel.setForeground(Color.GREEN);
            gradeLabel.setText("Excellent work!");
        } else if (pct >= 60) {
            percentLabel.setForeground(Color.ORANGE);
            gradeLabel.setText("Good job — keep it up!");
        } else {
            percentLabel.setForeground(Color.RED);
            gradeLabel.setText("Keep practising — you'll get there!");
        }

        revalidate();
        repaint();
    }
}
