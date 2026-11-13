package com.piet.busmanagement.ui.components;

import com.piet.busmanagement.config.AppException;
import com.piet.busmanagement.config.UITheme;

import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.Component;
import java.awt.Font;
import java.awt.LayoutManager;
import java.sql.SQLException;
import java.util.regex.Pattern;

/** Small helpers for building forms, validating input and showing messages. */
public final class FormHelper {

    private static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE = Pattern.compile("^(\\+91[- ]?)?[6-9][0-9]{9}$");

    private FormHelper() {
    }

    // ---------------- components ----------------

    /** A transparent panel (so it shows the card colour behind it). */
    public static JPanel panel(LayoutManager layout) {
        JPanel p = new JPanel(layout);
        p.setOpaque(false);
        return p;
    }

    public static JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UITheme.font(Font.BOLD, 12.5f));
        l.setForeground(UITheme.TEXT);
        return l;
    }

    public static JLabel muted(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UITheme.font(Font.PLAIN, 13f));
        l.setForeground(UITheme.MUTED);
        return l;
    }

    public static JLabel heading(String text, float size) {
        JLabel l = new JLabel(text);
        l.setFont(UITheme.font(Font.BOLD, size));
        l.setForeground(UITheme.NAVY);
        return l;
    }

    public static JTextField textField(String placeholder, IconFactory.Type icon) {
        JTextField f = new JTextField();
        f.putClientProperty("JTextField.placeholderText", placeholder);
        if (icon != null) {
            f.putClientProperty("JTextField.leadingIcon", IconFactory.create(icon, 16, UITheme.MUTED));
        }
        return f;
    }

    public static JPasswordField passwordField(String placeholder) {
        JPasswordField f = new JPasswordField();
        f.putClientProperty("JTextField.placeholderText", placeholder);
        f.putClientProperty("JTextField.leadingIcon", (Icon) IconFactory.create(IconFactory.Type.LOCK, 16, UITheme.MUTED));
        f.putClientProperty("JPasswordField.showRevealButton", true);
        return f;
    }

    public static JComboBox<String> combo(String... items) {
        return new JComboBox<>(items);
    }

    public static void selectItem(JComboBox<ComboItem> box, int id) {
        for (int i = 0; i < box.getItemCount(); i++) {
            if (box.getItemAt(i).id() == id) {
                box.setSelectedIndex(i);
                return;
            }
        }
    }

    // ---------------- validation ----------------

    public static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static boolean isEmail(String s) {
        return s != null && EMAIL.matcher(s.trim()).matches();
    }

    public static boolean isPhone(String s) {
        return s != null && PHONE.matcher(s.trim()).matches();
    }

    // ---------------- messages ----------------

    public static void info(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void error(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void warn(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Please check", JOptionPane.WARNING_MESSAGE);
    }

    public static boolean confirm(Component parent, String message, String title) {
        return JOptionPane.showConfirmDialog(parent, message, title, JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION;
    }

    public static boolean confirmDelete(Component parent) {
        return confirm(parent, "Are you sure you want to delete this record?", "Confirm Delete");
    }

    /** Turns any exception into a friendly message. */
    public static void handleException(Component parent, Exception ex) {
        if (ex instanceof AppException) {
            warn(parent, ex.getMessage());
        } else if (ex instanceof SQLException sql) {
            if (sql.getErrorCode() == 1451 || sql.getErrorCode() == 1217
                    || (sql instanceof java.sql.SQLIntegrityConstraintViolationException
                    && String.valueOf(sql.getMessage()).toLowerCase().contains("foreign key"))) {
                error(parent, "Unable to delete this record because it is currently in use.");
            } else if (sql.getErrorCode() == 1062) {
                error(parent, "This record already exists (duplicate value).");
            } else if (sql.getSQLState() != null && sql.getSQLState().startsWith("08")) {
                error(parent, "Cannot connect to the MySQL database. Please check that MySQL is running and the "
                        + "username/password in DBConnection.java are correct.");
            } else {
                error(parent, "A database error occurred:\n" + sql.getMessage());
            }
        } else {
            error(parent, "Something went wrong:\n" + ex.getMessage());
        }
    }
}
