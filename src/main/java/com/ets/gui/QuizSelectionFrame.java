package com.ets.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Screen for selecting a quiz, shown after clicking "Take Quiz" on the dashboard.
 */
public class QuizSelectionFrame extends JFrame {

    // ── Colours (matches app theme) ──────────────────────────────────────────
    private static final Color BG_DARK      = Color.BLACK;
    private static final Color CARD_BG      = Color.DARK_GRAY;
//    private static final Color ACCENT       = Color.BLUE;
    private static final Color TEXT_PRIMARY = Color.WHITE;
    private static final Color TEXT_MUTED   = Color.LIGHT_GRAY;

    private final String username;

    public QuizSelectionFrame(String username) {
        this.username = username;
        buildUI();
    }

    private void buildUI() {
        setTitle("ETS – Quiz Selection");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);
        setMinimumSize(new Dimension(600, 420));
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_DARK);
        setContentPane(root);

        // ── Header bar ────────────────────────────────────────────────────
        JPanel header = buildHeader();
        root.add(header, BorderLayout.NORTH);

        // ── Centre content ────────────────────────────────────────────────
        JPanel centre = new JPanel(new GridBagLayout());
        centre.setBackground(BG_DARK);
        centre.setBorder(new EmptyBorder(40, 40, 40, 40));

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_BG);
        card.setBorder(new EmptyBorder(40, 48, 40, 48));

        JLabel icon = new JLabel("📝", SwingConstants.CENTER);
        icon.setFont(new Font("Segue UI Emoji", Font.PLAIN, 52));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("Select a Quiz", SwingConstants.CENTER);
        title.setFont(new Font("Segue UI", Font.BOLD, 24));
        title.setForeground(TEXT_PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Choose a quiz to begin", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segue UI", Font.PLAIN, 14));
        subtitle.setForeground(TEXT_MUTED);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ── Back button ───────────────────────────────────────────────────
        JButton backBtn = new JButton("← Back to Dashboard");
        backBtn.setFont(new Font("Segue UI", Font.PLAIN, 13));
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
        card.add(Box.createVerticalStrut(32));
        card.add(backBtn);

        centre.add(card);
        root.add(centre, BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(CARD_BG);
        header.setBorder(new EmptyBorder(16, 24, 16, 24));

        JLabel appName = new JLabel("📚 Educational Testing System");
        appName.setFont(new Font("Segue UI", Font.BOLD, 16));
        appName.setForeground(TEXT_PRIMARY);

        JLabel greeting = new JLabel("Welcome, " + username + "  👋");
        greeting.setFont(new Font("Segue UI", Font.PLAIN, 14));
        greeting.setForeground(TEXT_MUTED);

        header.add(appName,  BorderLayout.WEST);
        header.add(greeting, BorderLayout.EAST);
        return header;
    }
}
