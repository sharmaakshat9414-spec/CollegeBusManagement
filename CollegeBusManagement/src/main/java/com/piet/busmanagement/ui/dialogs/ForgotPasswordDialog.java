package com.piet.busmanagement.ui.dialogs;

import com.piet.busmanagement.dao.UserDAO;
import com.piet.busmanagement.ui.components.FormHelper;
import com.piet.busmanagement.ui.components.IconFactory;

import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.Window;

/** Resets a password when the username and the registered email both match an account. */
public class ForgotPasswordDialog extends BaseFormDialog {

    private final JTextField login = FormHelper.textField("Username / registration no.", IconFactory.Type.USER);
    private final JTextField email = FormHelper.textField("Registered email", IconFactory.Type.MAIL);
    private final JPasswordField newPw = FormHelper.passwordField("At least 6 characters");
    private final JPasswordField confirm = FormHelper.passwordField("Repeat new password");

    public ForgotPasswordDialog(Window owner) {
        super(owner, "Forgot Password", "Verify your account details to set a new password");
        addField("Username *", login);
        addField("Registered Email *", email);
        addField("New Password *", newPw);
        addField("Confirm Password *", confirm);
        finish(owner);
    }

    @Override
    protected boolean onSave() throws Exception {
        String pw = new String(newPw.getPassword());
        if (FormHelper.isBlank(login.getText()) || FormHelper.isBlank(email.getText()) || pw.isEmpty()) {
            FormHelper.warn(this, "Please fill all required fields.");
            return false;
        }
        if (!FormHelper.isEmail(text(email))) {
            FormHelper.warn(this, "Please enter a valid email address.");
            return false;
        }
        if (pw.length() < 6) {
            FormHelper.warn(this, "Password must be at least 6 characters.");
            return false;
        }
        if (!pw.equals(new String(confirm.getPassword()))) {
            FormHelper.warn(this, "New password and confirm password do not match.");
            return false;
        }
        new UserDAO().resetPassword(text(login), text(email), pw);
        FormHelper.info(this, "Password reset successfully. You can now log in.");
        return true;
    }
}
