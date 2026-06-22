package com.ets.gui;

import com.ets.model.Result;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;

/**
 * Panel displaying quiz results with score and percentage.
 */
public final class ResultFrame extends JPanel {

    private static final String FONT_NAME = "SansSerif";
    private static final int TITLE_FONT_SIZE = 22;
    private static final int SCORE_FONT_SIZE = 36;
    private static final int DETAIL_FONT_SIZE = 14;

    private final QuizFrame frame;
    private final JLabel titleLabel;
    private final JLabel scoreLabel;
    private final JLabel detailLabel;
    private final JLabel percentageLabel;

    public ResultFrame(QuizFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout(20, 20));

        JPanel centerPanel = new JPanel(new GridLayout(4, 1, 10, 10));

        titleLabel = new JLabel("Quiz Complete!", SwingConstants.CENTER);
        titleLabel.setFont(new Font(FONT_NAME, Font.BOLD, TITLE_FONT_SIZE));
        centerPanel.add(titleLabel);

        scoreLabel = new JLabel("Score: 0/0", SwingConstants.CENTER);
        scoreLabel.setFont(new Font(FONT_NAME, Font.BOLD, SCORE_FONT_SIZE));
        centerPanel.add(scoreLabel);

        percentageLabel = new JLabel("0%", SwingConstants.CENTER);
        percentageLabel.setFont(new Font(FONT_NAME, Font.BOLD, SCORE_FONT_SIZE));
        centerPanel.add(percentageLabel);

        detailLabel = new JLabel(" ", SwingConstants.CENTER);
        detailLabel.setFont(new Font(FONT_NAME, Font.PLAIN, DETAIL_FONT_SIZE));
        centerPanel.add(detailLabel);

        add(centerPanel, BorderLayout.CENTER);

        JButton restartButton = new JButton("Close");
        restartButton.addActionListener(e -> frame.onRestartClicked());
        add(restartButton, BorderLayout.SOUTH);
    }

    public void setResult(Result result) {
        scoreLabel.setText(String.format("Score: %d/%d", result.getScore(), result.getTotalPoints()));
        percentageLabel.setText(String.format("%.0f%%", result.getPercentage()));

        String message;
        double pct = result.getPercentage();
        if (pct >= 80) {
            message = "Excellent work!";
        } else if (pct >= 60) {
            message = "Good job! Keep practicing.";
        } else if (pct >= 40) {
            message = "Not bad. Review the material and try again.";
        } else {
            message = "Keep studying! You'll improve with practice.";
        }
        detailLabel.setText(message);
    }
}