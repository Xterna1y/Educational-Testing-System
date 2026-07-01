package com.ets.gui;

import com.ets.api.TriviaApiService;
import com.ets.model.Difficulty;
import com.ets.model.Question;
import com.ets.repo.CachedQuestionRepository;
import com.ets.repo.JsonResultRepository;
import com.ets.repo.QuestionProvider;
import com.ets.repo.ResultRepository;
import com.ets.services.QuizService;
import com.ets.strategy.DifficultyStrategy;
import com.ets.strategy.QuizStrategy;
import com.ets.strategy.RandomStrategy;
import com.ets.strategy.WeakTopicStrategy;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.nio.file.Path;
import java.util.List;

/**
 * Screen for selecting a quiz, shown after clicking "Take Quiz" on the dashboard.
 */
public class QuizSelectionFrame extends JFrame {

    private static final Color BG_DARK      = Color.BLACK;
    private static final Color CARD_BG      = Color.DARK_GRAY;
    private static final Color TEXT_PRIMARY = Color.WHITE;
    private static final Color TEXT_MUTED   = Color.LIGHT_GRAY;

    private static final int DEFAULT_QUESTION_COUNT = 10;

    private final String username;
    private final QuestionProvider questionProvider;
    private final QuizService quizService = new QuizService();

    // TODO: same as CachedQuestionRepository below — replace hardcoded path
    // with something injected from Main.java once a shared instance exists.
    private final ResultRepository resultRepository =
            new JsonResultRepository(Path.of("src/main/resources/results.json"));

    private JComboBox<Difficulty> difficultyCombo;
    private JComboBox<String> strategyCombo;
    private JSpinner countSpinner;

    public QuizSelectionFrame(String username) {
        this.username = username;
        // TODO: replace this hardcoded path with whatever Main.java wires up
        // (e.g. a shared Path/Constants.CACHE_FILE injected from outside).
        CachedQuestionRepository cacheRepository =
                new CachedQuestionRepository(Path.of("src/main/resources/fallback_questions.json"));
        this.questionProvider = new QuestionProvider(new TriviaApiService(), cacheRepository);
        buildUI();
    }

    private void buildUI() {
        setTitle("ETS – Quiz Selection");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 560);
        setMinimumSize(new Dimension(600, 480));
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_DARK);
        setContentPane(root);

        root.add(buildHeader(), BorderLayout.NORTH);

        JPanel centre = new JPanel(new GridBagLayout());
        centre.setBackground(BG_DARK);
        centre.setBorder(new EmptyBorder(40, 40, 40, 40));

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_BG);
        card.setBorder(new EmptyBorder(40, 48, 40, 48));

        JLabel icon = new JLabel("📝", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 52));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("Select a Quiz", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(TEXT_PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Choose your settings to begin", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(TEXT_MUTED);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ── Difficulty selector ──────────────────────────────────────────
        JLabel difficultyLabel = makeFieldLabel("Difficulty");
        difficultyCombo = new JComboBox<>(Difficulty.values());
        styleCombo(difficultyCombo);

        // ── Strategy selector ────────────────────────────────────────────
        JLabel strategyLabel = makeFieldLabel("Question Selection Strategy");
        strategyCombo = new JComboBox<>(new String[]{"Random", "Difficulty-Based", "Weak Topics"});
        styleCombo(strategyCombo);

        // ── Question count selector ──────────────────────────────────────
        JLabel countLabel = makeFieldLabel("Number of Questions");
        countSpinner = new JSpinner(new SpinnerNumberModel(DEFAULT_QUESTION_COUNT, 1, 50, 1));
        countSpinner.setMaximumSize(new Dimension(200, 32));
        countSpinner.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ── Start button ──────────────────────────────────────────────────
        JButton startBtn = new JButton("Start Quiz →");
        startBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        startBtn.setForeground(TEXT_PRIMARY);
        startBtn.setBackground(new Color(46, 125, 50));
        startBtn.setBorderPainted(false);
        startBtn.setFocusPainted(false);
        startBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        startBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        startBtn.addActionListener(e -> onStartQuiz());

        JButton backBtn = new JButton("← Back to Dashboard");
        backBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        backBtn.setForeground(TEXT_MUTED);
        backBtn.setBackground(CARD_BG);
        backBtn.setBorderPainted(false);
        backBtn.setFocusPainted(false);
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        backBtn.addActionListener(e -> {
            DashboardFrame dashboard = new DashboardFrame(username);
            dashboard.setVisible(true);
            QuizSelectionFrame.this.dispose();
        });

        card.add(icon);
        card.add(Box.createVerticalStrut(12));
        card.add(title);
        card.add(Box.createVerticalStrut(8));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(28));
        card.add(difficultyLabel);
        card.add(difficultyCombo);
        card.add(Box.createVerticalStrut(16));
        card.add(strategyLabel);
        card.add(strategyCombo);
        card.add(Box.createVerticalStrut(16));
        card.add(countLabel);
        card.add(countSpinner);
        card.add(Box.createVerticalStrut(28));
        card.add(startBtn);
        card.add(Box.createVerticalStrut(12));
        card.add(backBtn);

        centre.add(card);
        root.add(centre, BorderLayout.CENTER);
    }

    private void onStartQuiz() {
        Difficulty selectedDifficulty = (Difficulty) difficultyCombo.getSelectedItem();
        int count = (Integer) countSpinner.getValue();

        // Tries the live Trivia API first; falls back to cached/local
        // questions automatically on any failure (no stack trace shown to user).
        List<Question> available = questionProvider.getQuestions(count, null, selectedDifficulty);

        if (available.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "No questions are available right now. Please check your connection or try again later.",
                    "No Questions Found",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        QuizStrategy strategy = resolveStrategy((String) strategyCombo.getSelectedItem());

        // QuizService.createQuiz() always calls strategy.selectQuestions(questions, questions.size()),
        // ignoring any requested count. We apply the strategy here ourselves first so the
        // user's chosen question count is actually respected before handing off to QuizFrame.
        List<Question> selected = strategy.selectQuestions(available, count);

        if (selected.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "No questions matched your selected criteria. Try a different difficulty or strategy.",
                    "No Matching Questions",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        QuizFrame quizFrame = new QuizFrame(quizService, selected, strategy, resultRepository, username);
        quizFrame.setVisible(true);
        dispose();
    }

    private QuizStrategy resolveStrategy(String label) {
        if (label == null) return new RandomStrategy();
        return switch (label) {
            case "Difficulty-Based" -> new DifficultyStrategy((Difficulty) difficultyCombo.getSelectedItem());
            case "Weak Topics" -> new WeakTopicStrategy();
            default -> new RandomStrategy();
        };
    }

    private JLabel makeFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(TEXT_MUTED);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private void styleCombo(JComboBox<?> combo) {
        combo.setMaximumSize(new Dimension(240, 32));
        combo.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(CARD_BG);
        header.setBorder(new EmptyBorder(16, 24, 16, 24));

        JLabel appName = new JLabel("📚 Educational Testing System");
        appName.setFont(new Font("Segoe UI", Font.BOLD, 16));
        appName.setForeground(TEXT_PRIMARY);

        JLabel greeting = new JLabel("Welcome, " + username + "  👋");
        greeting.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        greeting.setForeground(TEXT_MUTED);

        header.add(appName,  BorderLayout.WEST);
        header.add(greeting, BorderLayout.EAST);
        return header;
    }
}