package com.piet.busmanagement.ui.dialogs;

import com.piet.busmanagement.config.UITheme;
import com.piet.busmanagement.ui.components.FormHelper;
import com.piet.busmanagement.ui.components.IconFactory;
import com.piet.busmanagement.ui.components.ModernButton;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;

public class AboutDialog extends JDialog {

    public AboutDialog(Window owner) {
        super(owner, "About Project", ModalityType.APPLICATION_MODAL);
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);

        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setBackground(UITheme.NAVY);
        top.setBorder(BorderFactory.createEmptyBorder(26, 30, 22, 30));
        JLabel icon = new JLabel(IconFactory.create(IconFactory.Type.BUS, 44, UITheme.CYAN));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);
        top.add(icon);
        top.add(javax.swing.Box.createVerticalStrut(10));
        top.add(centered("College Bus Tracking & Route Management System", 17f, Font.BOLD, Color.WHITE));
        top.add(javax.swing.Box.createVerticalStrut(4));
        top.add(centered("Version 1.0.0", 12.5f, Font.PLAIN, UITheme.SIDEBAR_TEXT));

        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(Color.WHITE);
        body.setBorder(BorderFactory.createEmptyBorder(22, 30, 10, 30));
        body.add(centered("Poornima Institute of Engineering and Technology", 14f, Font.BOLD, UITheme.NAVY));
        body.add(javax.swing.Box.createVerticalStrut(6));
        body.add(centered("Jaipur, Rajasthan", 13f, Font.PLAIN, UITheme.MUTED));
        body.add(javax.swing.Box.createVerticalStrut(14));
        body.add(centered("Java Swing + MySQL + JDBC", 13.5f, Font.BOLD, UITheme.PRIMARY));
        body.add(javax.swing.Box.createVerticalStrut(6));
        body.add(centered("Manages buses, routes, drivers, students, assignments and notices.", 12.5f, Font.PLAIN, UITheme.MUTED));

        ModernButton close = new ModernButton("Close", ModernButton.Style.PRIMARY);
        close.addActionListener(e -> dispose());
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setBackground(Color.WHITE);
        footer.setBorder(BorderFactory.createEmptyBorder(8, 0, 18, 0));
        footer.add(close);

        root.add(top, BorderLayout.NORTH);
        root.add(body, BorderLayout.CENTER);
        root.add(footer, BorderLayout.SOUTH);
        setContentPane(root);
        pack();
        setSize(Math.max(getWidth(), 520), getHeight());
        setResizable(false);
        setLocationRelativeTo(owner);
    }

    private static JLabel centered(String text, float size, int style, Color color) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setFont(UITheme.font(style, size));
        l.setForeground(color);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        return l;
    }
}
