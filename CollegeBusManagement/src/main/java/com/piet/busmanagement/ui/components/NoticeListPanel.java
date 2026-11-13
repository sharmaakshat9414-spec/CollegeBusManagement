package com.piet.busmanagement.ui.components;

import com.piet.busmanagement.config.UITheme;
import com.piet.busmanagement.model.Notice;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Font;
import java.util.List;

/** Vertical list of notice entries (title, date and description). */
public final class NoticeListPanel {

    private NoticeListPanel() {
    }

    public static JPanel build(List<Notice> notices, int textWidth) {
        JPanel list = FormHelper.panel(null);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        if (notices.isEmpty()) {
            list.add(FormHelper.muted("No notices at the moment."));
            return list;
        }
        for (Notice n : notices) {
            JPanel row = FormHelper.panel(new BorderLayout(0, 3));
            row.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER),
                    BorderFactory.createEmptyBorder(9, 0, 9, 0)));
            JLabel title = new JLabel(n.title());
            title.setFont(UITheme.font(Font.BOLD, 14f));
            title.setForeground(UITheme.NAVY);
            title.setIcon(IconFactory.create(IconFactory.Type.NOTICE, 16, UITheme.PRIMARY));
            title.setIconTextGap(8);
            JLabel date = FormHelper.muted(n.noticeDate());
            date.setFont(UITheme.font(Font.PLAIN, 12f));
            JLabel desc = new JLabel("<html><div style='width:" + textWidth + "px'>" + escape(n.description()) + "</div></html>");
            desc.setFont(UITheme.font(Font.PLAIN, 13f));
            desc.setForeground(UITheme.MUTED);
            JPanel head = FormHelper.panel(new BorderLayout());
            head.add(title, BorderLayout.CENTER);
            head.add(date, BorderLayout.EAST);
            row.add(head, BorderLayout.NORTH);
            row.add(desc, BorderLayout.CENTER);
            row.setAlignmentX(0f);
            list.add(row);
        }
        return list;
    }

    private static String escape(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
