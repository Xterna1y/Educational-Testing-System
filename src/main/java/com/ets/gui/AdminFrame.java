package com.ets.gui;

import javax.swing.*;

/**
 * The main administration window of the Educational Testing System.
 * <p>
 * {@code AdminFrame} is a Swing {@link javax.swing.JFrame} that serves as the
 * primary graphical interface for administrators. It provides the entry point
 * into all admin-level functionality such as managing exams, questions, and
 * student accounts.
 * </p>
 *
 * @author ETS Team
 * @version 1.0
 */
public class AdminFrame extends JFrame {

    /**
     * Constructs and initialises the {@code AdminFrame} window.
     * <p>
     * Sets the window title, default size, screen-centred position, and the
     * close behaviour. An empty {@link javax.swing.JPanel} is added as a
     * placeholder for future UI components.
     * </p>
     */
    public AdminFrame() {
        setTitle("Admin Frame");
        setSize(400, 300);
        setLocationRelativeTo(null);
        // finish application with exit button
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        add(panel);
    }

    /**
     * Application entry point that launches the {@code AdminFrame} on the
     * Swing Event Dispatch Thread (EDT).
     * <p>
     * Using {@link SwingUtilities#invokeLater(Runnable)} ensures that all GUI
     * creation and updates are performed on the EDT, as required by Swing's
     * single-threading model.
     * </p>
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args){

//        Create GUI on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            AdminFrame adminFrame = new AdminFrame();
            adminFrame.setVisible(true);
        });
    }
}

