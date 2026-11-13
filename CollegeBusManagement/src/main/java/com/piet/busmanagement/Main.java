package com.piet.busmanagement;

import com.piet.busmanagement.config.DBConnection;
import com.piet.busmanagement.config.UITheme;
import com.piet.busmanagement.ui.LoginFrame;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/** Application entry point: theme, database check, then the login screen. */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UITheme.install();
            while (true) {
                String error = DBConnection.testConnection();
                if (error == null) {
                    break;
                }
                Object[] options = {"Retry", "Exit"};
                int choice = JOptionPane.showOptionDialog(null,
                        "Cannot connect to the MySQL database '" + DBConnection.DATABASE + "'.\n\n"
                                + "Please check that:\n"
                                + "  1. MySQL server is running\n"
                                + "  2. database/college_bus_db.sql has been imported\n"
                                + "  3. USERNAME and PASSWORD in DBConnection.java are correct\n\n"
                                + "Details: " + error,
                        "Database Connection Error", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
                        null, options, options[0]);
                if (choice != 0) {
                    System.exit(0);
                    return;
                }
            }
            new LoginFrame().setVisible(true);
        });
    }
}
