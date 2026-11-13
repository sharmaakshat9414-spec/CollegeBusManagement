package com.piet.busmanagement.ui.components;

import com.piet.busmanagement.config.UITheme;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/** Welcome banner with a navy-to-blue gradient. */
public class BannerPanel extends JPanel {

    public BannerPanel(String title, String subtitle) {
        super(new BorderLayout(0, 4));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(22, 28, 22, 28));
        JLabel t = new JLabel(title);
        t.setFont(UITheme.font(Font.BOLD, 26f));
        t.setForeground(Color.WHITE);
        JLabel s = new JLabel(subtitle);
        s.setFont(UITheme.font(Font.PLAIN, 14f));
        s.setForeground(new Color(0xBFDBFE));
        add(t, BorderLayout.CENTER);
        add(s, BorderLayout.SOUTH);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setPaint(new GradientPaint(0, 0, UITheme.NAVY, getWidth(), getHeight(), UITheme.PRIMARY));
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
        g2.setColor(new Color(255, 255, 255, 18));
        g2.fillOval(getWidth() - 170, -60, 220, 220);
        g2.fillOval(getWidth() - 280, 40, 140, 140);
        g2.dispose();
        super.paintComponent(g);
    }
}
