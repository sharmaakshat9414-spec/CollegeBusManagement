package com.piet.busmanagement.ui.panels;

import com.piet.busmanagement.dao.BusAssignmentDAO;
import com.piet.busmanagement.model.BusAssignment;
import com.piet.busmanagement.ui.dialogs.BusAssignmentDialog;

import java.util.ArrayList;
import java.util.List;

public class BusAssignmentPanel extends CrudPanel {

    private final BusAssignmentDAO dao = new BusAssignmentDAO();

    public BusAssignmentPanel() {
        super("Bus Assignments", "Assign a driver and a route to each bus",
                new String[]{"ID", "Bus Number", "Driver", "Route", "Assignment Date", "Status"},
                "Status", new String[]{"ACTIVE", "INACTIVE"});
    }

    @Override
    protected List<Object[]> fetchRows(String keyword, String filter) throws Exception {
        List<Object[]> rows = new ArrayList<>();
        for (BusAssignment a : dao.search(keyword, filter)) {
            rows.add(new Object[]{a.id(), a.busNumber(), a.driverName(), a.routeName(), a.assignmentDate(), a.status()});
        }
        return rows;
    }

    @Override
    protected void addRecord() {
        new BusAssignmentDialog(owner(), null).setVisible(true);
        refresh();
    }

    @Override
    protected void editRecord(int id) throws Exception {
        BusAssignment a = dao.findById(id);
        if (a != null) {
            new BusAssignmentDialog(owner(), a).setVisible(true);
            refresh();
        }
    }

    @Override
    protected void deleteRecord(int id) throws Exception {
        dao.delete(id);
    }
}
