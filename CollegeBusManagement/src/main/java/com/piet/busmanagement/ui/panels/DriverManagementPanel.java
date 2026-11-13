package com.piet.busmanagement.ui.panels;

import com.piet.busmanagement.dao.DriverDAO;
import com.piet.busmanagement.model.Driver;
import com.piet.busmanagement.ui.components.FormHelper;
import com.piet.busmanagement.ui.components.IconFactory;
import com.piet.busmanagement.ui.components.ModernButton;
import com.piet.busmanagement.ui.dialogs.DriverFormDialog;

import java.util.ArrayList;
import java.util.List;

public class DriverManagementPanel extends CrudPanel {

    private final DriverDAO dao = new DriverDAO();

    public DriverManagementPanel() {
        super("Driver Management", "Manage drivers and their login accounts",
                new String[]{"Driver ID", "Name", "Phone", "License No", "Address", "Email", "Assigned Bus", "Status"},
                "Status", new String[]{"ACTIVE", "INACTIVE"});
        ModernButton activate = new ModernButton("Activate", ModernButton.Style.SECONDARY, IconFactory.Type.CHECK);
        ModernButton deactivate = new ModernButton("Deactivate", ModernButton.Style.SECONDARY, IconFactory.Type.POWER);
        activate.addActionListener(e -> changeStatus("ACTIVE"));
        deactivate.addActionListener(e -> changeStatus("INACTIVE"));
        addActionButton(activate);
        addActionButton(deactivate);
    }

    private void changeStatus(String status) {
        int id = requireSelection();
        if (id < 0) {
            return;
        }
        try {
            dao.setStatus(id, status);
            FormHelper.info(this, "Driver " + (status.equals("ACTIVE") ? "activated" : "deactivated") + " successfully.");
            refresh();
        } catch (Exception ex) {
            FormHelper.handleException(this, ex);
        }
    }

    @Override
    protected List<Object[]> fetchRows(String keyword, String filter) throws Exception {
        List<Object[]> rows = new ArrayList<>();
        for (Driver d : dao.search(keyword, filter)) {
            rows.add(new Object[]{d.id(), d.name(), d.phone(), d.licenseNo(), d.address(), d.email(),
                    d.busNumber(), d.status()});
        }
        return rows;
    }

    @Override
    protected void addRecord() {
        new DriverFormDialog(owner(), null).setVisible(true);
        refresh();
    }

    @Override
    protected void editRecord(int id) throws Exception {
        Driver d = dao.findById(id);
        if (d != null) {
            new DriverFormDialog(owner(), d).setVisible(true);
            refresh();
        }
    }

    @Override
    protected void deleteRecord(int id) throws Exception {
        dao.delete(id);
    }
}
