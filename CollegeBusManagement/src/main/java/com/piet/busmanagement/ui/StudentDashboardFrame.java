package com.piet.busmanagement.ui;

import com.piet.busmanagement.config.Session;
import com.piet.busmanagement.config.UITheme;
import com.piet.busmanagement.dao.DashboardDAO;
import com.piet.busmanagement.dao.NoticeDAO;
import com.piet.busmanagement.dao.StudentDAO;
import com.piet.busmanagement.model.Notice;
import com.piet.busmanagement.model.Student;
import com.piet.busmanagement.ui.components.BannerPanel;
import com.piet.busmanagement.ui.components.FormHelper;
import com.piet.busmanagement.ui.components.IconFactory;
import com.piet.busmanagement.ui.components.InfoCard;
import com.piet.busmanagement.ui.components.NoticeListPanel;
import com.piet.busmanagement.ui.components.RoundedPanel;
import com.piet.busmanagement.ui.components.ScrollablePanel;
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

/** Read-only dashboard for a logged-in student. */
public class StudentDashboardFrame extends JFrame {

    public StudentDashboardFrame() {
        super("College Bus Tracking & Route Management System - Student");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setIconImage(IconFactory.appImage());
        setMinimumSize(new Dimension(980, 680));
        setSize(1180, 780);

        Student student = null;
        Map<String, String> transport = null;
        List<Notice> notices = new ArrayList<>();
        try {
            student = new StudentDAO().findByUserId(Session.getUser().id());
            if (student != null) {
                transport = new DashboardDAO().studentTransport(student.id());
            }
            notices = new NoticeDAO().listActive(8);
        } catch (Exception ex) {
            FormHelper.handleException(this, ex);
        }

        String name = student != null ? student.fullName() : Session.getUser().fullName();
        BannerPanel banner = new BannerPanel("Welcome, " + name,
                student != null ? student.regNo() + "  |  Section " + student.section() : "Student");

        InfoCard profile = new InfoCard("My Profile", IconFactory.Type.USER);
        if (student != null) {
            profile.addRow("Name", student.fullName());
            profile.addRow("Registration No.", student.regNo());
            profile.addRow("Section", student.section());
            profile.addRow("Phone", student.phone());
            profile.addRow("Email", student.email());
        } else {
            profile.addRow("Profile", "No student profile found for this account.");
        }

        InfoCard transportCard = new InfoCard("Transport Information", IconFactory.Type.BUS);
        if (transport != null) {
            transportCard.addRow("Assigned Bus", transport.get("bus_number") + " (" + transport.get("bus_type") + ")");
            transportCard.addRow("Route", transport.get("route_name"));
            transportCard.addRow("Driver", transport.get("driver_name"));
            transportCard.addRow("Driver Phone", transport.get("driver_phone"));
            transportCard.addRow("Pickup Stop", transport.get("pickup_stop"));
            transportCard.addRow("Schedule", "Departs " + transport.get("departure_time")
                    + "  |  Est. " + transport.get("est_time_min") + " min");
            transportCard.addRow("Route Stops", transport.get("stops"));
        } else {
            transportCard.addRow("Status", "No bus allocated yet. Please contact the transport office.");
        }

        JPanel cardsRow = FormHelper.panel(new GridLayout(1, 2, 18, 0));
        cardsRow.add(profile);
        cardsRow.add(transportCard);

        RoundedPanel noticeCard = new RoundedPanel(new BorderLayout(0, 8), 20);
        JLabel noticeTitle = new JLabel("  Notices & Transport Updates");
        noticeTitle.setIcon(IconFactory.create(IconFactory.Type.NOTICE, 20, UITheme.PRIMARY));
        noticeTitle.setFont(UITheme.font(Font.BOLD, 16f));
        noticeTitle.setForeground(UITheme.NAVY);
        noticeCard.add(noticeTitle, BorderLayout.NORTH);
        noticeCard.add(NoticeListPanel.build(notices, 700), BorderLayout.CENTER);

        JPanel body = FormHelper.panel(new BorderLayout(0, 18));
        body.add(cardsRow, BorderLayout.NORTH);
        body.add(noticeCard, BorderLayout.CENTER);

        JPanel content = new ScrollablePanel(new BorderLayout(0, 18));
        content.setBackground(UITheme.BG);
        content.setBorder(new EmptyBorder(24, 28, 24, 28));
        content.add(banner, BorderLayout.NORTH);
        content.add(body, BorderLayout.CENTER);

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(18);

        JPanel root = new JPanel(new BorderLayout());
        root.add(new TopBar("Student", name,
                () -> new ChangePasswordDialog(this).setVisible(true), () -> LoginFrame.logout(this)), BorderLayout.NORTH);
        root.add(scroll, BorderLayout.CENTER);
        setContentPane(root);
        setLocationRelativeTo(null);
    }
}
