package com.piet.busmanagement.ui.components;

import com.piet.busmanagement.config.UITheme;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/** Dashboard statistic card: coloured icon, big number and caption. */
public class StatCard extends RoundedPanel {

    private final JLabel valueLabel = new JLabel("0");

    public StatCard(String title, IconFactory.Type icon, Color accent) {
        super(new BorderLayout(10, 0), 12);
        JComponent badge = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.alpha(accent, 32));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                int s = 24;
                IconFactory.create(icon, s, accent).paintIcon(this, g2, (getWidth() - s) / 2, (getHeight() - s) / 2);
                g2.dispose();
            }
        };
        badge.setPreferredSize(new Dimension(46, 46));

        valueLabel.setFont(UITheme.font(Font.BOLD, 26f));
        valueLabel.setForeground(UITheme.NAVY);
        JLabel caption = new JLabel(title.length() > 14 ? "<html>" + title.replace(" ", "<br>") + "</html>" : title);
        caption.setFont(UITheme.font(Font.PLAIN, 13f));
        caption.setForeground(UITheme.MUTED);

        JPanel text = FormHelper.panel(new BorderLayout(0, 0));
        text.add(valueLabel, BorderLayout.CENTER);
        text.add(caption, BorderLayout.SOUTH);

        add(badge, BorderLayout.WEST);
        add(text, BorderLayout.CENTER);
    }

    public void setValue(String value) {
        valueLabel.setText(value);
    }
}
