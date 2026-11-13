package com.piet.busmanagement.ui;

import com.piet.busmanagement.config.Session;
import com.piet.busmanagement.config.UITheme;
import com.piet.busmanagement.dao.DashboardDAO;
import com.piet.busmanagement.dao.DriverDAO;
import com.piet.busmanagement.dao.NoticeDAO;
import com.piet.busmanagement.model.Driver;
import com.piet.busmanagement.model.Notice;
import com.piet.busmanagement.ui.components.BannerPanel;
import com.piet.busmanagement.ui.components.FormHelper;
import com.piet.busmanagement.ui.components.IconFactory;
import com.piet.busmanagement.ui.components.InfoCard;
import com.piet.busmanagement.ui.components.NoticeListPanel;
import com.piet.busmanagement.ui.components.RoundedPanel;
import com.piet.busmanagement.ui.components.ScrollablePanel;
import com.piet.busmanagement.ui.components.StatCard;
import com.piet.busmanagement.ui.components.TopBar;
import com.piet.busmanagement.ui.dialogs.ChangePasswordDialog;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Read-only dashboard for a logged-in driver. */
public class DriverDashboardFrame extends JFrame {

    public DriverDashboardFrame() {
        super("College Bus Tracking & Route Management System - Driver");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setIconImage(IconFactory.appImage());
        setMinimumSize(new Dimension(980, 680));
        setSize(1180, 780);

        Driver driver = null;
        Map<String, String> info = null;
        List<Notice> notices = new ArrayList<>();
        try {
            driver = new DriverDAO().findByUserId(Session.getUser().id());
            if (driver != null) {
                info = new DashboardDAO().driverTransport(driver.id());
            }
            notices = new NoticeDAO().listActive(8);
        } catch (Exception ex) {
            FormHelper.handleException(this, ex);
        }

        String name = driver != null ? driver.name() : Session.getUser().fullName();
        String driverCode = driver != null ? String.format("DRV-%03d", driver.id()) : "-";
        BannerPanel banner = new BannerPanel("Welcome, " + name, "Driver ID: " + driverCode
                + (driver != null ? "  |  License " + driver.licenseNo() : ""));

        StatCard busCard = new StatCard("Assigned Bus", IconFactory.Type.BUS, UITheme.PRIMARY);
        StatCard studentCard = new StatCard("Assigned Students", IconFactory.Type.STUDENT, UITheme.CYAN);
        StatCard timeCard = new StatCard("Departure Time", IconFactory.Type.ROUTE, UITheme.WARNING);
        busCard.setValue(info != null ? info.get("bus_number") : "None");
        studentCard.setValue(info != null ? info.get("students") : "0");
        timeCard.setValue(info != null ? info.get("departure_time") : "-");
        JPanel stats = FormHelper.panel(new GridLayout(1, 3, 14, 0));
        stats.add(busCard);
        stats.add(studentCard);
        stats.add(timeCard);

        InfoCard details = new InfoCard("Assignment Details", IconFactory.Type.ASSIGN);
        details.addRow("Driver Name", name);
        details.addRow("Driver ID", driverCode);
        if (driver != null) {
            details.addRow("Phone", driver.phone());
        }
        if (info != null) {
            details.addRow("Bus Number", info.get("bus_number") + " (" + info.get("bus_type") + ")");
            details.addRow("Route", info.get("route_name"));
            details.addRow("Route Stops", info.get("stops"));
            details.addRow("Schedule", "Departs " + info.get("departure_time") + "  |  Est. " + info.get("est_time_min") + " min");
            details.addRow("Assigned Students", info.get("students"));
        } else {
            details.addRow("Assignment", "No active bus assignment yet. Please contact the transport office.");
        }

        RoundedPanel noticeCard = new RoundedPanel(new BorderLayout(0, 8), 20);
        JLabel noticeTitle = new JLabel("  Important Notices");
        noticeTitle.setIcon(IconFactory.create(IconFactory.Type.NOTICE, 20, UITheme.PRIMARY));
        noticeTitle.setFont(UITheme.font(Font.BOLD, 16f));
        noticeTitle.setForeground(UITheme.NAVY);
        noticeCard.add(noticeTitle, BorderLayout.NORTH);
        JPanel noticeHolder = FormHelper.panel(new BorderLayout());
        noticeHolder.add(NoticeListPanel.build(notices, 340), BorderLayout.NORTH);
        noticeCard.add(noticeHolder, BorderLayout.CENTER);

        JPanel lower = FormHelper.panel(new GridLayout(1, 2, 18, 0));
        lower.add(details);
        lower.add(noticeCard);

        JPanel middle = FormHelper.panel(new BorderLayout(0, 18));
        middle.add(stats, BorderLayout.NORTH);
        middle.add(lower, BorderLayout.CENTER);

        JPanel content = new ScrollablePanel(new BorderLayout(0, 18));
        content.setBackground(UITheme.BG);
        content.setBorder(new EmptyBorder(24, 28, 24, 28));
        content.add(banner, BorderLayout.NORTH);
        content.add(middle, BorderLayout.CENTER);

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(18);

        JPanel root = new JPanel(new BorderLayout());
        root.add(new TopBar("Driver", name,
                () -> new ChangePasswordDialog(this).setVisible(true), () -> LoginFrame.logout(this)), BorderLayout.NORTH);
        root.add(scroll, BorderLayout.CENTER);
        setContentPane(root);
        setLocationRelativeTo(null);
    }
}
