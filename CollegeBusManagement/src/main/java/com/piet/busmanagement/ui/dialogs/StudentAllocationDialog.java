package com.piet.busmanagement.ui.dialogs;

import com.piet.busmanagement.dao.BusDAO;
import com.piet.busmanagement.dao.RouteDAO;
import com.piet.busmanagement.dao.StudentBusAllocationDAO;
import com.piet.busmanagement.dao.StudentDAO;
import com.piet.busmanagement.model.Bus;
import com.piet.busmanagement.model.Route;
import com.piet.busmanagement.model.Student;
import com.piet.busmanagement.model.StudentBusAllocation;
import com.piet.busmanagement.ui.components.ComboItem;
import com.piet.busmanagement.ui.components.FormHelper;

import javax.swing.JComboBox;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;

public class StudentAllocationDialog extends BaseFormDialog {

    private final StudentBusAllocationDAO dao = new StudentBusAllocationDAO();
    private final StudentBusAllocation existing;
    private final JComboBox<ComboItem> student = new JComboBox<>();
    private final JComboBox<ComboItem> bus = new JComboBox<>();
    private final JComboBox<ComboItem> route = new JComboBox<>();
    private final JComboBox<String> pickup = new JComboBox<>();
    private final JComboBox<String> status = FormHelper.combo("ACTIVE", "INACTIVE");
    private final List<Route> routes = new ArrayList<>();

    public StudentAllocationDialog(Window owner, StudentBusAllocation existing) {
        super(owner, existing == null ? "Allocate Student to Bus" : "Edit Student Allocation",
                "Choose the bus, route and pickup stop for the student");
        this.existing = existing;
        pickup.setEditable(true);
        try {
            if (existing == null) {
                for (Student s : new StudentDAO().listUnallocated()) {
                    student.addItem(new ComboItem(s.id(), s.fullName() + " - " + s.regNo()));
                }
            } else {
                student.addItem(new ComboItem(existing.studentId(), existing.studentName() + " - " + existing.regNo()));
                student.setEnabled(false);
            }
            for (Bus b : new BusDAO().listAll()) {
                bus.addItem(new ComboItem(b.id(), b.busNumber() + " (" + b.status() + ", " + b.capacity() + " seats)"));
            }
            routes.addAll(new RouteDAO().listAll());
            for (Route r : routes) {
                route.addItem(new ComboItem(r.id(), r.routeName()));
            }
        } catch (Exception ex) {
            FormHelper.handleException(owner, ex);
        }
        route.addActionListener(e -> loadStops());
        addField("Student *", student);
        addField("Bus *", bus);
        addField("Route *", route);
        addField("Pickup Stop *", pickup);
        addField("Status *", status);
        loadStops();
        if (existing != null) {
            FormHelper.selectItem(bus, existing.busId());
            FormHelper.selectItem(route, existing.routeId());
            loadStops();
            pickup.setSelectedItem(existing.pickupStop());
            status.setSelectedItem(existing.status());
        }
        finish(owner);
    }

    /** Fills the pickup-stop list from the stops of the selected route. */
    private void loadStops() {
        pickup.removeAllItems();
        ComboItem item = (ComboItem) route.getSelectedItem();
        if (item == null) {
            return;
        }
        for (Route r : routes) {
            if (r.id() == item.id()) {
                for (String stop : r.stops().split(",")) {
                    if (!stop.isBlank()) {
                        pickup.addItem(stop.trim());
                    }
                }
            }
        }
    }

    @Override
    protected boolean onSave() throws Exception {
        Object stop = pickup.getEditor().getItem();
        if (student.getSelectedItem() == null || bus.getSelectedItem() == null || route.getSelectedItem() == null
                || stop == null || FormHelper.isBlank(stop.toString())) {
            FormHelper.warn(this, "Please fill all required fields.");
            return false;
        }
        StudentBusAllocation a = new StudentBusAllocation(existing == null ? 0 : existing.id(),
                ((ComboItem) student.getSelectedItem()).id(), ((ComboItem) bus.getSelectedItem()).id(),
                ((ComboItem) route.getSelectedItem()).id(), null, null, null, null, stop.toString().trim(),
                (String) status.getSelectedItem());
        if (existing == null) {
            dao.add(a);
            FormHelper.info(this, "Student allocated successfully.");
        } else {
            dao.update(a);
            FormHelper.info(this, "Allocation updated successfully.");
        }
        return true;
    }
}
