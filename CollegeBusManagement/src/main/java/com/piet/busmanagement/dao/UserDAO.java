package com.piet.busmanagement.dao;

import com.piet.busmanagement.config.AppException;
import com.piet.busmanagement.config.PasswordUtil;
import com.piet.busmanagement.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO extends BaseDAO {

    private User map(ResultSet rs) throws SQLException {
        return new User(rs.getInt("user_id"), str(rs, "username"), str(rs, "email"),
                str(rs, "full_name"), str(rs, "role"), str(rs, "status"));
    }

    /** Returns the user when login (username or email), password and role match; otherwise null. */
    public User authenticate(String login, String password, String role) throws SQLException {
        return queryOne("SELECT user_id, username, email, full_name, role, status FROM users "
                        + "WHERE (username = ? OR email = ?) AND password_hash = ? AND role = ?",
                this::map, login, login, PasswordUtil.hash(password), role);
    }

    public void changePassword(int userId, String oldPassword, String newPassword) throws SQLException, AppException {
        int ok = count("SELECT COUNT(*) FROM users WHERE user_id = ? AND password_hash = ?",
                userId, PasswordUtil.hash(oldPassword));
        if (ok == 0) {
            throw new AppException("Current password is incorrect.");
        }
        update("UPDATE users SET password_hash = ? WHERE user_id = ?", PasswordUtil.hash(newPassword), userId);
    }

    /** Forgot-password: the username/email and the registered email must both match. */
    public void resetPassword(String login, String email, String newPassword) throws SQLException, AppException {
        int changed = update("UPDATE users SET password_hash = ? WHERE (username = ? OR email = ?) AND email = ?",
                PasswordUtil.hash(newPassword), login, login, email);
        if (changed == 0) {
            throw new AppException("No account found with these details.");
        }
    }
}
