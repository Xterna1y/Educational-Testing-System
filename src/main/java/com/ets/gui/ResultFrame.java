package com.ets.gui;

import javax.swing.*;
import java.awt.*;

public class ResultFrame extends JFrame {
    private static final Color BG_DARK      = Color.BLACK;
    private static final Color CARD_BG      = Color.DARK_GRAY;
    private static final Color ACCENT       = Color.BLUE;
    private static final Color TEXT_PRIMARY = Color.WHITE;
    private static final Color TEXT_MUTED   = Color.LIGHT_GRAY;
    private final String username;


    public ResultFrame(String username) {
        this.username = username;
        buildUI();
    }

    private void buildUI() {
        setTitle("ResultFrame");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
