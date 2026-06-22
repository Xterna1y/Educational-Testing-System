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
 * Admin login screen for the Educational Testing System.
 *
 * <p>Identical look and feel to {@link LoginFrame} but operates exclusively
 * on ADMIN-role accounts. Login only succeeds for users whose role is ADMIN;
 * Register creates new ADMIN accounts. A "← Back" link returns to the
 * Student login screen.</p>
 */
public class AdminLoginFrame extends JFrame {

    // ── Accent colours unique to the admin portal (amber / gold) ───────────
    private static final Color ACCENT        = Color.ORANGE;
    private static final Color ACCENT_HOVER  = Color.YELLOW;
    private static final Color ACCENT2       = Color.RED;
    private static final Color ACCENT2_HOVER = Color.PINK;

    // ── UI components ──────────────────────────────────────────────────────
    private JTextField usernameField;
    private JLabel     statusLabel;
    private JButton    loginButton;
    private JButton    registerButton;

    private final LoginController controller;

    // ─────────────────────────────────────────────────────────────────────
    public AdminLoginFrame(LoginController controller) {
        this.controller = controller;
        buildUI();
    }

    // ── UI construction ───────────────────────────────────────────────────

    private void buildUI() {
        setTitle("ETS – Admin Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(480, 540);
        setMinimumSize(new Dimension(400, 480));
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(LoginFrame.BG_DARK);
        setContentPane(root);
        root.add(buildCard());
    }

    private JPanel buildCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(LoginFrame.CARD_BG);
        card.setBorder(new EmptyBorder(48, 48, 36, 48));
        card.setPreferredSize(new Dimension(380, 450));

        // ── Icon
        JLabel icon = new JLabel("🔐", SwingConstants.CENTER);
        icon.setFont(new Font("Segue UI Emoji", Font.PLAIN, 56));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ── Title
        JLabel title = new JLabel("Admin Portal", SwingConstants.CENTER);
        title.setFont(new Font("Segue UI", Font.BOLD, 22));
        title.setForeground(LoginFrame.TEXT_PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ── Subtitle
        JLabel subtitle = new JLabel("Administrator accounts only", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segue UI", Font.PLAIN, 13));
        subtitle.setForeground(LoginFrame.TEXT_MUTED);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ── Username field
        usernameField = new JTextField(20);
        JPanel usernameGroup = buildFieldGroup(usernameField);

        // ── Status label
        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segue UI", Font.PLAIN, 13));
        statusLabel.setForeground(LoginFrame.ERROR_COLOR);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ── Main buttons
        JPanel buttonRow = buildButtonRow();

        // ── Back link
        JLabel backLink = buildBackLink();

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
        card.add(backLink);

        return card;
    }

    private JPanel buildFieldGroup(JTextField field) {
        JPanel group = new JPanel();
        group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));
        group.setBackground(LoginFrame.CARD_BG);
        group.setAlignmentX(Component.CENTER_ALIGNMENT);
        group.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JLabel lbl = new JLabel("Username");
        lbl.setFont(new Font("Segue UI", Font.BOLD, 13));
        lbl.setForeground(LoginFrame.TEXT_PRIMARY);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        field.setBackground(LoginFrame.FIELD_BG);
        field.setForeground(LoginFrame.TEXT_PRIMARY);
        field.setCaretColor(LoginFrame.TEXT_PRIMARY);
        field.setFont(new Font("Segue UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createEmptyBorder());
        field.setOpaque(false);

        // Enter key triggers login
        field.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "login");
        field.getActionMap().put("login", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { handleLogin(); }
        });

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(LoginFrame.FIELD_BG);
        wrapper.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LoginFrame.FIELD_BORDER, 1, true),
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
        loginButton    = LoginFrame.buildStyledButton("Login",    ACCENT,  ACCENT_HOVER);
        registerButton = LoginFrame.buildStyledButton("Register", ACCENT2, ACCENT2_HOVER);

        loginButton.addActionListener(e -> handleLogin());
        registerButton.addActionListener(e -> handleRegister());

        JPanel row = new JPanel(new GridLayout(1, 2, 12, 0));
        row.setBackground(LoginFrame.CARD_BG);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        row.setAlignmentX(Component.CENTER_ALIGNMENT);
        row.add(loginButton);
        row.add(registerButton);
        return row;
    }

    /** Thin horizontal rule separating the main actions from the back link. */
    private JPanel buildDivider() {
        JPanel line = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(LoginFrame.FIELD_BORDER);
                g.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2);
            }
        };
        line.setBackground(LoginFrame.CARD_BG);
        line.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        line.setAlignmentX(Component.CENTER_ALIGNMENT);
        return line;
    }

    /** Clickable "← Student Portal" label that returns to the Student login screen. */
    private JLabel buildBackLink() {
        JLabel link = new JLabel("←  Student Portal", SwingConstants.CENTER);
        link.setFont(new Font("Segue UI", Font.PLAIN, 12));
        link.setForeground(LoginFrame.TEXT_MUTED);
        link.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        link.setAlignmentX(Component.CENTER_ALIGNMENT);

        link.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                link.setForeground(new Color(99, 102, 241)); // indigo on hover
            }
            @Override public void mouseExited(MouseEvent e) {
                link.setForeground(LoginFrame.TEXT_MUTED);
            }
            @Override public void mouseClicked(MouseEvent e) {
                goBackToStudentLogin();
            }
        });
        return link;
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
                return controller.login(username, "ADMIN");
            }
            @Override protected void done() {
                setBothButtonsEnabled(true, "Login", "Register");
                try {
                    User user = get();
                    if (user != null) {
                        showStatus("✓ Welcome, Admin " + user.getUsername() + "!", true);
                        openAdminDashboard(user);
                    } else {
                        showStatus("✗ Admin username not found. Please register first.", false);
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
                return controller.register(username, "ADMIN");
            }
            @Override protected void done() {
                setBothButtonsEnabled(true, "Login", "Register");
                try {
                    User user = get();
                    if (user != null) {
                        showStatus("✓ Admin account created! Welcome, " + user.getUsername() + "!", true);
                        openAdminDashboard(user);
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

    private void openAdminDashboard(User user) {
        SwingUtilities.invokeLater(() -> {
            Timer timer = new Timer(700, e -> {
                AdminFrame adminFrame = new AdminFrame();
                adminFrame.setVisible(true);
                AdminLoginFrame.this.dispose();
            });
            timer.setRepeats(false);
            timer.start();
        });
    }

    /** Returns to the Student login screen, closing this frame. */
    private void goBackToStudentLogin() {
        SwingUtilities.invokeLater(() -> {
            LoginFrame studentLogin = new LoginFrame(controller);
            studentLogin.setVisible(true);
            AdminLoginFrame.this.dispose();
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
        statusLabel.setForeground(success ? LoginFrame.SUCCESS_COLOR : LoginFrame.ERROR_COLOR);
        statusLabel.setText(message);
    }
}
