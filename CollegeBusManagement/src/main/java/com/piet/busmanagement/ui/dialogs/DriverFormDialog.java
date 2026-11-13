package com.piet.busmanagement.ui.dialogs;

import com.piet.busmanagement.dao.DriverDAO;
import com.piet.busmanagement.model.Driver;
import com.piet.busmanagement.ui.components.FormHelper;

import javax.swing.JComboBox;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.Window;

public class DriverFormDialog extends BaseFormDialog {

    private final DriverDAO dao = new DriverDAO();
    private final Driver existing;
    private final JTextField name = FormHelper.textField("Driver name", null);
    private final JTextField phone = FormHelper.textField("10-digit mobile number", null);
    private final JTextField license = FormHelper.textField("e.g. RJ14-20200012345", null);
    private final JTextField address = FormHelper.textField("Home address", null);
    private final JTextField email = FormHelper.textField("Used as the login username", null);
    private final JComboBox<String> status = FormHelper.combo("ACTIVE", "INACTIVE");
    private final JPasswordField password = FormHelper.passwordField(null);

    public DriverFormDialog(Window owner, Driver existing) {
        super(owner, existing == null ? "Add Driver" : "Edit Driver", "The email is the driver's login username");
        this.existing = existing;
        password.putClientProperty("JTextField.placeholderText",
                existing == null ? "At least 6 characters" : "Leave blank to keep current password");
        addField("Driver Name *", name);
        addField("Phone *", phone);
        addField("License No. *", license);
        addField("Address *", address);
        addField("Email / Username *", email);
        addField("Status *", status);
        addField(existing == null ? "Password *" : "New Password", password);
        if (existing != null) {
            name.setText(existing.name());
            phone.setText(existing.phone());
            license.setText(existing.licenseNo());
            address.setText(existing.address());
            email.setText(existing.email());
            status.setSelectedItem(existing.status());
        }
        finish(owner);
    }

    @Override
    protected boolean onSave() throws Exception {
        String pw = new String(password.getPassword());
        if (FormHelper.isBlank(name.getText()) || FormHelper.isBlank(phone.getText()) || FormHelper.isBlank(license.getText())
                || FormHelper.isBlank(address.getText()) || FormHelper.isBlank(email.getText())
                || (existing == null && pw.isEmpty())) {
            FormHelper.warn(this, "Please fill all required fields.");
            return false;
        }
        if (!FormHelper.isEmail(text(email))) {
            FormHelper.warn(this, "Please enter a valid email address.");
            return false;
        }
        if (!FormHelper.isPhone(text(phone))) {
            FormHelper.warn(this, "Please enter a valid 10-digit phone number.");
            return false;
        }
        if (!pw.isEmpty() && pw.length() < 6) {
            FormHelper.warn(this, "Password must be at least 6 characters.");
            return false;
        }
        Driver d = new Driver(existing == null ? 0 : existing.id(), existing == null ? 0 : existing.userId(),
                text(name), text(phone), text(license).toUpperCase(), text(address), text(email).toLowerCase(),
                (String) status.getSelectedItem(), null);
        if (existing == null) {
            dao.add(d, pw);
            FormHelper.info(this, "Driver added successfully.");
        } else {
            dao.update(d, pw);
            FormHelper.info(this, "Driver updated successfully.");
        }
        return true;
    }
}
