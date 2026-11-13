package com.piet.busmanagement.ui.components;

import com.piet.busmanagement.config.UITheme;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.List;

/** Clean read-only table. A column titled "Status" is drawn as a coloured pill. */
public class ModernTable extends JTable {

    private final DefaultTableModel model;

    public ModernTable(String... columns) {
        super(new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int column) {
                return getRowCount() > 0 && getValueAt(0, column) != null ? getValueAt(0, column).getClass() : Object.class;
            }
        });
        model = (DefaultTableModel) getModel();
        setRowHeight(40);
        setShowVerticalLines(false);
        setShowHorizontalLines(true);
        setGridColor(new Color(0xEEF2F7));
        setIntercellSpacing(new Dimension(0, 1));
        setSelectionBackground(new Color(0xDBEAFE));
        setSelectionForeground(UITheme.TEXT);
        setFillsViewportHeight(true);
        setFont(UITheme.font(Font.PLAIN, 13f));
        setAutoCreateRowSorter(true);
        setDefaultRenderer(Object.class, new CellRenderer());
        JTableHeader header = getTableHeader();
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(100, 42));
        header.setDefaultRenderer(new HeaderRenderer());
    }

    public void setRows(List<Object[]> rows) {
        model.setRowCount(0);
        for (Object[] row : rows) {
            model.addRow(row);
        }
    }

    /** Value of the first column (the record id) in the selected row, or -1. */
    public int getSelectedId() {
        int row = getSelectedRow();
        if (row < 0) {
            return -1;
        }
        return ((Number) model.getValueAt(convertRowIndexToModel(row), 0)).intValue();
    }

    public static JScrollPane scroll(JTable table) {
        JScrollPane pane = new JScrollPane(table);
        pane.setBorder(BorderFactory.createEmptyBorder());
        pane.getViewport().setBackground(Color.WHITE);
        return pane;
    }

    // ------------------------------------------------------------------
    private static class HeaderRenderer extends DefaultTableCellRenderer {
        HeaderRenderer() {
            setOpaque(true);
            setHorizontalAlignment(SwingConstants.LEFT);
        }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object value, boolean sel, boolean focus, int row, int col) {
            super.getTableCellRendererComponent(t, value, sel, focus, row, col);
            setText(value == null ? "" : value.toString().toUpperCase());
            setFont(UITheme.font(Font.BOLD, 11.5f));
            setForeground(UITheme.MUTED);
            setBackground(new Color(0xF8FAFC));
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER), new EmptyBorder(0, 12, 0, 12)));
            return this;
        }
    }

    private static class CellRenderer extends DefaultTableCellRenderer {
        private final StatusPill pill = new StatusPill();

        @Override
        public Component getTableCellRendererComponent(JTable t, Object value, boolean sel, boolean focus, int row, int col) {
            Color rowBg = sel ? t.getSelectionBackground() : (row % 2 == 0 ? Color.WHITE : new Color(0xFAFBFD));
            if ("Status".equals(t.getColumnName(col)) && value != null) {
                pill.set(value.toString(), rowBg);
                return pill;
            }
            super.getTableCellRendererComponent(t, value, sel, false, row, col);
            setBackground(rowBg);
            setForeground(UITheme.TEXT);
            setBorder(new EmptyBorder(0, 12, 0, 12));
            return this;
        }
    }

    private static class StatusPill extends JLabel {
        private String status = "";
        private Color rowBg = Color.WHITE;

        void set(String status, Color rowBg) {
            this.status = status;
            this.rowBg = rowBg;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(rowBg);
            g2.fillRect(0, 0, getWidth(), getHeight());
            Color bg;
            Color fg;
            switch (status.toUpperCase()) {
                case "ACTIVE" -> { bg = new Color(0xDCFCE7); fg = new Color(0x166534); }
                case "INACTIVE" -> { bg = new Color(0xE2E8F0); fg = new Color(0x475569); }
                case "MAINTENANCE" -> { bg = new Color(0xFEF3C7); fg = new Color(0x92400E); }
                default -> { bg = new Color(0xDBEAFE); fg = new Color(0x1E40AF); }
            }
            g2.setFont(UITheme.font(Font.BOLD, 11.5f));
            FontMetrics fm = g2.getFontMetrics();
            int pw = fm.stringWidth(status) + 22;
            int ph = 24;
            int y = (getHeight() - ph) / 2;
            g2.setColor(bg);
            g2.fillRoundRect(12, y, pw, ph, ph, ph);
            g2.setColor(fg);
            g2.drawString(status, 23, y + (ph + fm.getAscent() - fm.getDescent()) / 2);
            g2.dispose();
        }
    }
}
