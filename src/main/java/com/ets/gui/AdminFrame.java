package com.ets.gui;

import javax.swing.*;

public class AdminFrame extends JFrame {
    public AdminFrame() {
        setTitle("Admin Frame");
        setSize(400, 300);
        setLocationRelativeTo(null);
        // finish application with exit button
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        add(panel);
    }

    public static void main(String[] args){

//        Create GUI on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            AdminFrame adminFrame = new AdminFrame();
            adminFrame.setVisible(true);
        });
    }
}
