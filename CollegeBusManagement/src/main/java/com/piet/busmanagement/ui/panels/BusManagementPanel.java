package com.piet.busmanagement.ui.panels;

import com.piet.busmanagement.dao.BusDAO;
import com.piet.busmanagement.model.Bus;
import com.piet.busmanagement.ui.dialogs.BusFormDialog;

import java.util.ArrayList;
import java.util.List;

public class BusManagementPanel extends CrudPanel {

    private final BusDAO dao = new BusDAO();

    public BusManagementPanel() {
        super("Bus Management", "Add, update and monitor all college buses",
                new String[]{"Bus ID", "Bus Number", "Registration No", "Type", "Capacity", "Driver", "Route", "Status"},
                "Status", new String[]{"ACTIVE", "INACTIVE", "MAINTENANCE"});
    }

    @Override
    protected List<Object[]> fetchRows(String keyword, String filter) throws Exception {
        List<Object[]> rows = new ArrayList<>();
        for (Bus b : dao.search(keyword, filter)) {
            rows.add(new Object[]{b.id(), b.busNumber(), b.registrationNo(), b.busType(), b.capacity(),
                    b.driverName(), b.routeName(), b.status()});
        }
        return rows;
    }

    @Override
    protected void addRecord() {
        new BusFormDialog(owner(), null).setVisible(true);
        refresh();
    }

    @Override
    protected void editRecord(int id) throws Exception {
        Bus bus = dao.findById(id);
        if (bus != null) {
            new BusFormDialog(owner(), bus).setVisible(true);
            refresh();
        }
    }

    @Override
    protected void deleteRecord(int id) throws Exception {
        dao.delete(id);
    }
}
