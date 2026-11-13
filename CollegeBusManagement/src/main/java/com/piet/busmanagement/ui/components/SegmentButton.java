package com.piet.busmanagement.ui.components;

import com.piet.busmanagement.config.UITheme;

import javax.swing.JToggleButton;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/** One option of a segmented selector (used for choosing the login role). */
public class SegmentButton extends JToggleButton {

    public SegmentButton(String text) {
        super(text);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setRolloverEnabled(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setFont(UITheme.font(Font.BOLD, 13f));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (isSelected()) {
            g2.setColor(UITheme.PRIMARY);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            setForeground(Color.WHITE);
        } else {
            g2.setColor(getModel().isRollover() ? new Color(0xE2E8F0) : new Color(0xF1F5F9));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            setForeground(UITheme.MUTED);
        }
        g2.dispose();
        super.paintComponent(g);
    }
}
