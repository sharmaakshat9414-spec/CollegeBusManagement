package com.piet.busmanagement.dao;

import com.piet.busmanagement.config.AppException;
import com.piet.busmanagement.model.BusAssignment;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class BusAssignmentDAO extends BaseDAO {

    private static final String SELECT =
            "SELECT a.assignment_id, a.bus_id, a.driver_id, a.route_id, b.bus_number, d.name AS driver_name, "
            + "r.route_name, a.assignment_date, a.status FROM bus_assignments a "
            + "JOIN buses b ON b.bus_id = a.bus_id JOIN drivers d ON d.driver_id = a.driver_id "
            + "JOIN routes r ON r.route_id = a.route_id ";

    private BusAssignment map(ResultSet rs) throws SQLException {
        return new BusAssignment(rs.getInt("assignment_id"), rs.getInt("bus_id"), rs.getInt("driver_id"),
                rs.getInt("route_id"), str(rs, "bus_number"), str(rs, "driver_name"), str(rs, "route_name"),
                str(rs, "assignment_date"), str(rs, "status"));
    }

    public List<BusAssignment> search(String keyword, String status) throws SQLException {
        String k = like(keyword);
        return query(SELECT + "WHERE (b.bus_number LIKE ? OR d.name LIKE ? OR r.route_name LIKE ?) "
                        + "AND (? = 'All' OR a.status = ?) ORDER BY a.assignment_id DESC",
                this::map, k, k, k, status, status);
    }

    public BusAssignment findById(int id) throws SQLException {
        return queryOne(SELECT + "WHERE a.assignment_id = ?", this::map, id);
    }

    private void validate(BusAssignment a, int ignoreId) throws SQLException, AppException {
        if (!"ACTIVE".equals(a.status())) {
            return;
        }
        if (count("SELECT COUNT(*) FROM buses WHERE bus_id = ? AND status = 'ACTIVE'", a.busId()) == 0) {
            throw new AppException("Only buses with ACTIVE status can be assigned.");
        }
        if (count("SELECT COUNT(*) FROM drivers WHERE driver_id = ? AND status = 'ACTIVE'", a.driverId()) == 0) {
            throw new AppException("Only ACTIVE drivers can be assigned.");
        }
        if (count("SELECT COUNT(*) FROM routes WHERE route_id = ? AND status = 'ACTIVE'", a.routeId()) == 0) {
            throw new AppException("Only ACTIVE routes can be assigned.");
        }
        if (count("SELECT COUNT(*) FROM bus_assignments WHERE bus_id = ? AND status = 'ACTIVE' AND assignment_id <> ?",
                a.busId(), ignoreId) > 0) {
            throw new AppException("This bus already has an active assignment.");
        }
        if (count("SELECT COUNT(*) FROM bus_assignments WHERE driver_id = ? AND status = 'ACTIVE' AND assignment_id <> ?",
                a.driverId(), ignoreId) > 0) {
            throw new AppException("This driver already has an active assignment.");
        }
    }

    public void add(BusAssignment a) throws SQLException, AppException {
        validate(a, 0);
        update("INSERT INTO bus_assignments (bus_id, driver_id, route_id, assignment_date, status) VALUES (?, ?, ?, ?, ?)",
                a.busId(), a.driverId(), a.routeId(), a.assignmentDate(), a.status());
    }

    public void update(BusAssignment a) throws SQLException, AppException {
        validate(a, a.id());
        update("UPDATE bus_assignments SET bus_id = ?, driver_id = ?, route_id = ?, assignment_date = ?, status = ? "
                        + "WHERE assignment_id = ?",
                a.busId(), a.driverId(), a.routeId(), a.assignmentDate(), a.status(), a.id());
    }

    public void delete(int id) throws SQLException {
        update("DELETE FROM bus_assignments WHERE assignment_id = ?", id);
    }
}
