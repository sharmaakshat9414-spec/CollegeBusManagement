package com.piet.busmanagement.ui;

import com.piet.busmanagement.config.Session;
import com.piet.busmanagement.config.UITheme;
import com.piet.busmanagement.ui.components.FormHelper;
import com.piet.busmanagement.ui.components.IconFactory;
import com.piet.busmanagement.ui.components.Refreshable;
import com.piet.busmanagement.ui.components.SidebarButton;
import com.piet.busmanagement.ui.panels.BusAssignmentPanel;
import com.piet.busmanagement.ui.panels.BusManagementPanel;
import com.piet.busmanagement.ui.panels.DashboardPanel;
import com.piet.busmanagement.ui.panels.DriverManagementPanel;
import com.piet.busmanagement.ui.panels.NoticeManagementPanel;
import com.piet.busmanagement.ui.panels.RouteManagementPanel;
import com.piet.busmanagement.ui.panels.SettingsPanel;
import com.piet.busmanagement.ui.panels.StudentBusAllocationPanel;
import com.piet.busmanagement.ui.panels.StudentManagementPanel;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Map;

/** Admin window: navy sidebar on the left, the selected screen on the right. */
public class MainDashboardFrame extends JFrame {

    private final CardLayout cards = new CardLayout();
    private final JPanel content = new JPanel(cards);
    private final Map<String, SidebarButton> navButtons = new HashMap<>();
    private final Map<String, JComponent> pages = new HashMap<>();
    private final JPanel sidebar = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setPaint(new GradientPaint(0, 0, UITheme.NAVY, 0, getHeight(), UITheme.NAVY_MID));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
        }
    };

    public MainDashboardFrame() {
        super("College Bus Tracking & Route Management System - Admin");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setIconImage(IconFactory.appImage());
        setMinimumSize(new Dimension(1180, 680));
        setSize(1320, 780);

        buildSidebar();
        content.setBackground(UITheme.BG);
        addPage("Dashboard", IconFactory.Type.DASHBOARD, new DashboardPanel());
        addPage("Buses", IconFactory.Type.BUS, new BusManagementPanel());
        addPage("Students", IconFactory.Type.STUDENT, new StudentManagementPanel());
        addPage("Drivers", IconFactory.Type.DRIVER, new DriverManagementPanel());
        addPage("Routes", IconFactory.Type.ROUTE, new RouteManagementPanel());
        addPage("Bus Assignments", IconFactory.Type.ASSIGN, new BusAssignmentPanel());
        addPage("Student Allocation", IconFactory.Type.ALLOCATE, new StudentBusAllocationPanel());
        addPage("Notices", IconFactory.Type.NOTICE, new NoticeManagementPanel());
        addPage("Settings", IconFactory.Type.SETTINGS, new SettingsPanel());
        addLogoutAndUser();

        JPanel root = new JPanel(new BorderLayout());
        root.add(sidebar, BorderLayout.WEST);
        root.add(content, BorderLayout.CENTER);
        setContentPane(root);
        setLocationRelativeTo(null);
        navigate("Dashboard");
    }

    private void buildSidebar() {
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(250, 100));
        sidebar.setBorder(new EmptyBorder(22, 16, 18, 16));

        JLabel brand = new JLabel("  PIET Transport");
        brand.setIcon(IconFactory.create(IconFactory.Type.BUS, 30, UITheme.CYAN));
        brand.setFont(UITheme.font(Font.BOLD, 19f));
        brand.setForeground(Color.WHITE);
        brand.setAlignmentX(LEFT_ALIGNMENT);
        JLabel sub = new JLabel("   Bus Tracking & Route Management");
        sub.setFont(UITheme.font(Font.PLAIN, 11f));
        sub.setForeground(UITheme.SIDEBAR_TEXT);
        sub.setAlignmentX(LEFT_ALIGNMENT);
        sidebar.add(brand);
        sidebar.add(Box.createVerticalStrut(3));
        sidebar.add(sub);
        sidebar.add(Box.createVerticalStrut(26));
    }

    private void addPage(String name, IconFactory.Type icon, JComponent page) {
        page.setOpaque(false);
        content.add(page, name);
        pages.put(name, page);
        SidebarButton button = new SidebarButton(name, icon);
        button.addActionListener(e -> navigate(name));
        navButtons.put(name, button);
        sidebar.add(button);
        sidebar.add(Box.createVerticalStrut(4));
    }

    private void addLogoutAndUser() {
        sidebar.add(Box.createVerticalGlue());
        JLabel user = new JLabel("  " + Session.getUser().fullName());
        user.setIcon(IconFactory.create(IconFactory.Type.USER, 18, UITheme.SIDEBAR_TEXT));
        user.setForeground(UITheme.SIDEBAR_TEXT);
        user.setFont(UITheme.font(Font.PLAIN, 12.5f));
        user.setBorder(new EmptyBorder(0, 8, 8, 0));
        user.setAlignmentX(LEFT_ALIGNMENT);
        sidebar.add(user);

        SidebarButton logout = new SidebarButton("Logout", IconFactory.Type.LOGOUT);
        logout.addActionListener(e -> LoginFrame.logout(this));
        sidebar.add(logout);
    }

    private void navigate(String name) {
        for (Map.Entry<String, SidebarButton> e : navButtons.entrySet()) {
            e.getValue().setActive(e.getKey().equals(name));
        }
        cards.show(content, name);
        JComponent page = pages.get(name);
        if (page instanceof Refreshable r) {
            r.refresh();
        }
    }
}
