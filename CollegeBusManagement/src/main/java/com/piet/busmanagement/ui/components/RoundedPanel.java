package com.piet.busmanagement.ui.components;

import com.piet.busmanagement.config.UITheme;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;

/** A white rounded card with a soft shadow. Put transparent child panels inside it. */
public class RoundedPanel extends JPanel {

    public static final int SHADOW = 5;

    private final Color background;
    private final int arc;
    private final boolean shadow;

    public RoundedPanel(LayoutManager layout, int padding) {
        this(layout, Color.WHITE, 18, true, padding);
    }

    public RoundedPanel(LayoutManager layout, Color background, int arc, boolean shadow, int padding) {
        super(layout);
        this.background = background;
        this.arc = arc;
        this.shadow = shadow;
        setOpaque(false);
        int inset = shadow ? SHADOW : 0;
        setBorder(new EmptyBorder(inset + padding, inset + padding, inset + padding + (shadow ? 1 : 0), inset + padding));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth();
        int h = getHeight();
        if (shadow) {
            for (int k = 0; k < SHADOW; k++) {
                g2.setColor(new Color(15, 23, 42, 7));
                g2.fillRoundRect(k, k + 1, w - 2 * k, h - 2 * k - 1, arc + 4, arc + 4);
            }
            g2.setColor(background);
            g2.fillRoundRect(SHADOW, SHADOW - 1, w - 2 * SHADOW, h - 2 * SHADOW, arc, arc);
            g2.setColor(UITheme.alpha(UITheme.BORDER, 160));
            g2.drawRoundRect(SHADOW, SHADOW - 1, w - 2 * SHADOW - 1, h - 2 * SHADOW - 1, arc, arc);
        } else {
            g2.setColor(background);
            g2.fillRoundRect(0, 0, w, h, arc, arc);
        }
        g2.dispose();
        super.paintComponent(g);
    }
}
