package com.piet.busmanagement.ui;

import com.piet.busmanagement.config.Session;
import com.piet.busmanagement.config.UITheme;
import com.piet.busmanagement.dao.UserDAO;
import com.piet.busmanagement.model.User;
import com.piet.busmanagement.ui.components.FormHelper;
import com.piet.busmanagement.ui.components.IconFactory;
import com.piet.busmanagement.ui.components.ModernButton;
import com.piet.busmanagement.ui.components.RoundedPanel;
import com.piet.busmanagement.ui.components.SegmentButton;
import com.piet.busmanagement.ui.dialogs.ForgotPasswordDialog;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Window;

/** Split-screen login: branding on the left, login card on the right. */
public class LoginFrame extends JFrame {

    private final UserDAO userDAO = new UserDAO();
    private final SegmentButton adminBtn = new SegmentButton("ADMIN");
    private final SegmentButton studentBtn = new SegmentButton("STUDENT");
    private final SegmentButton driverBtn = new SegmentButton("DRIVER");
    private final JTextField username = FormHelper.textField("Username / Email / Registration No.", IconFactory.Type.USER);
    private final JPasswordField password = FormHelper.passwordField("Password");

    public LoginFrame() {
        super("College Bus Tracking & Route Management System - Login");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setIconImage(IconFactory.appImage());
        setMinimumSize(new Dimension(920, 600));
        setSize(1080, 680);

        JPanel root = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1;
        c.gridx = 0;
        c.weightx = 0.55;
        root.add(new BrandingPanel(), c);
        c.gridx = 1;
        c.weightx = 0.45;
        root.add(buildLoginSide(), c);
        setContentPane(root);
        setLocationRelativeTo(null);
        getRootPane().setDefaultButton(null);
    }

    private JPanel buildLoginSide() {
        JPanel side = new JPanel(new GridBagLayout());
        side.setBackground(new Color(0xF8FAFC));

        RoundedPanel card = new RoundedPanel(new GridBagLayout(), 30);
        card.setPreferredSize(new Dimension(420, 580));

        ButtonGroup group = new ButtonGroup();
        group.add(adminBtn);
        group.add(studentBtn);
        group.add(driverBtn);
        adminBtn.setSelected(true);
        JPanel roles = FormHelper.panel(new GridLayout(1, 3, 8, 0));
        roles.add(adminBtn);
        roles.add(studentBtn);
        roles.add(driverBtn);

        ModernButton login = new ModernButton("Login", ModernButton.Style.PRIMARY, IconFactory.Type.LOGOUT);
        login.setMargin(new Insets(11, 16, 11, 16));
        login.addActionListener(e -> doLogin());
        ModernButton forgot = new ModernButton("Forgot password?", ModernButton.Style.GHOST);
        forgot.addActionListener(e -> new ForgotPasswordDialog(this).setVisible(true));
        ModernButton signup = new ModernButton("Student Sign Up", ModernButton.Style.SECONDARY, IconFactory.Type.ALLOCATE);
        signup.addActionListener(e -> {
            new StudentSignupFrame().setVisible(true);
            dispose();
        });
        ModernButton exit = new ModernButton("Exit", ModernButton.Style.GHOST, IconFactory.Type.POWER);
        exit.addActionListener(e -> System.exit(0));
        getRootPane().setDefaultButton(login);

        JPanel bottom = FormHelper.panel(new GridLayout(2, 1, 0, 8));
        bottom.add(signup);
        bottom.add(exit);

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.NORTH;
        int y = 0;

        g.gridy = y++;
        g.insets = new Insets(0, 0, 4, 0);
        card.add(FormHelper.heading("Welcome Back", 28f), g);
        g.gridy = y++;
        g.insets = new Insets(0, 0, 22, 0);
        card.add(FormHelper.muted("Sign in to continue to your transport portal"), g);
        g.gridy = y++;
        g.insets = new Insets(0, 0, 6, 0);
        card.add(FormHelper.label("Login as"), g);
        g.gridy = y++;
        g.insets = new Insets(0, 0, 16, 0);
        card.add(roles, g);
        g.gridy = y++;
        g.insets = new Insets(0, 0, 6, 0);
        card.add(FormHelper.label("Username / Email"), g);
        g.gridy = y++;
        g.insets = new Insets(0, 0, 14, 0);
        card.add(username, g);
        g.gridy = y++;
        g.insets = new Insets(0, 0, 6, 0);
        card.add(FormHelper.label("Password"), g);
        g.gridy = y++;
        g.insets = new Insets(0, 0, 4, 0);
        card.add(password, g);
        g.gridy = y++;
        g.fill = GridBagConstraints.NONE;
        g.anchor = GridBagConstraints.EAST;
        g.insets = new Insets(0, 0, 14, 0);
        card.add(forgot, g);
        g.gridy = y++;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.NORTH;
        g.insets = new Insets(0, 0, 14, 0);
        card.add(login, g);
        g.gridy = y++;
        g.insets = new Insets(0, 0, 0, 0);
        card.add(bottom, g);

        side.add(card);
        return side;
    }

    private String selectedRole() {
        if (studentBtn.isSelected()) {
            return "STUDENT";
        }
        return driverBtn.isSelected() ? "DRIVER" : "ADMIN";
    }

