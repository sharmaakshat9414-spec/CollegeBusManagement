package com.piet.busmanagement.ui.components;

import com.piet.busmanagement.config.UITheme;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/** Card with a title and "label : value" rows (profile and transport details). */
public class InfoCard extends RoundedPanel {

    private final JPanel body = FormHelper.panel(new GridBagLayout());
    private int row = 0;

    public InfoCard(String title, IconFactory.Type icon) {
        super(new BorderLayout(0, 12), 20);
        JLabel header = new JLabel("  " + title);
        header.setIcon(IconFactory.create(icon, 20, UITheme.PRIMARY));
        header.setFont(UITheme.font(Font.BOLD, 16f));
        header.setForeground(UITheme.NAVY);
        header.setBorder(new EmptyBorder(0, 0, 4, 0));
        add(header, BorderLayout.NORTH);
        JPanel top = FormHelper.panel(new BorderLayout());
        top.add(body, BorderLayout.NORTH);
        add(top, BorderLayout.CENTER);
    }

    public void addRow(String label, String value) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = row++;
        c.insets = new Insets(5, 0, 5, 16);
        c.anchor = GridBagConstraints.NORTHWEST;
        c.gridx = 0;
        body.add(FormHelper.muted(label), c);

        String text = value == null || value.isBlank() ? "-" : value;
        JLabel v = new JLabel(text.length() > 38
                ? "<html><div style='width:260px'>" + text.replace("<", "&lt;") + "</div></html>" : text);
        v.setFont(UITheme.font(Font.BOLD, 13.5f));
        v.setForeground(UITheme.TEXT);
        c.gridx = 1;
        c.weightx = 1;
        c.insets = new Insets(5, 0, 5, 0);
        body.add(v, c);
    }
}
