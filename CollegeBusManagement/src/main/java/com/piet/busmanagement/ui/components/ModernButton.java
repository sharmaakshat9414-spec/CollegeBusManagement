package com.piet.busmanagement.ui.components;

import com.piet.busmanagement.config.UITheme;

import javax.swing.JButton;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;

/** Rounded button with hover and pressed effects. */
public class ModernButton extends JButton {

    public enum Style { PRIMARY, SECONDARY, SUCCESS, DANGER, GHOST }

    private final Style style;

    public ModernButton(String text, Style style) {
        this(text, style, null);
    }

    public ModernButton(String text, Style style, IconFactory.Type icon) {
        super(text);
        this.style = style;
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setRolloverEnabled(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setFont(UITheme.font(Font.BOLD, 13f));
        setMargin(new Insets(8, 16, 8, 16));
        Color fg = (style == Style.SECONDARY || style == Style.GHOST) ? UITheme.NAVY : Color.WHITE;
        setForeground(fg);
        if (icon != null) {
            setIcon(IconFactory.create(icon, 16, fg));
            setIconTextGap(8);
        }
    }

    private Color base() {
        return switch (style) {
            case PRIMARY -> UITheme.PRIMARY;
            case SUCCESS -> UITheme.SUCCESS;
            case DANGER -> UITheme.DANGER;
            case SECONDARY -> Color.WHITE;
            case GHOST -> UITheme.alpha(UITheme.PRIMARY, 0);
        };
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color fill = base();
        boolean light = style == Style.SECONDARY || style == Style.GHOST;
        if (!isEnabled()) {
            fill = new Color(0xCBD5E1);
        } else if (getModel().isPressed()) {
            fill = light ? new Color(0xE2E8F0) : fill.darker();
        } else if (getModel().isRollover()) {
            fill = light ? new Color(0xEEF2F7) : blend(fill, Color.WHITE, 0.14f);
        }
        g2.setColor(fill);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
        if (style == Style.SECONDARY) {
            g2.setColor(UITheme.BORDER);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
        }
        g2.dispose();
        super.paintComponent(g);
    }

    private static Color blend(Color a, Color b, float t) {
        return new Color((int) (a.getRed() + (b.getRed() - a.getRed()) * t),
                (int) (a.getGreen() + (b.getGreen() - a.getGreen()) * t),
                (int) (a.getBlue() + (b.getBlue() - a.getBlue()) * t));
    }
}
