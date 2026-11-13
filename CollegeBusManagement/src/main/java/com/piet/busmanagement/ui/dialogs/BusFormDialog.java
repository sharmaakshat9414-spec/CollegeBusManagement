package com.piet.busmanagement.ui.dialogs;

import com.piet.busmanagement.dao.BusDAO;
import com.piet.busmanagement.model.Bus;
import com.piet.busmanagement.ui.components.FormHelper;

import javax.swing.JComboBox;
import javax.swing.JTextField;
import java.awt.Window;

public class BusFormDialog extends BaseFormDialog {

    private final BusDAO dao = new BusDAO();
    private final Bus existing;
    private final JTextField busNumber = FormHelper.textField("e.g. PIET-07", null);
    private final JTextField regNo = FormHelper.textField("e.g. RJ14-PA-1107", null);
    private final JComboBox<String> type = FormHelper.combo("AC Bus", "Non-AC Bus", "Mini Bus", "Electric Bus");
    private final JTextField capacity = FormHelper.textField("Number of seats (1 - 100)", null);
    private final JComboBox<String> status = FormHelper.combo("ACTIVE", "INACTIVE", "MAINTENANCE");

    public BusFormDialog(Window owner, Bus existing) {
        super(owner, existing == null ? "Add Bus" : "Edit Bus", "Bus details (driver and route are set in Bus Assignments)");
        this.existing = existing;
        addField("Bus Number *", busNumber);
        addField("Registration No. *", regNo);
        addField("Bus Type *", type);
        addField("Capacity *", capacity);
        addField("Status *", status);
        if (existing != null) {
            busNumber.setText(existing.busNumber());
            regNo.setText(existing.registrationNo());
            type.setSelectedItem(existing.busType());
            capacity.setText(String.valueOf(existing.capacity()));
            status.setSelectedItem(existing.status());
        }
        finish(owner);
    }

    @Override
    protected boolean onSave() throws Exception {
        if (FormHelper.isBlank(busNumber.getText()) || FormHelper.isBlank(regNo.getText()) || FormHelper.isBlank(capacity.getText())) {
            FormHelper.warn(this, "Please fill all required fields.");
            return false;
        }
        int cap;
        try {
            cap = Integer.parseInt(text(capacity));
        } catch (NumberFormatException e) {
            FormHelper.warn(this, "Capacity must be a whole number.");
            return false;
        }
        if (cap < 1 || cap > 100) {
            FormHelper.warn(this, "Capacity must be between 1 and 100.");
            return false;
        }
        Bus bus = new Bus(existing == null ? 0 : existing.id(), text(busNumber).toUpperCase(), text(regNo).toUpperCase(),
                (String) type.getSelectedItem(), cap, (String) status.getSelectedItem(), null, null);
        if (existing == null) {
            dao.add(bus);
            FormHelper.info(this, "Bus added successfully.");
        } else {
            dao.update(bus);
            FormHelper.info(this, "Bus updated successfully.");
        }
        return true;
    }
}
