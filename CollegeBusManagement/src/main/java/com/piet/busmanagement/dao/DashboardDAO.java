package com.piet.busmanagement.dao;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Read-only queries for the three dashboards. */
public class DashboardDAO extends BaseDAO {

    public int totalBuses() throws SQLException {
        return count("SELECT COUNT(*) FROM buses");
    }

    public int totalStudents() throws SQLException {
        return count("SELECT COUNT(*) FROM students");
    }

    public int totalDrivers() throws SQLException {
        return count("SELECT COUNT(*) FROM drivers");
    }

    public int totalRoutes() throws SQLException {
        return count("SELECT COUNT(*) FROM routes");
    }

    public int activeAssignments() throws SQLException {
        return count("SELECT COUNT(*) FROM bus_assignments WHERE status = 'ACTIVE'");
    }

    /** Rows of {name, regNo, section, registered on}. */
    public List<String[]> recentStudents(int limit) throws SQLException {
        return query("SELECT full_name, reg_no, section, DATE_FORMAT(created_at, '%d %b %Y') AS created "
                        + "FROM students ORDER BY student_id DESC LIMIT ?",
                rs -> new String[]{str(rs, "full_name"), str(rs, "reg_no"), str(rs, "section"), str(rs, "created")},
                limit);
    }

    /** Rows of {bus, driver, route, date}. */
    public List<String[]> recentAssignments(int limit) throws SQLException {
        return query("SELECT b.bus_number, d.name, r.route_name, DATE_FORMAT(a.assignment_date, '%d %b %Y') AS dt "
                        + "FROM bus_assignments a JOIN buses b ON b.bus_id = a.bus_id "
                        + "JOIN drivers d ON d.driver_id = a.driver_id JOIN routes r ON r.route_id = a.route_id "
                        + "ORDER BY a.assignment_id DESC LIMIT ?",
                rs -> new String[]{str(rs, "bus_number"), str(rs, "name"), str(rs, "route_name"), str(rs, "dt")},
                limit);
    }

    public Map<String, Integer> busStatusCounts() throws SQLException {
        Map<String, Integer> map = new LinkedHashMap<>();
        map.put("ACTIVE", 0);
        map.put("MAINTENANCE", 0);
        map.put("INACTIVE", 0);
        query("SELECT status, COUNT(*) AS total FROM buses GROUP BY status", rs -> {
            map.put(rs.getString("status"), rs.getInt("total"));
            return null;
        });
        return map;
    }

    private static Map<String, String> rowToMap(ResultSet rs) throws SQLException {
        Map<String, String> map = new LinkedHashMap<>();
        ResultSetMetaData md = rs.getMetaData();
        for (int i = 1; i <= md.getColumnCount(); i++) {
            String v = rs.getString(i);
            map.put(md.getColumnLabel(i), v == null || v.isBlank() ? "-" : v);
        }
        return map;
    }

    /** Transport details of a student, or null when no active allocation exists. */
    public Map<String, String> studentTransport(int studentId) throws SQLException {
        return queryOne("SELECT b.bus_number, b.bus_type, r.route_name, r.stops, r.departure_time, r.est_time_min, "
                        + "a.pickup_stop, d.name AS driver_name, d.phone AS driver_phone "
                        + "FROM student_bus_allocations a JOIN buses b ON b.bus_id = a.bus_id "
                        + "JOIN routes r ON r.route_id = a.route_id "
                        + "LEFT JOIN bus_assignments ba ON ba.bus_id = b.bus_id AND ba.status = 'ACTIVE' "
                        + "LEFT JOIN drivers d ON d.driver_id = ba.driver_id "
                        + "WHERE a.student_id = ? AND a.status = 'ACTIVE'",
                DashboardDAO::rowToMap, studentId);
    }

    /** Active assignment details of a driver, or null when the driver has no active assignment. */
    public Map<String, String> driverTransport(int driverId) throws SQLException {
        return queryOne("SELECT b.bus_number, b.bus_type, r.route_name, r.stops, r.departure_time, r.est_time_min, "
                        + "(SELECT COUNT(*) FROM student_bus_allocations x WHERE x.bus_id = b.bus_id "
                        + "AND x.status = 'ACTIVE') AS students "
                        + "FROM bus_assignments a JOIN buses b ON b.bus_id = a.bus_id "
                        + "JOIN routes r ON r.route_id = a.route_id "
                        + "WHERE a.driver_id = ? AND a.status = 'ACTIVE'",
                DashboardDAO::rowToMap, driverId);
    }
}