    private void doLogin() {
        String login = username.getText().trim();
        String pw = new String(password.getPassword());
        if (login.isEmpty() || pw.isEmpty()) {
            FormHelper.warn(this, "Please fill all required fields.");
            return;
        }
        try {
            User user = userDAO.authenticate(login, pw, selectedRole());
            if (user == null) {
                FormHelper.error(this, "Invalid login credentials.");
                return;
            }
            if (!"ACTIVE".equals(user.status())) {
                FormHelper.error(this, "Your account is deactivated. Please contact the transport office.");
                return;
            }
            Session.start(user);
            JFrame next = switch (user.role()) {
                case "STUDENT" -> new StudentDashboardFrame();
                case "DRIVER" -> new DriverDashboardFrame();
                default -> new MainDashboardFrame();
            };
            next.setVisible(true);
            dispose();
        } catch (Exception ex) {
            FormHelper.handleException(this, ex);
        }
    }

    /** Shared logout used by every dashboard: confirm, clear session, back to login. */
    public static void logout(Window current) {
        if (!FormHelper.confirm(current, "Are you sure you want to logout?", "Logout")) {
            return;
        }
        Session.clear();
        current.dispose();
        new LoginFrame().setVisible(true);
    }

    // ------------------------------------------------------------------
    /** Left side: gradient, a drawn bus and the project title. */
    private static class BrandingPanel extends JPanel {

        BrandingPanel() {
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createEmptyBorder(34, 44, 40, 44));
            setOpaque(false);

            JLabel logo = new JLabel("  PIET Transport");
            logo.setIcon(IconFactory.create(IconFactory.Type.BUS, 28, UITheme.CYAN));
            logo.setFont(UITheme.font(Font.BOLD, 17f));
            logo.setForeground(Color.WHITE);

            JPanel text = FormHelper.panel(new GridLayout(0, 1, 0, 4));
            JLabel t1 = new JLabel("College Bus Tracking");
            JLabel t2 = new JLabel("& Route Management System");
            t1.setFont(UITheme.font(Font.BOLD, 32f));
            t2.setFont(UITheme.font(Font.BOLD, 26f));
            t1.setForeground(Color.WHITE);
            t2.setForeground(UITheme.CYAN);
            JLabel college = new JLabel("Poornima Institute of Engineering and Technology, Jaipur");
            college.setFont(UITheme.font(Font.PLAIN, 14f));
            college.setForeground(UITheme.SIDEBAR_TEXT);
            JLabel chips = new JLabel("Buses  •  Routes  •  Drivers  •  Students  •  Notices");
            chips.setFont(UITheme.font(Font.PLAIN, 12.5f));
            chips.setForeground(new Color(0x7F93B2));
            text.add(t1);
            text.add(t2);
            text.add(college);
            text.add(chips);

            add(logo, BorderLayout.NORTH);
            add(text, BorderLayout.SOUTH);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            g2.setPaint(new GradientPaint(0, 0, UITheme.NAVY, w, h, UITheme.NAVY_LIGHT));
            g2.fillRect(0, 0, w, h);
            g2.setColor(new Color(6, 182, 212, 28));
            g2.fillOval(-120, h - 330, 420, 420);
            g2.setColor(new Color(37, 99, 235, 45));
            g2.fillOval(w - 260, -140, 420, 420);
            g2.setColor(new Color(255, 255, 255, 12));
            g2.fillOval(w / 2 - 40, h / 2 - 230, 300, 300);
            drawBus(g2, w / 2, h / 2 - 20, Math.min(1.0, (w - 100) / 440.0));
            g2.dispose();
            super.paintComponent(g);
        }

        private void drawBus(Graphics2D g, int cx, int cy, double scale) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.translate(cx, cy);
            g2.scale(scale, scale);
            g2.setColor(new Color(255, 255, 255, 40));
            g2.fillRoundRect(-230, 96, 460, 6, 6, 6);
            g2.setColor(new Color(0, 0, 0, 50));
            g2.fillOval(-170, 92, 340, 18);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(-190, -70, 380, 150, 44, 44);
            g2.setColor(UITheme.PRIMARY);
            g2.fillRect(-190, 38, 380, 16);
            g2.setColor(UITheme.CYAN);
            g2.fillRect(-190, 54, 380, 6);
            g2.setColor(UITheme.NAVY_MID);
            for (int i = 0; i < 5; i++) {
                g2.fillRoundRect(-168 + i * 66, -50, 54, 56, 12, 12);
            }
            g2.setColor(new Color(255, 255, 255, 60));
            for (int i = 0; i < 5; i++) {
                g2.fillRoundRect(-162 + i * 66, -46, 12, 48, 8, 8);
            }
            g2.setColor(new Color(0xFBBF24));
            g2.fillRoundRect(176, 14, 14, 16, 6, 6);
            for (int wx : new int[]{-110, 110}) {
                g2.setColor(new Color(0x0F172A));
                g2.fillOval(wx - 28, 52, 56, 56);
                g2.setColor(new Color(0x94A3B8));
                g2.fillOval(wx - 14, 66, 28, 28);
                g2.setColor(new Color(0x0F172A));
                g2.fillOval(wx - 5, 75, 10, 10);
            }
            g2.dispose();
        }
    }
}
