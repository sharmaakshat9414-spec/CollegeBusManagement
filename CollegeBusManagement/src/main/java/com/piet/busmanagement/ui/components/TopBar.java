package com.piet.busmanagement.ui.components;

import com.piet.busmanagement.config.UITheme;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

/** Header used by the student and driver dashboards. */
public class TopBar extends JPanel {

    public TopBar(String roleLabel, String userName, Runnable onChangePassword, Runnable onLogout) {
        super(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(12, 28, 12, 28));

        JLabel brand = new JLabel("  College Bus Tracking & Route Management");
        brand.setIcon(IconFactory.create(IconFactory.Type.BUS, 26, UITheme.CYAN));
        brand.setFont(UITheme.font(Font.BOLD, 17f));
        brand.setForeground(java.awt.Color.WHITE);

        JLabel user = new JLabel(userName + "  |  " + roleLabel + "   ");
        user.setIcon(IconFactory.create(IconFactory.Type.USER, 18, UITheme.SIDEBAR_TEXT));
        user.setForeground(UITheme.SIDEBAR_TEXT);
        user.setFont(UITheme.font(Font.PLAIN, 13f));

        ModernButton changePw = new ModernButton("Change Password", ModernButton.Style.SECONDARY, IconFactory.Type.LOCK);
        ModernButton logout = new ModernButton("Logout", ModernButton.Style.DANGER, IconFactory.Type.LOGOUT);
        changePw.addActionListener(e -> onChangePassword.run());
        logout.addActionListener(e -> onLogout.run());

        JPanel right = FormHelper.panel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.add(user);
        right.add(changePw);
        right.add(logout);

        add(brand, BorderLayout.WEST);
        add(right, BorderLayout.EAST);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setPaint(new GradientPaint(0, 0, UITheme.NAVY, getWidth(), 0, UITheme.NAVY_MID));
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
    }
}
