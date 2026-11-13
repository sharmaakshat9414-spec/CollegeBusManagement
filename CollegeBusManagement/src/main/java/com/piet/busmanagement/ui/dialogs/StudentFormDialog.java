package com.piet.busmanagement.ui.dialogs;

import com.piet.busmanagement.dao.StudentDAO;
import com.piet.busmanagement.model.Student;
import com.piet.busmanagement.ui.components.FormHelper;

import javax.swing.JComboBox;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.Window;

public class StudentFormDialog extends BaseFormDialog {

    private final StudentDAO dao = new StudentDAO();
    private final Student existing;
    private final JTextField name = FormHelper.textField("Full name", null);
    private final JTextField regNo = FormHelper.textField("e.g. PIET24CS101", null);
    private final JTextField email = FormHelper.textField("student@example.com", null);
    private final JTextField phone = FormHelper.textField("10-digit mobile number", null);
    private final JTextField section = FormHelper.textField("e.g. CSE-A", null);
    private final JComboBox<String> status = FormHelper.combo("ACTIVE", "INACTIVE");
    private final JPasswordField password = FormHelper.passwordField(null);

    public StudentFormDialog(Window owner, Student existing) {
        super(owner, existing == null ? "Add Student" : "Edit Student",
                existing == null ? "A login account is created for the student" : "Update student details");
        this.existing = existing;
        password.putClientProperty("JTextField.placeholderText",
                existing == null ? "At least 6 characters" : "Leave blank to keep current password");
        addField("Full Name *", name);
        addField("Registration No. *", regNo);
        addField("Email *", email);
        addField("Phone *", phone);
        addField("Section *", section);
        addField("Status *", status);
        addField(existing == null ? "Password *" : "New Password", password);
        if (existing != null) {
            name.setText(existing.fullName());
            regNo.setText(existing.regNo());
            email.setText(existing.email());
            phone.setText(existing.phone());
            section.setText(existing.section());
            status.setSelectedItem(existing.status());
        }
        finish(owner);
    }

    @Override
    protected boolean onSave() throws Exception {
        String pw = new String(password.getPassword());
        if (FormHelper.isBlank(name.getText()) || FormHelper.isBlank(regNo.getText()) || FormHelper.isBlank(email.getText())
                || FormHelper.isBlank(phone.getText()) || FormHelper.isBlank(section.getText())
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
        Student s = new Student(existing == null ? 0 : existing.id(), existing == null ? 0 : existing.userId(),
                text(name), text(regNo).toUpperCase(), text(email).toLowerCase(), text(phone), text(section).toUpperCase(),
                (String) status.getSelectedItem(), null, null, null);
        if (existing == null) {
            dao.register(s, pw);
            FormHelper.info(this, "Student added successfully.");
        } else {
            dao.update(s, pw);
            FormHelper.info(this, "Student updated successfully.");
        }
        return true;
    }
}
