package com.piet.busmanagement.ui;

import com.piet.busmanagement.dao.StudentDAO;
import com.piet.busmanagement.config.UITheme;
import com.piet.busmanagement.model.Student;
import com.piet.busmanagement.ui.components.FormHelper;
import com.piet.busmanagement.ui.components.IconFactory;
import com.piet.busmanagement.ui.components.ModernButton;
import com.piet.busmanagement.ui.components.RoundedPanel;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

/** Student self-registration screen. */
public class StudentSignupFrame extends JFrame {

    private final StudentDAO studentDAO = new StudentDAO();
    private final JTextField fullName = FormHelper.textField("Full name", IconFactory.Type.USER);
    private final JTextField regNo = FormHelper.textField("e.g. PIET24CS101", IconFactory.Type.BADGE);
    private final JTextField email = FormHelper.textField("you@example.com", IconFactory.Type.MAIL);
    private final JTextField phone = FormHelper.textField("10-digit mobile number", IconFactory.Type.PHONE);
    private final JTextField section = FormHelper.textField("e.g. CSE-A", IconFactory.Type.DASHBOARD);
    private final JPasswordField password = FormHelper.passwordField("At least 6 characters");
    private final JPasswordField confirm = FormHelper.passwordField("Repeat password");

    public StudentSignupFrame() {
        super("Student Sign Up - College Bus Tracking & Route Management System");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setIconImage(IconFactory.appImage());
        setMinimumSize(new Dimension(820, 720));
        setSize(980, 780);

        JPanel background = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, UITheme.NAVY, getWidth(), getHeight(), UITheme.NAVY_LIGHT));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };

        RoundedPanel card = new RoundedPanel(new BorderLayout(0, 18), 28);
        card.setPreferredSize(new Dimension(640, 640));

        JPanel titles = FormHelper.panel(new BorderLayout(0, 4));
        titles.add(FormHelper.heading("Create Student Account", 26f), BorderLayout.CENTER);
        titles.add(FormHelper.muted("Register once, then log in to see your bus, route and notices"), BorderLayout.SOUTH);

        JPanel form = FormHelper.panel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.insets = new Insets(6, 6, 6, 6);
        addBlock(form, c, 0, 0, 1, "Full Name *", fullName);
        addBlock(form, c, 1, 0, 1, "Registration Number *", regNo);
        addBlock(form, c, 0, 1, 1, "Email *", email);
        addBlock(form, c, 1, 1, 1, "Phone Number *", phone);
        addBlock(form, c, 0, 2, 2, "Section *", section);
        addBlock(form, c, 0, 3, 1, "Password *", password);
        addBlock(form, c, 1, 3, 1, "Confirm Password *", confirm);

        ModernButton create = new ModernButton("Create Account", ModernButton.Style.PRIMARY, IconFactory.Type.CHECK);
        create.setMargin(new Insets(11, 18, 11, 18));
        create.addActionListener(e -> createAccount());
        ModernButton back = new ModernButton("Back to Login", ModernButton.Style.SECONDARY);
        back.setMargin(new Insets(11, 18, 11, 18));
        back.addActionListener(e -> backToLogin());
        JPanel buttons = FormHelper.panel(new GridLayout(1, 2, 12, 0));
        buttons.add(create);
        buttons.add(back);
        getRootPane().setDefaultButton(create);

        card.add(titles, BorderLayout.NORTH);
        card.add(form, BorderLayout.CENTER);
        card.add(buttons, BorderLayout.SOUTH);
        background.add(card);
        setContentPane(background);
        setLocationRelativeTo(null);
    }

    private void addBlock(JPanel form, GridBagConstraints c, int x, int y, int width, String label, JComponent field) {
        JPanel block = FormHelper.panel(new BorderLayout(0, 5));
        block.add(FormHelper.label(label), BorderLayout.NORTH);
        block.add(field, BorderLayout.CENTER);
        c.gridx = x;
        c.gridy = y;
        c.gridwidth = width;
        form.add(block, c);
    }

    private void backToLogin() {
        new LoginFrame().setVisible(true);
        dispose();
    }

    private void createAccount() {
        String pw = new String(password.getPassword());
        if (FormHelper.isBlank(fullName.getText()) || FormHelper.isBlank(regNo.getText()) || FormHelper.isBlank(email.getText())
                || FormHelper.isBlank(phone.getText()) || FormHelper.isBlank(section.getText()) || pw.isEmpty()
                || confirm.getPassword().length == 0) {
            FormHelper.warn(this, "Please fill all required fields.");
            return;
        }
        if (!FormHelper.isEmail(email.getText())) {
            FormHelper.warn(this, "Please enter a valid email address.");
            return;
        }
        if (!FormHelper.isPhone(phone.getText())) {
            FormHelper.warn(this, "Please enter a valid 10-digit phone number.");
            return;
        }
        if (pw.length() < 6) {
            FormHelper.warn(this, "Password must be at least 6 characters.");
            return;
        }
        if (!pw.equals(new String(confirm.getPassword()))) {
            FormHelper.warn(this, "Password and confirm password do not match.");
            return;
        }
        try {
            Student s = new Student(0, 0, fullName.getText().trim(), regNo.getText().trim().toUpperCase(),
                    email.getText().trim().toLowerCase(), phone.getText().trim(), section.getText().trim().toUpperCase(),
                    "ACTIVE", null, null, null);
            studentDAO.register(s, pw);
            FormHelper.info(this, "Student account created successfully.");
            backToLogin();
        } catch (Exception ex) {
            FormHelper.handleException(this, ex);
        }
    }
}
