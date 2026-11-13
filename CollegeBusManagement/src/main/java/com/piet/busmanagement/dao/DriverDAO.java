package com.piet.busmanagement.dao;

import com.piet.busmanagement.config.AppException;
import com.piet.busmanagement.config.PasswordUtil;
import com.piet.busmanagement.model.Driver;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class DriverDAO extends BaseDAO {

    private static final String SELECT =
            "SELECT d.driver_id, d.user_id, d.name, d.phone, d.license_no, d.address, d.email, d.status, "
            + "COALESCE(b.bus_number, '-') AS bus_number FROM drivers d "
            + "LEFT JOIN bus_assignments a ON a.driver_id = d.driver_id AND a.status = 'ACTIVE' "
            + "LEFT JOIN buses b ON b.bus_id = a.bus_id ";

    private Driver map(ResultSet rs) throws SQLException {
        return new Driver(rs.getInt("driver_id"), rs.getInt("user_id"), str(rs, "name"), str(rs, "phone"),
                str(rs, "license_no"), str(rs, "address"), str(rs, "email"), str(rs, "status"),
                str(rs, "bus_number"));
    }

    public List<Driver> search(String keyword, String status) throws SQLException {
        String k = like(keyword);
        return query(SELECT + "WHERE (d.name LIKE ? OR d.phone LIKE ? OR d.license_no LIKE ? OR d.email LIKE ? "
                        + "OR d.address LIKE ?) AND (? = 'All' OR d.status = ?) ORDER BY d.driver_id",
                this::map, k, k, k, k, k, status, status);
    }

    public List<Driver> listAll() throws SQLException {
        return query(SELECT + "ORDER BY d.name", this::map);
    }

    public Driver findById(int id) throws SQLException {
        return queryOne(SELECT + "WHERE d.driver_id = ?", this::map, id);
    }

    public Driver findByUserId(int userId) throws SQLException {
        return queryOne(SELECT + "WHERE d.user_id = ?", this::map, userId);
    }

    private void checkDuplicates(Connection c, Driver d, int driverId, int userId) throws SQLException, AppException {
        if (existsTx(c, "SELECT 1 FROM drivers WHERE license_no = ? AND driver_id <> ?", d.licenseNo(), driverId)) {
            throw new AppException("This license number is already registered.");
        }
        if (existsTx(c, "SELECT 1 FROM drivers WHERE email = ? AND driver_id <> ?", d.email(), driverId)) {
            throw new AppException("This email is already used by another driver.");
        }
        if (existsTx(c, "SELECT 1 FROM users WHERE (username = ? OR email = ?) AND user_id <> ?",
                d.email(), d.email(), userId)) {
            throw new AppException("A user account with this email already exists.");
        }
    }

    /** Creates the driver login (username = email) and the driver record together. */
    public void add(Driver d, String plainPassword) throws SQLException, AppException {
        inTransaction(c -> {
            checkDuplicates(c, d, 0, 0);
            long userId = insertTx(c, "INSERT INTO users (username, email, password_hash, full_name, role, status) "
                            + "VALUES (?, ?, ?, ?, 'DRIVER', ?)",
                    d.email(), d.email(), PasswordUtil.hash(plainPassword), d.name(), d.status());
            insertTx(c, "INSERT INTO drivers (user_id, name, phone, license_no, address, email, status) "
                            + "VALUES (?, ?, ?, ?, ?, ?, ?)",
                    userId, d.name(), d.phone(), d.licenseNo(), d.address(), d.email(), d.status());
        });
    }

    public void update(Driver d, String newPassword) throws SQLException, AppException {
        inTransaction(c -> {
            checkDuplicates(c, d, d.id(), d.userId());
            updateTx(c, "UPDATE drivers SET name = ?, phone = ?, license_no = ?, address = ?, email = ?, status = ? "
                            + "WHERE driver_id = ?",
                    d.name(), d.phone(), d.licenseNo(), d.address(), d.email(), d.status(), d.id());
            updateTx(c, "UPDATE users SET username = ?, email = ?, full_name = ?, status = ? WHERE user_id = ?",
                    d.email(), d.email(), d.name(), d.status(), d.userId());
            if (newPassword != null && !newPassword.isEmpty()) {
                updateTx(c, "UPDATE users SET password_hash = ? WHERE user_id = ?",
                        PasswordUtil.hash(newPassword), d.userId());
            }
        });
    }

    /** Activate / deactivate the driver and the driver's login together. */
    public void setStatus(int driverId, String status) throws SQLException, AppException {
        Driver d = findById(driverId);
        if (d == null) {
            return;
        }
        inTransaction(c -> {
            updateTx(c, "UPDATE drivers SET status = ? WHERE driver_id = ?", status, driverId);
            updateTx(c, "UPDATE users SET status = ? WHERE user_id = ?", status, d.userId());
        });
    }

    /** Fails with a foreign-key error if the driver still has assignments. */
    public void delete(int id) throws SQLException, AppException {
        Driver d = findById(id);
        if (d == null) {
            return;
        }
        inTransaction(c -> {
            updateTx(c, "DELETE FROM drivers WHERE driver_id = ?", id);
            updateTx(c, "DELETE FROM users WHERE user_id = ?", d.userId());
        });
    }
}
