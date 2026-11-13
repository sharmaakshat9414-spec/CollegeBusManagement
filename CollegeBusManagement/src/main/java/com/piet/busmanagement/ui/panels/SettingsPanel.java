package com.piet.busmanagement.ui.panels;

import com.piet.busmanagement.config.Session;
import com.piet.busmanagement.ui.components.FormHelper;
import com.piet.busmanagement.ui.components.IconFactory;
import com.piet.busmanagement.ui.components.InfoCard;
import com.piet.busmanagement.ui.components.ModernButton;
import com.piet.busmanagement.ui.components.RoundedPanel;
import com.piet.busmanagement.ui.dialogs.AboutDialog;
import com.piet.busmanagement.ui.dialogs.ChangePasswordDialog;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

/** Admin settings: account info, Change Password and About Project. */
public class SettingsPanel extends JPanel {

    public SettingsPanel() {
        setLayout(new BorderLayout(0, 18));
        setOpaque(false);
        setBorder(new EmptyBorder(24, 28, 24, 28));

        JPanel titles = FormHelper.panel(new BorderLayout(0, 2));
        titles.add(FormHelper.heading("Settings", 24f), BorderLayout.CENTER);
        titles.add(FormHelper.muted("Manage your account and view project information"), BorderLayout.SOUTH);
        add(titles, BorderLayout.NORTH);

        InfoCard account = new InfoCard("My Account", IconFactory.Type.USER);
        account.addRow("Name", Session.getUser().fullName());
        account.addRow("Username", Session.getUser().username());
        account.addRow("Email", Session.getUser().email());
        account.addRow("Role", Session.getUser().role());

        ModernButton changePw = new ModernButton("Change Password", ModernButton.Style.PRIMARY, IconFactory.Type.LOCK);
        changePw.addActionListener(e -> new ChangePasswordDialog(SwingUtilities.getWindowAncestor(this)).setVisible(true));
        ModernButton about = new ModernButton("About Project", ModernButton.Style.SECONDARY, IconFactory.Type.NOTICE);
        about.addActionListener(e -> new AboutDialog(SwingUtilities.getWindowAncestor(this)).setVisible(true));

        RoundedPanel actions = new RoundedPanel(new BorderLayout(0, 12), 20);
        JLabel t = FormHelper.heading("  Security & Information", 16f);
        t.setIcon(IconFactory.create(IconFactory.Type.SETTINGS, 20, com.piet.busmanagement.config.UITheme.PRIMARY));
        JPanel buttons = FormHelper.panel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttons.add(changePw);
        buttons.add(about);
        JPanel inner = FormHelper.panel(new BorderLayout(0, 10));
        inner.add(FormHelper.muted("Change your admin password or view information about this project."), BorderLayout.NORTH);
        inner.add(buttons, BorderLayout.CENTER);
        actions.add(t, BorderLayout.NORTH);
        actions.add(inner, BorderLayout.CENTER);

        JPanel grid = FormHelper.panel(new GridLayout(1, 2, 18, 0));
        grid.add(account);
        grid.add(actions);
        JPanel wrap = FormHelper.panel(new BorderLayout());
        wrap.add(grid, BorderLayout.NORTH);
        add(wrap, BorderLayout.CENTER);
    }
}
