package com.piet.busmanagement.ui.panels;

import com.piet.busmanagement.dao.NoticeDAO;
import com.piet.busmanagement.model.Notice;
import com.piet.busmanagement.ui.dialogs.NoticeFormDialog;

import java.util.ArrayList;
import java.util.List;

public class NoticeManagementPanel extends CrudPanel {

    private final NoticeDAO dao = new NoticeDAO();

    public NoticeManagementPanel() {
        super("Notices", "Publish transport announcements for students and drivers",
                new String[]{"Notice ID", "Title", "Description", "Date", "Status"},
                "Status", new String[]{"ACTIVE", "INACTIVE"});
    }

    @Override
    protected List<Object[]> fetchRows(String keyword, String filter) throws Exception {
        List<Object[]> rows = new ArrayList<>();
        for (Notice n : dao.search(keyword, filter)) {
            rows.add(new Object[]{n.id(), n.title(), n.description(), n.noticeDate(), n.status()});
        }
        return rows;
    }

    @Override
    protected void addRecord() {
        new NoticeFormDialog(owner(), null).setVisible(true);
        refresh();
    }

    @Override
    protected void editRecord(int id) throws Exception {
        Notice n = dao.findById(id);
        if (n != null) {
            new NoticeFormDialog(owner(), n).setVisible(true);
            refresh();
        }
    }

    @Override
    protected void deleteRecord(int id) throws Exception {
        dao.delete(id);
    }
}
