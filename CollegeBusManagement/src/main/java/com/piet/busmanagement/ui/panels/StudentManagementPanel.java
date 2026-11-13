package com.piet.busmanagement.ui.panels;

import com.piet.busmanagement.dao.StudentDAO;
import com.piet.busmanagement.model.Student;
import com.piet.busmanagement.ui.dialogs.StudentFormDialog;

import java.util.ArrayList;
import java.util.List;

public class StudentManagementPanel extends CrudPanel {

    private final StudentDAO dao = new StudentDAO();

    public StudentManagementPanel() {
        super("Student Management", "Manage student records and their login accounts",
                new String[]{"Student ID", "Full Name", "Reg. No", "Email", "Phone", "Section", "Route", "Bus", "Status"},
                "Status", new String[]{"ACTIVE", "INACTIVE"});
    }

    @Override
    protected List<Object[]> fetchRows(String keyword, String filter) throws Exception {
        List<Object[]> rows = new ArrayList<>();
        for (Student s : dao.search(keyword, filter)) {
            rows.add(new Object[]{s.id(), s.fullName(), s.regNo(), s.email(), s.phone(), s.section(),
                    s.routeName(), s.busNumber(), s.status()});
        }
        return rows;
    }

    @Override
    protected void addRecord() {
        new StudentFormDialog(owner(), null).setVisible(true);
        refresh();
    }

    @Override
    protected void editRecord(int id) throws Exception {
        Student s = dao.findById(id);
        if (s != null) {
            new StudentFormDialog(owner(), s).setVisible(true);
            refresh();
        }
    }

    @Override
    protected void deleteRecord(int id) throws Exception {
        dao.delete(id);
    }
}
