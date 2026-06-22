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
import java.io.IOException;

/**
 * Login screen for the Educational Testing System.
 *
 * <p>Presents a username field and two buttons — <em>Login</em> and
 * <em>Register</em> — delegating both actions to {@link LoginController}.
 * No password is required. On a successful login the appropriate dashboard
 * opens; on a successful registration the new account is confirmed and the
 * user is logged in automatically. Duplicate username attempts show an
 * inline error message.</p>
 */
public class LoginFrame extends JFrame {

    // ── Colours ────────────────────────────────────────────────────────────
    private static final Color BG_DARK        = new Color(15, 15, 20);
    private static final Color CARD_BG        = new Color(28, 28, 38);
    private static final Color ACCENT         = new Color(99, 102, 241);   // Indigo
    private static final Color ACCENT_HOVER   = new Color(129, 132, 255);
    private static final Color ACCENT2        = new Color(16, 185, 129);   // Emerald
    private static final Color ACCENT2_HOVER  = new Color(52, 211, 153);
    private static final Color TEXT_PRIMARY   = Color.WHITE;
    private static final Color TEXT_MUTED     = new Color(148, 163, 184);
    private static final Color FIELD_BG       = new Color(38, 38, 52);
    private static final Color FIELD_BORDER   = new Color(65, 65, 90);
    private static final Color ERROR_COLOR    = new Color(239, 68, 68);
    private static final Color SUCCESS_COLOR  = new Color(52, 211, 153);

    // ── UI components ──────────────────────────────────────────────────────
    private JTextField usernameField;
    private JLabel     statusLabel;
    private JButton    loginButton;
    private JButton    registerButton;

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
        setSize(480, 500);
        setMinimumSize(new Dimension(400, 440));
        setLocationRelativeTo(null);
        setResizable(false);

        // ── Root panel with dark background
        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(BG_DARK);
        setContentPane(root);

        // ── Card panel (centred)
        JPanel card = buildCard();
        root.add(card);
    }

    private JPanel buildCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_BG);
        card.setBorder(new EmptyBorder(48, 48, 48, 48));
        card.setPreferredSize(new Dimension(380, 410));

        // ── Logo / Icon area
        JLabel icon = new JLabel("🎓", SwingConstants.CENTER);
        icon.setFont(new Font("Segue UI Emoji", Font.PLAIN, 56));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ── Title
        JLabel title = new JLabel("Educational Testing System", SwingConstants.CENTER);
        title.setFont(new Font("Segue UI", Font.BOLD, 22));
        title.setForeground(TEXT_PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ── Subtitle
        JLabel subtitle = new JLabel("Enter your username to continue", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segue UI", Font.PLAIN, 13));
        subtitle.setForeground(TEXT_MUTED);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ── Username field
        usernameField = new JTextField(20);
        JPanel usernameGroup = buildFieldGroup(usernameField);

        // ── Status label (errors / success messages)
        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segue UI", Font.PLAIN, 13));
        statusLabel.setForeground(ERROR_COLOR);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ── Buttons row
        JPanel buttonRow = buildButtonRow();

        // ── Assembly
        card.add(icon);
        card.add(Box.createVerticalStrut(12));
        card.add(title);
        card.add(Box.createVerticalStrut(6));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(32));
        card.add(usernameGroup);
        card.add(Box.createVerticalStrut(10));
        card.add(statusLabel);
        card.add(Box.createVerticalStrut(12));
        card.add(buttonRow);

        return card;
    }

    /**
     * Creates a labelled field group wrapping an already-created input field.
     *
     * @param field the pre-created {@link JTextField} to embed
     * @return a panel containing the label and the styled input wrapper
     */
    private JPanel buildFieldGroup(JTextField field) {
        JPanel group = new JPanel();
        group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));
        group.setBackground(CARD_BG);
        group.setAlignmentX(Component.CENTER_ALIGNMENT);
        group.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        // Label
        JLabel lbl = new JLabel("Username");
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

    /** Builds a horizontal panel with the Login and Register buttons side by side. */
    private JPanel buildButtonRow() {
        loginButton    = buildStyledButton("Login",    ACCENT,  ACCENT_HOVER);
        registerButton = buildStyledButton("Register", ACCENT2, ACCENT2_HOVER);

        loginButton.addActionListener(e -> handleLogin());
        registerButton.addActionListener(e -> handleRegister());

        JPanel row = new JPanel(new GridLayout(1, 2, 12, 0));
        row.setBackground(CARD_BG);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        row.setAlignmentX(Component.CENTER_ALIGNMENT);
        row.add(loginButton);
        row.add(registerButton);
        return row;
    }

    /**
     * Builds a rounded, coloured action button.
     *
     * @param text  button label
     * @param base  base background colour
     * @param hover hover background colour
     */
    private JButton buildStyledButton(String text, Color base, Color hover) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? hover : base);
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
        btn.setPreferredSize(new Dimension(130, 46));

        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.repaint(); }
            @Override public void mouseExited(MouseEvent e)  { btn.repaint(); }
        });
        return btn;
    }

    // ── Login logic ───────────────────────────────────────────────────────

    private void handleLogin() {
        String username = usernameField.getText().trim();

        if (username.isEmpty()) {
            showStatus("Please enter your username.", false);
            return;
        }

        setBothButtonsEnabled(false, "Logging in…", null);

        SwingWorker<User, Void> worker = new SwingWorker<User, Void>() {
            @Override
            protected User doInBackground() {
                return controller.login(username);
            }

            @Override
            protected void done() {
                setBothButtonsEnabled(true, "Login", "Register");
                try {
                    User user = get();
                    if (user != null) {
                        showStatus("✓ Welcome back, " + user.getUsername() + "!", true);
                        openDashboard(user);
                    } else {
                        showStatus("✗ Username not found. Please register first.", false);
                        usernameField.requestFocusInWindow();
                    }
                } catch (Exception ex) {
                    showStatus("An unexpected error occurred.", false);
                }
            }
        };
        worker.execute();
    }

    // ── Register logic ────────────────────────────────────────────────────

    private void handleRegister() {
        String username = usernameField.getText().trim();

        if (username.isEmpty()) {
            showStatus("Please enter a username to register.", false);
            return;
        }

        setBothButtonsEnabled(false, null, "Registering…");

        SwingWorker<User, Void> worker = new SwingWorker<User, Void>() {
            @Override
            protected User doInBackground() throws IOException {
                return controller.register(username);
            }

            @Override
            protected void done() {
                setBothButtonsEnabled(true, "Login", "Register");
                try {
                    User user = get();
                    if (user != null) {
                        showStatus("✓ Account created! Welcome, " + user.getUsername() + "!", true);
                        openDashboard(user);
                    } else {
                        showStatus("✗ Username \"" + username + "\" is already taken.", false);
                        usernameField.selectAll();
                        usernameField.requestFocusInWindow();
                    }
                } catch (Exception ex) {
                    showStatus("Failed to save account: " + ex.getMessage(), false);
                }
            }
        };
        worker.execute();
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private void setBothButtonsEnabled(boolean enabled, String loginText, String registerText) {
        loginButton.setEnabled(enabled);
        registerButton.setEnabled(enabled);
        if (loginText    != null) loginButton.setText(loginText);
        if (registerText != null) registerButton.setText(registerText);
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
