package com.ets.gui;

import com.ets.controller.LoginController;
import com.ets.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Login screen for the Educational Testing System.
 *
 * <p>Presents username and password fields and delegates authentication
 * to {@link LoginController}. On success, opens the appropriate dashboard
 * for the user's role (ADMIN or STUDENT).</p>
 */
public class LoginFrame extends JFrame {

    // ── Colours ────────────────────────────────────────────────────────────
    private static final Color BG_DARK        = Color.BLACK;
    private static final Color CARD_BG        = Color.DARK_GRAY;
    private static final Color ACCENT         = Color.BLUE;
    private static final Color ACCENT_HOVER   = Color.BLUE;
    private static final Color TEXT_PRIMARY   = Color.WHITE;
    private static final Color TEXT_MUTED     = Color.LIGHT_GRAY;
    private static final Color FIELD_BG       = Color.DARK_GRAY;
    private static final Color FIELD_BORDER   = Color.GRAY;
    private static final Color ERROR_COLOR    = Color.RED;
    private static final Color SUCCESS_COLOR  = Color.GREEN;

    // ── UI components ──────────────────────────────────────────────────────
    private JTextField     usernameField;
    private JPasswordField passwordField;
    private JLabel         statusLabel;
    private JButton        loginButton;

    private final LoginController controller;

    // ─────────────────────────────────────────────────────────────────────
    public LoginFrame(LoginController controller) {
        this.controller = controller;
        buildUI();
    }

    // ── UI construction ───────────────────────────────────────────────────

    private void buildUI() {
        setTitle("ETS – Educational Testing System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(480, 580);
        setMinimumSize(new Dimension(400, 520));
        setLocationRelativeTo(null);
        setResizable(false);

        // ── Root panel with dark background
        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(BG_DARK);
        setContentPane(root);

        // ── Card panel (centred white-ish card)
        JPanel card = buildCard();
        root.add(card);
    }

    private JPanel buildCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_BG);
        card.setBorder(new EmptyBorder(48, 48, 48, 48));
        card.setPreferredSize(new Dimension(380, 490));

        // ── Logo / Icon area
        JLabel icon = new JLabel("🎓", SwingConstants.CENTER);
        icon.setFont(new Font("Segue UI Emoji", Font.PLAIN, 56));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ── Title
        JLabel title = new JLabel("Welcome Back", SwingConstants.CENTER);
        title.setFont(new Font("Segue UI", Font.BOLD, 26));
        title.setForeground(TEXT_PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ── Subtitle
        JLabel subtitle = new JLabel("Sign in to your ETS account", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segue UI", Font.PLAIN, 14));
        subtitle.setForeground(TEXT_MUTED);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ── Username field (initialize first, pass into builder)
        usernameField = new JTextField(20);
        JPanel usernameGroup = buildFieldGroup("Username", usernameField);

        // ── Password field
        passwordField = new JPasswordField(20);
        JPanel passwordGroup = buildFieldGroup("Password", passwordField);

        // ── Status label (errors / success messages)
        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segue UI", Font.PLAIN, 13));
        statusLabel.setForeground(ERROR_COLOR);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ── Login button
        loginButton = buildLoginButton();

        // ── Hint
        JLabel hint = new JLabel("Demo: student01 / admin01  ·  password123", SwingConstants.CENTER);
        hint.setFont(new Font("Segue UI", Font.ITALIC, 11));
        hint.setForeground(TEXT_MUTED);
        hint.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ── Assembly
        card.add(icon);
        card.add(Box.createVerticalStrut(12));
        card.add(title);
        card.add(Box.createVerticalStrut(6));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(32));
        card.add(usernameGroup);
        card.add(Box.createVerticalStrut(16));
        card.add(passwordGroup);
        card.add(Box.createVerticalStrut(8));
        card.add(statusLabel);
        card.add(Box.createVerticalStrut(12));
        card.add(loginButton);
        card.add(Box.createVerticalStrut(20));
        card.add(hint);

        // Round card corners via compound border (visual only — paint override below)
        return card;
    }

    /**
     * Creates a labelled field group wrapping an already-created input field.
     *
     * @param label field label text
     * @param field the pre-created {@link JTextField} or {@link JPasswordField} to embed
     * @return a panel containing the label and the styled input wrapper
     */
    private JPanel buildFieldGroup(String label, JTextField field) {
        JPanel group = new JPanel();
        group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));
        group.setBackground(CARD_BG);
        group.setAlignmentX(Component.CENTER_ALIGNMENT);
        group.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        // Label
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segue UI", Font.BOLD, 13));
        lbl.setForeground(TEXT_PRIMARY);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Style the field
        field.setBackground(FIELD_BG);
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(TEXT_PRIMARY);
        field.setFont(new Font("Segue UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createEmptyBorder());
        field.setOpaque(false);

        // Allow pressing Enter to trigger login
        field.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "login");
        field.getActionMap().put("login", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });

        // Wrapper panel that gives the field its coloured background + border
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(FIELD_BG);
        wrapper.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(FIELD_BORDER, 1, true),
                new EmptyBorder(8, 14, 8, 14)
        ));
        wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        wrapper.add(field, BorderLayout.CENTER);

        group.add(lbl);
        group.add(Box.createVerticalStrut(4));
        group.add(wrapper);
        return group;
    }

    private JButton buildLoginButton() {
        JButton btn = new JButton("Sign In") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? ACCENT_HOVER : ACCENT);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segue UI", Font.BOLD, 15));
        btn.setForeground(Color.WHITE);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        btn.setPreferredSize(new Dimension(284, 46));

        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.repaint(); }
            @Override public void mouseExited(MouseEvent e)  { btn.repaint(); }
        });

        btn.addActionListener(e -> handleLogin());
        return btn;
    }

    // ── Login logic ───────────────────────────────────────────────────────

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            showStatus("Please enter both username and password.", false);
            return;
        }

        // Disable button while processing to prevent double-clicks
        loginButton.setEnabled(false);
        loginButton.setText("Signing in…");

        // Run on a background thread so the UI stays responsive
        SwingWorker<User, Void> worker = new SwingWorker<User, Void>() {
            @Override
            protected User doInBackground() {
                return controller.login(username, password);
            }

            @Override
            protected void done() {
                loginButton.setEnabled(true);
                loginButton.setText("Sign In");
                try {
                    User user = get();
                    if (user != null) {
                        showStatus("✓ Login successful! Welcome, " + user.getUsername(), true);
                        openDashboard(user);
                    } else {
                        showStatus("✗ Invalid username or password.", false);
                        passwordField.setText("");
                        passwordField.requestFocusInWindow();
                    }
                } catch (Exception ex) {
                    showStatus("An unexpected error occurred.", false);
                }
            }
        };
        worker.execute();
    }

    private void showStatus(String message, boolean success) {
        statusLabel.setForeground(success ? SUCCESS_COLOR : ERROR_COLOR);
        statusLabel.setText(message);
    }

    /**
     * Opens the appropriate dashboard based on the authenticated user's role,
     * then hides this login window.
     */
    private void openDashboard(User user) {
        SwingUtilities.invokeLater(() -> {
            // Small delay so the user can read the success message
            Timer timer = new Timer(700, e -> {
                if ("ADMIN".equalsIgnoreCase(user.getRole())) {
                    AdminFrame adminFrame = new AdminFrame();
                    adminFrame.setVisible(true);
                } else {
                    DashboardFrame dashboardFrame = new DashboardFrame(user.getUsername());
                    dashboardFrame.setVisible(true);
                }
                LoginFrame.this.dispose();
            });
            timer.setRepeats(false);
            timer.start();
        });
    }
}
