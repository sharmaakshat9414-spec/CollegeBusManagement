package com.piet.busmanagement.ui.dialogs;

import com.piet.busmanagement.config.UITheme;
import com.piet.busmanagement.ui.components.FormHelper;
import com.piet.busmanagement.ui.components.IconFactory;
import com.piet.busmanagement.ui.components.ModernButton;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.KeyEvent;

/**
 * Common look and behaviour of all form dialogs: navy header, two-column form, Save / Cancel.
 * Subclasses add their fields, call {@link #finish(Window)} and implement {@link #onSave()}.
 */
public abstract class BaseFormDialog extends JDialog {

    private final JPanel form = FormHelper.panel(new GridBagLayout());
    private int row = 0;
    private final ModernButton saveButton = new ModernButton("Save", ModernButton.Style.PRIMARY, IconFactory.Type.CHECK);

    protected BaseFormDialog(Window owner, String title, String subtitle) {
        super(owner, title, ModalityType.APPLICATION_MODAL);
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);

        JPanel header = new JPanel(new BorderLayout(0, 2));
        header.setBackground(UITheme.NAVY);
        header.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));
        JLabel t = new JLabel(title);
        t.setFont(UITheme.font(Font.BOLD, 18f));
        t.setForeground(Color.WHITE);
        JLabel s = new JLabel(subtitle);
        s.setFont(UITheme.font(Font.PLAIN, 12.5f));
        s.setForeground(UITheme.SIDEBAR_TEXT);
        header.add(t, BorderLayout.CENTER);
        header.add(s, BorderLayout.SOUTH);

        form.setBorder(BorderFactory.createEmptyBorder(18, 24, 8, 24));

        ModernButton cancel = new ModernButton("Cancel", ModernButton.Style.SECONDARY);
        cancel.addActionListener(e -> dispose());
        saveButton.addActionListener(e -> save());
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        footer.setBackground(new Color(0xF8FAFC));
        footer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)));
        footer.add(cancel);
        footer.add(saveButton);

        root.add(header, BorderLayout.NORTH);
        root.add(form, BorderLayout.CENTER);
        root.add(footer, BorderLayout.SOUTH);
        setContentPane(root);

        getRootPane().setDefaultButton(saveButton);
        getRootPane().registerKeyboardAction(e -> dispose(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    /** Adds a "Label : field" row. Mark required fields with a trailing * in the label. */
    protected void addField(String label, JComponent field) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = row++;
        c.insets = new Insets(6, 0, 6, 14);
        c.anchor = GridBagConstraints.NORTHWEST;
        c.gridx = 0;
        JLabel l = FormHelper.label(label);
        l.setPreferredSize(new java.awt.Dimension(140, 34));
        form.add(l, c);
        c.gridx = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(6, 0, 6, 0);
        c.ipadx = 230;
        form.add(field, c);
    }

    /** Call at the end of the subclass constructor. */
    protected void finish(Window owner) {
        pack();
        setSize(Math.max(getWidth(), 520), getHeight());
        setResizable(false);
        setLocationRelativeTo(owner);
    }

    private void save() {
        try {
            if (onSave()) {
                dispose();
            }
        } catch (Exception ex) {
            FormHelper.handleException(this, ex);
        }
    }

    /** Validate, save to the database and return true to close the dialog. */
    protected abstract boolean onSave() throws Exception;

    protected static String text(javax.swing.text.JTextComponent c) {
        return c.getText().trim();
    }
}
