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
 * Student login screen for the Educational Testing System.
 *
 * <p>Presents a username field and two buttons — <em>Login</em> and
 * <em>Register</em> — for STUDENT accounts only. An "Admin Portal" link
 * at the bottom opens {@link AdminLoginFrame} for administrator access.</p>
 */
public class LoginFrame extends JFrame {

    // ── Colours (package-visible so AdminLoginFrame can reuse them) ─────────
    static final Color BG_DARK       = Color.BLACK;
    static final Color CARD_BG       = Color.DARK_GRAY;
    static final Color TEXT_PRIMARY  = Color.WHITE;
    static final Color TEXT_MUTED    = Color.LIGHT_GRAY;
    static final Color FIELD_BG      = Color.DARK_GRAY;
    static final Color FIELD_BORDER  = Color.GRAY;
    static final Color ERROR_COLOR   = Color.RED;
    static final Color SUCCESS_COLOR = Color.GREEN;

    private static final Color ACCENT        = Color.BLUE;    // similar to Indigo
    private static final Color ACCENT_HOVER  = Color.BLUE;
    private static final Color ACCENT2       = Color.GREEN;   // similar to Emerald
    private static final Color ACCENT2_HOVER = Color.GREEN;

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
        setTitle("ETS – Student Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(480, 540);
        setMinimumSize(new Dimension(400, 480));
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(BG_DARK);
        setContentPane(root);
        root.add(buildCard());
    }

    private JPanel buildCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_BG);
        card.setBorder(new EmptyBorder(48, 48, 36, 48));
        card.setPreferredSize(new Dimension(380, 450));

        // ── Icon
        JLabel icon = new JLabel("🎓", SwingConstants.CENTER);
        icon.setFont(new Font("Segue UI Emoji", Font.PLAIN, 56));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ── Title
        JLabel title = new JLabel("Student Portal", SwingConstants.CENTER);
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

        // ── Status label
        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segue UI", Font.PLAIN, 13));
        statusLabel.setForeground(ERROR_COLOR);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ── Main buttons
        JPanel buttonRow = buildButtonRow();

        // ── Admin portal link
        JLabel adminLink = buildAdminLink();

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
        card.add(Box.createVerticalStrut(24));
        card.add(buildDivider());
        card.add(Box.createVerticalStrut(16));
        card.add(adminLink);

        return card;
    }

    private JPanel buildFieldGroup(JTextField field) {
        JPanel group = new JPanel();
        group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));
        group.setBackground(CARD_BG);
        group.setAlignmentX(Component.CENTER_ALIGNMENT);
        group.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JLabel lbl = new JLabel("Username");
        lbl.setFont(new Font("Segue UI", Font.BOLD, 13));
        lbl.setForeground(TEXT_PRIMARY);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        field.setBackground(FIELD_BG);
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(TEXT_PRIMARY);
        field.setFont(new Font("Segue UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createEmptyBorder());
        field.setOpaque(false);

        // Enter key triggers login
        field.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "login");
        field.getActionMap().put("login", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { handleLogin(); }
        });

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

    /** Thin horizontal rule separating the main actions from the admin link. */
    private JPanel buildDivider() {
        JPanel line = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(FIELD_BORDER);
                g.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2);
            }
        };
        line.setBackground(CARD_BG);
        line.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        line.setAlignmentX(Component.CENTER_ALIGNMENT);
        return line;
    }

    /** Clickable "Admin Portal →" label that opens the Admin login screen. */
    private JLabel buildAdminLink() {
        JLabel link = new JLabel("Admin Portal  →", SwingConstants.CENTER);
        link.setFont(new Font("Segue UI", Font.PLAIN, 12));
        link.setForeground(TEXT_MUTED);
        link.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        link.setAlignmentX(Component.CENTER_ALIGNMENT);

        link.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                link.setForeground(new Color(245, 158, 11)); // amber on hover
            }
            @Override public void mouseExited(MouseEvent e) {
                link.setForeground(TEXT_MUTED);
            }
            @Override public void mouseClicked(MouseEvent e) {
                openAdminLogin();
            }
        });
        return link;
    }

    /**
     * Builds a rounded, coloured action button.
     * Package-visible so {@link AdminLoginFrame} can reuse the same style.
     */
    static JButton buildStyledButton(String text, Color base, Color hover) {
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
            @Override protected User doInBackground() {
                return controller.login(username, "STUDENT");
            }
            @Override protected void done() {
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
            @Override protected User doInBackground() throws IOException {
                return controller.register(username, "STUDENT");
            }
            @Override protected void done() {
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

    // ── Navigation ────────────────────────────────────────────────────────

    /** Switches to the Admin login screen, closing this frame. */
    private void openAdminLogin() {
        SwingUtilities.invokeLater(() -> {
            AdminLoginFrame adminLogin = new AdminLoginFrame(controller);
            adminLogin.setVisible(true);
            LoginFrame.this.dispose();
        });
    }

    private void openDashboard(User user) {
        SwingUtilities.invokeLater(() -> {
            Timer timer = new Timer(700, e -> {
                DashboardFrame dashboardFrame = new DashboardFrame(user.getUsername());
                dashboardFrame.setVisible(true);
                LoginFrame.this.dispose();
            });
            timer.setRepeats(false);
            timer.start();
        });
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
}
