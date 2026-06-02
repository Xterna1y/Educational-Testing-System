package com.ets.gui;

import com.ets.controller.QuizController;
import com.ets.model.Question;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.Enumeration;

/**
 * Displays a single quiz question with radio button options.
 */
public final class QuestionPanel extends JPanel {

    private static final String FONT_NAME = "SansSerif";
    private static final int TITLE_FONT_SIZE = 18;
    private static final int OPTION_FONT_SIZE = 14;

    private final QuizFrame frame;
    private final JLabel questionLabel;
    private final ButtonGroup optionsGroup;
    private final JPanel optionsPanel;

    public QuestionPanel(QuizFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout(10, 10));

        questionLabel = new JLabel("Question", SwingConstants.CENTER);
        questionLabel.setFont(new Font(FONT_NAME, Font.BOLD, TITLE_FONT_SIZE));
        add(questionLabel, BorderLayout.NORTH);

        optionsPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        optionsGroup = new ButtonGroup();
        add(optionsPanel, BorderLayout.CENTER);

        JButton nextButton = new JButton("Next");
        nextButton.addActionListener(e -> frame.onNextClicked());
        add(nextButton, BorderLayout.SOUTH);
    }

    public void refresh() {
        QuizController controller = frame.getController();
        Question question = controller.getCurrentQuestion();

        questionLabel.setText("<html><center>" + question.getText() + "</center></html>");

        optionsPanel.removeAll();
        optionsGroup.clearSelection();

        for (String option : question.getOptions()) {
            JRadioButton radio = new JRadioButton(option);
            radio.setFont(new Font(FONT_NAME, Font.PLAIN, OPTION_FONT_SIZE));
            optionsGroup.add(radio);
            optionsPanel.add(radio);
        }

        optionsPanel.revalidate();
        optionsPanel.repaint();
    }

    public int getSelectedOption() {
        int index = 0;
        Enumeration<javax.swing.AbstractButton> buttons = optionsGroup.getElements();
        while (buttons.hasMoreElements()) {
            if (buttons.nextElement().isSelected()) {
                return index;
            }
            index++;
        }
        return -1; // No selection
    }
}