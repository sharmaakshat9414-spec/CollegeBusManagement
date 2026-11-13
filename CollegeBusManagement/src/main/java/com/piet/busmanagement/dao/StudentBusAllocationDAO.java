package com.piet.busmanagement.dao;

import com.piet.busmanagement.config.AppException;
import com.piet.busmanagement.model.StudentBusAllocation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class StudentBusAllocationDAO extends BaseDAO {

    private static final String SELECT =
            "SELECT a.allocation_id, a.student_id, a.bus_id, a.route_id, s.full_name, s.reg_no, b.bus_number, "
            + "r.route_name, a.pickup_stop, a.status FROM student_bus_allocations a "
            + "JOIN students s ON s.student_id = a.student_id JOIN buses b ON b.bus_id = a.bus_id "
            + "JOIN routes r ON r.route_id = a.route_id ";

    private StudentBusAllocation map(ResultSet rs) throws SQLException {
        return new StudentBusAllocation(rs.getInt("allocation_id"), rs.getInt("student_id"), rs.getInt("bus_id"),
                rs.getInt("route_id"), str(rs, "full_name"), str(rs, "reg_no"), str(rs, "bus_number"),
                str(rs, "route_name"), str(rs, "pickup_stop"), str(rs, "status"));
    }

    public List<StudentBusAllocation> search(String keyword, String status) throws SQLException {
        String k = like(keyword);
        return query(SELECT + "WHERE (s.full_name LIKE ? OR s.reg_no LIKE ? OR b.bus_number LIKE ? "
                        + "OR r.route_name LIKE ? OR a.pickup_stop LIKE ?) AND (? = 'All' OR a.status = ?) "
                        + "ORDER BY a.allocation_id",
                this::map, k, k, k, k, k, status, status);
    }

    public StudentBusAllocation findById(int id) throws SQLException {
        return queryOne(SELECT + "WHERE a.allocation_id = ?", this::map, id);
    }

    private void validate(StudentBusAllocation a, int ignoreId) throws SQLException, AppException {
        if (count("SELECT COUNT(*) FROM student_bus_allocations WHERE student_id = ? AND allocation_id <> ?",
                a.studentId(), ignoreId) > 0) {
            throw new AppException("This student already has a bus allocation. Edit the existing one instead.");
        }
        if (!"ACTIVE".equals(a.status())) {
            return;
        }
        if (count("SELECT COUNT(*) FROM buses WHERE bus_id = ? AND status = 'ACTIVE'", a.busId()) == 0) {
            throw new AppException("Students can only be allocated to ACTIVE buses.");
        }
        int capacity = count("SELECT capacity FROM buses WHERE bus_id = ?", a.busId());
        int used = count("SELECT COUNT(*) FROM student_bus_allocations WHERE bus_id = ? AND status = 'ACTIVE' "
                + "AND allocation_id <> ?", a.busId(), ignoreId);
        if (used >= capacity) {
            throw new AppException("The selected bus is full (capacity " + capacity + ").");
        }
    }

    public void add(StudentBusAllocation a) throws SQLException, AppException {
        validate(a, 0);
        update("INSERT INTO student_bus_allocations (student_id, bus_id, route_id, pickup_stop, status) "
                        + "VALUES (?, ?, ?, ?, ?)",
                a.studentId(), a.busId(), a.routeId(), a.pickupStop(), a.status());
    }

    public void update(StudentBusAllocation a) throws SQLException, AppException {
        validate(a, a.id());
        update("UPDATE student_bus_allocations SET bus_id = ?, route_id = ?, pickup_stop = ?, status = ? "
                        + "WHERE allocation_id = ?",
                a.busId(), a.routeId(), a.pickupStop(), a.status(), a.id());
    }

    public void delete(int id) throws SQLException {
        update("DELETE FROM student_bus_allocations WHERE allocation_id = ?", id);
    }
}
