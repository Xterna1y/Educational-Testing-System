package com.ets.gui;

import com.ets.controller.QuizController;
import com.ets.model.Question;
import com.ets.model.Result;
import com.ets.repo.ResultRepository;
import com.ets.services.QuizService;
import com.ets.strategy.QuizStrategy;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.CardLayout;
import java.util.List;

/**
 * Main quiz window using CardLayout to switch between questions and results.
 */
public final class QuizFrame extends JFrame {

    private static final String QUIZ = "QUIZ";
    private static final String RESULT = "RESULT";
    private static final int FRAME_WIDTH = 700;
    private static final int FRAME_HEIGHT = 550;

    private final QuizController controller;
    private final CardLayout cardLayout;
    private final JPanel mainPanel;
    private final QuestionPanel questionPanel;
    private final ResultFrame resultPanel;

    /**
     * @param service          quiz creation/scoring logic
     * @param questions        the questions selected for this quiz attempt
     * @param strategy         the selection strategy used (passed through to the controller)
     * @param resultRepository where the completed result gets saved; may be {@code null}
     * @param username         the student taking the quiz
     */
    public QuizFrame(QuizService service, List<Question> questions, QuizStrategy strategy,
                     ResultRepository resultRepository, String username) {
        super("Educational Testing System");

        this.controller = new QuizController(service, resultRepository, username);
        this.cardLayout = new CardLayout();
        this.mainPanel = new JPanel(cardLayout);

        controller.startQuiz(questions, strategy);

        this.questionPanel = new QuestionPanel(this);
        this.resultPanel = new ResultFrame(username);

        mainPanel.add(questionPanel, QUIZ);
        mainPanel.add(resultPanel, RESULT);

        add(mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setLocationRelativeTo(null);

        showQuiz();
    }

    public QuizController getController() {
        return controller;
    }

    public void showQuiz() {
        cardLayout.show(mainPanel, QUIZ);
        questionPanel.refresh();
    }

    public void showResult(Result result) {
        resultPanel.setResult(result);
        cardLayout.show(mainPanel, RESULT);
    }

    public void onNextClicked() {
        controller.submitAnswer(questionPanel.getSelectedOption());
        if (controller.hasNextQuestion()) {
            controller.nextQuestion();
            questionPanel.refresh();
        } else {
            Result result = controller.finishQuiz();
            showResult(result);
        }
    }

    public void onRestartClicked() {
        dispose();
    }
}