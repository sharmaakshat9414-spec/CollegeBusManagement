package com.piet.busmanagement.ui.panels;

import com.piet.busmanagement.dao.StudentBusAllocationDAO;
import com.piet.busmanagement.model.StudentBusAllocation;
import com.piet.busmanagement.ui.dialogs.StudentAllocationDialog;

import java.util.ArrayList;
import java.util.List;

public class StudentBusAllocationPanel extends CrudPanel {

    private final StudentBusAllocationDAO dao = new StudentBusAllocationDAO();

    public StudentBusAllocationPanel() {
        super("Student Bus Allocation", "Allocate students to a bus, route and pickup stop",
                new String[]{"ID", "Student", "Reg. No", "Bus Number", "Route", "Pickup Stop", "Status"},
                "Status", new String[]{"ACTIVE", "INACTIVE"});
    }

    @Override
    protected List<Object[]> fetchRows(String keyword, String filter) throws Exception {
        List<Object[]> rows = new ArrayList<>();
        for (StudentBusAllocation a : dao.search(keyword, filter)) {
            rows.add(new Object[]{a.id(), a.studentName(), a.regNo(), a.busNumber(), a.routeName(), a.pickupStop(), a.status()});
        }
        return rows;
    }

    @Override
    protected void addRecord() {
        new StudentAllocationDialog(owner(), null).setVisible(true);
        refresh();
    }

    @Override
    protected void editRecord(int id) throws Exception {
        StudentBusAllocation a = dao.findById(id);
        if (a != null) {
            new StudentAllocationDialog(owner(), a).setVisible(true);
            refresh();
        }
    }

    @Override
    protected void deleteRecord(int id) throws Exception {
        dao.delete(id);
    }
}
