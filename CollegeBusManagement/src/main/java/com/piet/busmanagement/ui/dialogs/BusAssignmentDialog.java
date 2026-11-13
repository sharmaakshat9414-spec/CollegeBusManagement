package com.piet.busmanagement.ui.dialogs;

import com.piet.busmanagement.dao.BusAssignmentDAO;
import com.piet.busmanagement.dao.BusDAO;
import com.piet.busmanagement.dao.DriverDAO;
import com.piet.busmanagement.dao.RouteDAO;
import com.piet.busmanagement.model.Bus;
import com.piet.busmanagement.model.BusAssignment;
import com.piet.busmanagement.model.Driver;
import com.piet.busmanagement.model.Route;
import com.piet.busmanagement.ui.components.ComboItem;
import com.piet.busmanagement.ui.components.FormHelper;

import javax.swing.JComboBox;
import javax.swing.JTextField;
import java.awt.Window;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class BusAssignmentDialog extends BaseFormDialog {

    private final BusAssignmentDAO dao = new BusAssignmentDAO();
    private final BusAssignment existing;
    private final JComboBox<ComboItem> bus = new JComboBox<>();
    private final JComboBox<ComboItem> driver = new JComboBox<>();
    private final JComboBox<ComboItem> route = new JComboBox<>();
    private final JTextField date = FormHelper.textField("yyyy-MM-dd", null);
    private final JComboBox<String> status = FormHelper.combo("ACTIVE", "INACTIVE");

    public BusAssignmentDialog(Window owner, BusAssignment existing) {
        super(owner, existing == null ? "New Bus Assignment" : "Edit Bus Assignment",
                "Assign a driver and a route to a bus");
        this.existing = existing;
        try {
            for (Bus b : new BusDAO().listAll()) {
                bus.addItem(new ComboItem(b.id(), b.busNumber() + " (" + b.status() + ")"));
            }
            for (Driver d : new DriverDAO().listAll()) {
                driver.addItem(new ComboItem(d.id(), d.name() + " (" + d.status() + ")"));
            }
            for (Route r : new RouteDAO().listAll()) {
                route.addItem(new ComboItem(r.id(), r.routeName() + " (" + r.status() + ")"));
            }
        } catch (Exception ex) {
            FormHelper.handleException(owner, ex);
        }
        addField("Bus *", bus);
        addField("Driver *", driver);
        addField("Route *", route);
        addField("Assignment Date *", date);
        addField("Status *", status);
        date.setText(existing == null ? LocalDate.now().toString() : existing.assignmentDate());
        if (existing != null) {
            FormHelper.selectItem(bus, existing.busId());
            FormHelper.selectItem(driver, existing.driverId());
            FormHelper.selectItem(route, existing.routeId());
            status.setSelectedItem(existing.status());
        }
        finish(owner);
    }

    @Override
    protected boolean onSave() throws Exception {
        if (bus.getSelectedItem() == null || driver.getSelectedItem() == null || route.getSelectedItem() == null) {
            FormHelper.warn(this, "Please select a bus, a driver and a route.");
            return false;
        }
        try {
            LocalDate.parse(text(date));
        } catch (DateTimeParseException e) {
            FormHelper.warn(this, "Date must be in yyyy-MM-dd format, for example 2025-07-15.");
            return false;
        }
        BusAssignment a = new BusAssignment(existing == null ? 0 : existing.id(),
                ((ComboItem) bus.getSelectedItem()).id(), ((ComboItem) driver.getSelectedItem()).id(),
                ((ComboItem) route.getSelectedItem()).id(), null, null, null, text(date),
                (String) status.getSelectedItem());
        if (existing == null) {
            dao.add(a);
            FormHelper.info(this, "Bus assignment added successfully.");
        } else {
            dao.update(a);
            FormHelper.info(this, "Bus assignment updated successfully.");
        }
        return true;
    }
}
