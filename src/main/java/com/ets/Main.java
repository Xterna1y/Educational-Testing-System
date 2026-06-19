package com.ets;

import com.ets.controller.LoginController;
import com.ets.gui.LoginFrame;
import com.ets.repo.UserRepository;
import com.ets.services.AuthenticationService;

import javax.swing.*;

/**
 * Application entry point for the Educational Testing System.
 *
 * <p>Wires up the dependency graph and launches the {@link LoginFrame}
 * on the Swing Event Dispatch Thread.</p>
 */
public class Main {

    public static void main(String[] args) {
        // ── Dependency wiring ────────────────────────────────────────────
        UserRepository       userRepo    = new UserRepository();
        AuthenticationService authService = new AuthenticationService(userRepo);
        LoginController      controller  = new LoginController(authService);

        // ── Launch UI on the EDT ─────────────────────────────────────────
        SwingUtilities.invokeLater(() -> {
            // Use system look-and-feel for native window decorations
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Fall back to default Swing look-and-feel
            }

            LoginFrame loginFrame = new LoginFrame(controller);
            loginFrame.setVisible(true);
        });
    }
}
