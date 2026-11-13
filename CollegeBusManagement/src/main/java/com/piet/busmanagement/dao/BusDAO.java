package com.piet.busmanagement.dao;

import com.piet.busmanagement.config.AppException;
import com.piet.busmanagement.model.Bus;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class BusDAO extends BaseDAO {

    // Driver and route of a bus come from its ACTIVE assignment.
    private static final String SELECT =
            "SELECT b.bus_id, b.bus_number, b.registration_no, b.bus_type, b.capacity, b.status, "
            + "COALESCE(d.name, '-') AS driver_name, COALESCE(r.route_name, '-') AS route_name FROM buses b "
            + "LEFT JOIN bus_assignments a ON a.bus_id = b.bus_id AND a.status = 'ACTIVE' "
            + "LEFT JOIN drivers d ON d.driver_id = a.driver_id "
            + "LEFT JOIN routes r ON r.route_id = a.route_id ";

    private Bus map(ResultSet rs) throws SQLException {
        return new Bus(rs.getInt("bus_id"), str(rs, "bus_number"), str(rs, "registration_no"),
                str(rs, "bus_type"), rs.getInt("capacity"), str(rs, "status"),
                str(rs, "driver_name"), str(rs, "route_name"));
    }

    public List<Bus> search(String keyword, String status) throws SQLException {
        String k = like(keyword);
        return query(SELECT + "WHERE (b.bus_number LIKE ? OR b.registration_no LIKE ? OR b.bus_type LIKE ? "
                        + "OR d.name LIKE ? OR r.route_name LIKE ?) AND (? = 'All' OR b.status = ?) ORDER BY b.bus_id",
                this::map, k, k, k, k, k, status, status);
    }

    public List<Bus> listAll() throws SQLException {
        return query(SELECT + "ORDER BY b.bus_number", this::map);
    }

    public Bus findById(int id) throws SQLException {
        return queryOne(SELECT + "WHERE b.bus_id = ?", this::map, id);
    }

    private void checkDuplicates(Bus b, int id) throws SQLException, AppException {
        if (count("SELECT COUNT(*) FROM buses WHERE bus_number = ? AND bus_id <> ?", b.busNumber(), id) > 0) {
            throw new AppException("This bus number already exists.");
        }
        if (count("SELECT COUNT(*) FROM buses WHERE registration_no = ? AND bus_id <> ?", b.registrationNo(), id) > 0) {
            throw new AppException("This registration number already exists.");
        }
    }

    public void add(Bus b) throws SQLException, AppException {
        checkDuplicates(b, 0);
        update("INSERT INTO buses (bus_number, registration_no, bus_type, capacity, status) VALUES (?, ?, ?, ?, ?)",
                b.busNumber(), b.registrationNo(), b.busType(), b.capacity(), b.status());
    }

    public void update(Bus b) throws SQLException, AppException {
        checkDuplicates(b, b.id());
        int used = count("SELECT COUNT(*) FROM student_bus_allocations WHERE bus_id = ? AND status = 'ACTIVE'", b.id());
        if (b.capacity() < used) {
            throw new AppException("Capacity cannot be lower than the " + used + " students already allocated.");
        }
        update("UPDATE buses SET bus_number = ?, registration_no = ?, bus_type = ?, capacity = ?, status = ? "
                        + "WHERE bus_id = ?",
                b.busNumber(), b.registrationNo(), b.busType(), b.capacity(), b.status(), b.id());
    }

    /** Fails with a foreign-key error when the bus is used by assignments or allocations. */
    public void delete(int id) throws SQLException {
        update("DELETE FROM buses WHERE bus_id = ?", id);
    }
}
