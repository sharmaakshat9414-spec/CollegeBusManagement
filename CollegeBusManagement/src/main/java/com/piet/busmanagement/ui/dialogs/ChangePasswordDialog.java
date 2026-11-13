package com.piet.busmanagement.ui.dialogs;

import com.piet.busmanagement.config.Session;
import com.piet.busmanagement.dao.UserDAO;
import com.piet.busmanagement.ui.components.FormHelper;

import javax.swing.JPasswordField;
import java.awt.Window;

public class ChangePasswordDialog extends BaseFormDialog {

    private final JPasswordField current = FormHelper.passwordField("Current password");
    private final JPasswordField newPw = FormHelper.passwordField("At least 6 characters");
    private final JPasswordField confirm = FormHelper.passwordField("Repeat new password");

    public ChangePasswordDialog(Window owner) {
        super(owner, "Change Password", "Choose a new password for your account");
        addField("Current Password *", current);
        addField("New Password *", newPw);
        addField("Confirm Password *", confirm);
        finish(owner);
    }

    @Override
    protected boolean onSave() throws Exception {
        String old = new String(current.getPassword());
        String pw = new String(newPw.getPassword());
        String again = new String(confirm.getPassword());
        if (old.isEmpty() || pw.isEmpty() || again.isEmpty()) {
            FormHelper.warn(this, "Please fill all required fields.");
            return false;
        }
        if (pw.length() < 6) {
            FormHelper.warn(this, "New password must be at least 6 characters.");
            return false;
        }
        if (!pw.equals(again)) {
            FormHelper.warn(this, "New password and confirm password do not match.");
            return false;
        }
        new UserDAO().changePassword(Session.getUser().id(), old, pw);
        FormHelper.info(this, "Password changed successfully.");
        return true;
    }
}
