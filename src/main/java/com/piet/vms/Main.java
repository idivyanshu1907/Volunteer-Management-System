package com.piet.vms;

import com.piet.vms.config.DatabaseInitializer;
import com.piet.vms.ui.LoginFrame;
import com.piet.vms.ui.Theme;

import javax.swing.*;

public final class Main {
    private Main() {}
    public static void main(String[] args) {
        Theme.install();
        SwingUtilities.invokeLater(() -> {
            try {
                DatabaseInitializer.initialize();
                new LoginFrame().setVisible(true);
            } catch (Exception e) {
                String message = "The Volunteer Management System could not initialize its database.\n\n" + e.getMessage() +
                        "\n\nStart MySQL Server and set VMS_DB_USER / VMS_DB_PASSWORD, then restart the application.\nSee README.md for setup instructions.";
                JOptionPane.showMessageDialog(null, message, "Database setup required", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
