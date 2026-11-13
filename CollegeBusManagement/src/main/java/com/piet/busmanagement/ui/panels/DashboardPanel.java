package com.piet.busmanagement.ui.panels;

import com.piet.busmanagement.config.Session;
import com.piet.busmanagement.config.UITheme;
import com.piet.busmanagement.dao.DashboardDAO;
import com.piet.busmanagement.dao.NoticeDAO;
import com.piet.busmanagement.ui.components.FormHelper;
import com.piet.busmanagement.ui.components.IconFactory;
import com.piet.busmanagement.ui.components.ModernTable;
import com.piet.busmanagement.ui.components.NoticeListPanel;
import com.piet.busmanagement.ui.components.Refreshable;
import com.piet.busmanagement.ui.components.RoundedPanel;
import com.piet.busmanagement.ui.components.ScrollablePanel;
import com.piet.busmanagement.ui.components.StatCard;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Admin home screen with live statistics, recent activity, bus status and notices. */
public class DashboardPanel extends JPanel implements Refreshable {

    private final DashboardDAO dao = new DashboardDAO();
    private final NoticeDAO noticeDAO = new NoticeDAO();

    private final StatCard buses = new StatCard("Total Buses", IconFactory.Type.BUS, UITheme.PRIMARY);
    private final StatCard students = new StatCard("Total Students", IconFactory.Type.STUDENT, UITheme.CYAN);
    private final StatCard drivers = new StatCard("Total Drivers", IconFactory.Type.DRIVER, new Color(0x8B5CF6));
    private final StatCard routes = new StatCard("Total Routes", IconFactory.Type.ROUTE, UITheme.WARNING);
    private final StatCard assignments = new StatCard("Active Assignments", IconFactory.Type.ASSIGN, UITheme.SUCCESS);

    private final ModernTable recentStudents = new ModernTable("Name", "Reg. No", "Section", "Registered");
    private final ModernTable recentAssignments = new ModernTable("Bus", "Driver", "Route", "Date");
    private final StatusBars statusBars = new StatusBars();
    private final JPanel noticeHolder = FormHelper.panel(new BorderLayout());

