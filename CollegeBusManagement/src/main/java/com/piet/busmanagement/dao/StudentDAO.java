package com.piet.busmanagement.dao;

import com.piet.busmanagement.config.AppException;
import com.piet.busmanagement.config.PasswordUtil;
import com.piet.busmanagement.model.Student;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class StudentDAO extends BaseDAO {

    private static final String SELECT =
            "SELECT s.student_id, s.user_id, s.full_name, s.reg_no, s.email, s.phone, s.section, s.status, "
            + "COALESCE(b.bus_number, '-') AS bus_number, COALESCE(r.route_name, '-') AS route_name, "
            + "DATE_FORMAT(s.created_at, '%d %b %Y') AS created "
            + "FROM students s "
            + "LEFT JOIN student_bus_allocations a ON a.student_id = s.student_id AND a.status = 'ACTIVE' "
            + "LEFT JOIN buses b ON b.bus_id = a.bus_id "
            + "LEFT JOIN routes r ON r.route_id = a.route_id ";

    private Student map(ResultSet rs) throws SQLException {
        return new Student(rs.getInt("student_id"), rs.getInt("user_id"), str(rs, "full_name"),
                str(rs, "reg_no"), str(rs, "email"), str(rs, "phone"), str(rs, "section"),
                str(rs, "status"), str(rs, "bus_number"), str(rs, "route_name"), str(rs, "created"));
    }

    public List<Student> search(String keyword, String status) throws SQLException {
        String k = like(keyword);
        return query(SELECT + "WHERE (s.full_name LIKE ? OR s.reg_no LIKE ? OR s.email LIKE ? "
                        + "OR s.phone LIKE ? OR s.section LIKE ?) AND (? = 'All' OR s.status = ?) "
                        + "ORDER BY s.student_id",
                this::map, k, k, k, k, k, status, status);
    }

    public Student findById(int id) throws SQLException {
        return queryOne(SELECT + "WHERE s.student_id = ?", this::map, id);
    }

    public Student findByUserId(int userId) throws SQLException {
        return queryOne(SELECT + "WHERE s.user_id = ?", this::map, userId);
    }

    /** Active students that do not yet have any bus allocation. */
    public List<Student> listUnallocated() throws SQLException {
        return query(SELECT + "WHERE s.status = 'ACTIVE' AND NOT EXISTS "
                + "(SELECT 1 FROM student_bus_allocations x WHERE x.student_id = s.student_id) "
                + "ORDER BY s.full_name", this::map);
    }

    private void checkDuplicates(Connection c, Student s, int studentId, int userId) throws SQLException, AppException {
        if (existsTx(c, "SELECT 1 FROM students WHERE reg_no = ? AND student_id <> ?", s.regNo(), studentId)) {
            throw new AppException("This registration number is already registered.");
        }
        if (existsTx(c, "SELECT 1 FROM students WHERE email = ? AND student_id <> ?", s.email(), studentId)) {
            throw new AppException("This email is already registered.");
        }
        if (existsTx(c, "SELECT 1 FROM users WHERE (username = ? OR email = ? OR username = ? OR email = ?) "
                + "AND user_id <> ?", s.regNo(), s.regNo(), s.email(), s.email(), userId)) {
            throw new AppException("An account with this registration number or email already exists.");
        }
    }

    /** Creates the login account and the student record together (used by Sign Up and by Admin). */
    public void register(Student s, String plainPassword) throws SQLException, AppException {
        inTransaction(c -> {
            checkDuplicates(c, s, 0, 0);
            long userId = insertTx(c, "INSERT INTO users (username, email, password_hash, full_name, role, status) "
                            + "VALUES (?, ?, ?, ?, 'STUDENT', ?)",
                    s.regNo(), s.email(), PasswordUtil.hash(plainPassword), s.fullName(), s.status());
            insertTx(c, "INSERT INTO students (user_id, full_name, reg_no, email, phone, section, status) "
                            + "VALUES (?, ?, ?, ?, ?, ?, ?)",
                    userId, s.fullName(), s.regNo(), s.email(), s.phone(), s.section(), s.status());
        });
    }

    /** newPassword may be empty to keep the current password. */
    public void update(Student s, String newPassword) throws SQLException, AppException {
        inTransaction(c -> {
            checkDuplicates(c, s, s.id(), s.userId());
            updateTx(c, "UPDATE students SET full_name = ?, reg_no = ?, email = ?, phone = ?, section = ?, status = ? "
                            + "WHERE student_id = ?",
                    s.fullName(), s.regNo(), s.email(), s.phone(), s.section(), s.status(), s.id());
            updateTx(c, "UPDATE users SET username = ?, email = ?, full_name = ?, status = ? WHERE user_id = ?",
                    s.regNo(), s.email(), s.fullName(), s.status(), s.userId());
            if (newPassword != null && !newPassword.isEmpty()) {
                updateTx(c, "UPDATE users SET password_hash = ? WHERE user_id = ?",
                        PasswordUtil.hash(newPassword), s.userId());
            }
        });
    }

    /** Deletes the student, their bus allocation (cascade) and their login account. */
    public void delete(int id) throws SQLException, AppException {
        Student s = findById(id);
        if (s == null) {
            return;
        }
        inTransaction(c -> {
            updateTx(c, "DELETE FROM students WHERE student_id = ?", id);
            updateTx(c, "DELETE FROM users WHERE user_id = ?", s.userId());
        });
    }
}
