package com.ets.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Student dashboard shown after a successful login with a STUDENT role.
 */
public class DashboardFrame extends JFrame {

    // ── Colours (matches LoginFrame theme) ─────────────────────────────────
    private static final Color BG_DARK      = Color.BLACK;
    private static final Color CARD_BG      = Color.DARK_GRAY;
    private static final Color ACCENT       = Color.BLUE;
    private static final Color TEXT_PRIMARY = Color.WHITE;
    private static final Color TEXT_MUTED   = Color.LIGHT_GRAY;

    private final String username;

    public DashboardFrame(String username) {
        this.username = username;
        buildUI();
    }

    private void buildUI() {
        setTitle("ETS – Student Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);
        setMinimumSize(new Dimension(600, 420));
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_DARK);
        setContentPane(root);

        // ── Header bar ────────────────────────────────────────────────────
        JPanel header = getJPanel();
        root.add(header, BorderLayout.NORTH);

        // ── Centre content ─────────────────────────────────────────────────
        JPanel centre = new JPanel(new GridBagLayout());
        centre.setBackground(BG_DARK);
        centre.setBorder(new EmptyBorder(40, 40, 40, 40));

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_BG);
        card.setBorder(new EmptyBorder(40, 48, 40, 48));

        JLabel icon = new JLabel("🎓", SwingConstants.CENTER);
        icon.setFont(new Font("Segue UI Emoji", Font.PLAIN, 52));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("Student Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("Segue UI", Font.BOLD, 24));
        title.setForeground(TEXT_PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Select an action below to get started", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segue UI", Font.PLAIN, 14));
        subtitle.setForeground(TEXT_MUTED);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ── Action buttons ─────────────────────────────────────────────────
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        buttonRow.setBackground(CARD_BG);
        buttonRow.setAlignmentX(Component.CENTER_ALIGNMENT);

        buttonRow.add(buildActionButton("📝  Take Quiz",   ACCENT));
        buttonRow.add(buildActionButton("📊  My Results",  Color.GREEN));

        card.add(icon);
        card.add(Box.createVerticalStrut(12));
        card.add(title);
        card.add(Box.createVerticalStrut(8));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(32));
        card.add(buttonRow);

        centre.add(card);
        root.add(centre, BorderLayout.CENTER);
    }

    private JPanel getJPanel() {
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

    private JButton buildActionButton(String text, Color color) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segue UI", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(160, 44));
        return btn;
    }
}