    public DashboardPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);

        JPanel content = new ScrollablePanel(new BorderLayout(0, 18));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(24, 28, 24, 28));

        // greeting
        JPanel titles = FormHelper.panel(new BorderLayout(0, 2));
        titles.add(FormHelper.heading("Welcome back, " + Session.getUser().fullName(), 24f), BorderLayout.CENTER);
        titles.add(FormHelper.muted(LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy"))
                + "  -  here is what is happening with college transport today"), BorderLayout.SOUTH);

        JPanel stats = FormHelper.panel(new GridLayout(1, 5, 14, 0));
        stats.add(buses);
        stats.add(students);
        stats.add(drivers);
        stats.add(routes);
        stats.add(assignments);

        JPanel top = FormHelper.panel(new BorderLayout(0, 16));
        top.add(titles, BorderLayout.NORTH);
        top.add(stats, BorderLayout.CENTER);

        // recent activity
        JPanel tables = FormHelper.panel(new GridLayout(1, 2, 18, 0));
        tables.add(tableCard("Recent Student Registrations", IconFactory.Type.STUDENT, recentStudents));
        tables.add(tableCard("Recent Bus Assignments", IconFactory.Type.ASSIGN, recentAssignments));

        // bus status + notices
        JPanel bottom = FormHelper.panel(new GridLayout(1, 2, 18, 0));
        RoundedPanel statusCard = new RoundedPanel(new BorderLayout(0, 8), 18);
        statusCard.add(cardTitle("Bus Status Summary", IconFactory.Type.BUS), BorderLayout.NORTH);
        statusCard.add(statusBars, BorderLayout.CENTER);
        RoundedPanel noticeCard = new RoundedPanel(new BorderLayout(0, 8), 18);
        noticeCard.add(cardTitle("Important Notices", IconFactory.Type.NOTICE), BorderLayout.NORTH);
        noticeCard.add(noticeHolder, BorderLayout.CENTER);
        bottom.add(statusCard);
        bottom.add(noticeCard);

        JPanel middle = FormHelper.panel(new BorderLayout(0, 18));
        middle.add(tables, BorderLayout.NORTH);
        middle.add(bottom, BorderLayout.CENTER);

        content.add(top, BorderLayout.NORTH);
        content.add(middle, BorderLayout.CENTER);

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        add(scroll, BorderLayout.CENTER);
    }

    private JLabel cardTitle(String text, IconFactory.Type icon) {
        JLabel l = new JLabel("  " + text);
        l.setIcon(IconFactory.create(icon, 18, UITheme.PRIMARY));
        l.setFont(UITheme.font(Font.BOLD, 15f));
        l.setForeground(UITheme.NAVY);
        l.setBorder(new EmptyBorder(2, 4, 6, 0));
        return l;
    }

    private RoundedPanel tableCard(String title, IconFactory.Type icon, ModernTable table) {
        RoundedPanel card = new RoundedPanel(new BorderLayout(0, 4), 12);
        card.add(cardTitle(title, icon), BorderLayout.NORTH);
        table.setRowSorter(null);
        JScrollPane pane = ModernTable.scroll(table);
        pane.setPreferredSize(new Dimension(300, 230));
        card.add(pane, BorderLayout.CENTER);
        return card;
    }

    @Override
    public void refresh() {
        try {
            buses.setValue(String.valueOf(dao.totalBuses()));
            students.setValue(String.valueOf(dao.totalStudents()));
            drivers.setValue(String.valueOf(dao.totalDrivers()));
            routes.setValue(String.valueOf(dao.totalRoutes()));
            assignments.setValue(String.valueOf(dao.activeAssignments()));

            List<Object[]> rows = new ArrayList<>();
            for (String[] r : dao.recentStudents(6)) {
                rows.add(r);
            }
            recentStudents.setRows(rows);
            rows = new ArrayList<>();
            for (String[] r : dao.recentAssignments(6)) {
                rows.add(r);
            }
            recentAssignments.setRows(rows);

            statusBars.setData(dao.busStatusCounts());
            noticeHolder.removeAll();
            noticeHolder.add(NoticeListPanel.build(noticeDAO.listActive(3), 300), BorderLayout.NORTH);
            noticeHolder.revalidate();
            noticeHolder.repaint();
        } catch (Exception ex) {
            FormHelper.handleException(this, ex);
        }
    }

    /** Horizontal bars showing how many buses are ACTIVE / MAINTENANCE / INACTIVE. */
    private static class StatusBars extends JComponent {
        private Map<String, Integer> data = Map.of();

        StatusBars() {
            setPreferredSize(new Dimension(300, 190));
        }

        void setData(Map<String, Integer> data) {
            this.data = data;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int max = 1;
            for (int v : data.values()) {
                max = Math.max(max, v);
            }
            int y = 14;
            int labelW = 120;
            int barX = labelW + 10;
            int barW = Math.max(60, getWidth() - barX - 50);
            for (Map.Entry<String, Integer> e : data.entrySet()) {
                Color color = switch (e.getKey()) {
                    case "ACTIVE" -> UITheme.SUCCESS;
                    case "MAINTENANCE" -> UITheme.WARNING;
                    default -> new Color(0x94A3B8);
                };
                g2.setFont(UITheme.font(Font.BOLD, 12.5f));
                g2.setColor(UITheme.TEXT);
                g2.drawString(e.getKey(), 6, y + 15);
                g2.setColor(new Color(0xE2E8F0));
                g2.fillRoundRect(barX, y, barW, 20, 20, 20);
                g2.setColor(color);
                int w = e.getValue() == 0 ? 0 : Math.max(20, (int) ((double) e.getValue() / max * barW));
                g2.fillRoundRect(barX, y, w, 20, 20, 20);
                g2.setColor(UITheme.NAVY);
                g2.drawString(String.valueOf(e.getValue()), barX + barW + 12, y + 15);
                y += 48;
            }
            g2.dispose();
        }
    }
}
