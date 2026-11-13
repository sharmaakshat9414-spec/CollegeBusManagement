package com.piet.busmanagement.ui.components;

import com.piet.busmanagement.config.UITheme;

import javax.swing.JButton;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;

/** Navigation button of the admin sidebar with active and hover states. */
public class SidebarButton extends JButton {

    private final IconFactory.Type iconType;
    private boolean active;

    public SidebarButton(String text, IconFactory.Type iconType) {
        super(text);
        this.iconType = iconType;
        setHorizontalAlignment(SwingConstants.LEFT);
        setIconTextGap(14);
        setMargin(new Insets(11, 16, 11, 12));
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setRolloverEnabled(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setFont(UITheme.font(Font.PLAIN, 14f));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        setAlignmentX(LEFT_ALIGNMENT);
        setActive(false);
    }

    public void setActive(boolean active) {
        this.active = active;
        Color c = active ? Color.WHITE : UITheme.SIDEBAR_TEXT;
        setForeground(c);
        setIcon(IconFactory.create(iconType, 18, c));
        setFont(UITheme.font(active ? Font.BOLD : Font.PLAIN, 14f));
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (active) {
            g2.setColor(UITheme.PRIMARY);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            g2.setColor(UITheme.CYAN);
            g2.fillRoundRect(0, 8, 4, getHeight() - 16, 4, 4);
        } else if (getModel().isRollover()) {
            g2.setColor(new Color(255, 255, 255, 22));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
        }
        g2.dispose();
        super.paintComponent(g);
    }
}
