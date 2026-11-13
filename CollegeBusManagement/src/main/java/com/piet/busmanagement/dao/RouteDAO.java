package com.piet.busmanagement.dao;

import com.piet.busmanagement.config.AppException;
import com.piet.busmanagement.model.Route;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class RouteDAO extends BaseDAO {

    private static final String SELECT =
            "SELECT route_id, route_name, start_point, destination, stops, distance_km, est_time_min, "
            + "departure_time, status FROM routes ";

    private Route map(ResultSet rs) throws SQLException {
        return new Route(rs.getInt("route_id"), str(rs, "route_name"), str(rs, "start_point"),
                str(rs, "destination"), str(rs, "stops"), rs.getDouble("distance_km"),
                rs.getInt("est_time_min"), str(rs, "departure_time"), str(rs, "status"));
    }

    public List<Route> search(String keyword, String status) throws SQLException {
        String k = like(keyword);
        return query(SELECT + "WHERE (route_name LIKE ? OR start_point LIKE ? OR destination LIKE ? OR stops LIKE ?) "
                        + "AND (? = 'All' OR status = ?) ORDER BY route_id",
                this::map, k, k, k, k, status, status);
    }

    public List<Route> listAll() throws SQLException {
        return query(SELECT + "ORDER BY route_name", this::map);
    }

    public Route findById(int id) throws SQLException {
        return queryOne(SELECT + "WHERE route_id = ?", this::map, id);
    }

    private void checkDuplicate(Route r, int id) throws SQLException, AppException {
        if (count("SELECT COUNT(*) FROM routes WHERE route_name = ? AND route_id <> ?", r.routeName(), id) > 0) {
            throw new AppException("A route with this name already exists.");
        }
    }

    public void add(Route r) throws SQLException, AppException {
        checkDuplicate(r, 0);
        update("INSERT INTO routes (route_name, start_point, destination, stops, distance_km, est_time_min, "
                        + "departure_time, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                r.routeName(), r.startPoint(), r.destination(), r.stops(), r.distanceKm(), r.estTimeMin(),
                r.departureTime(), r.status());
    }

    public void update(Route r) throws SQLException, AppException {
        checkDuplicate(r, r.id());
        update("UPDATE routes SET route_name = ?, start_point = ?, destination = ?, stops = ?, distance_km = ?, "
                        + "est_time_min = ?, departure_time = ?, status = ? WHERE route_id = ?",
                r.routeName(), r.startPoint(), r.destination(), r.stops(), r.distanceKm(), r.estTimeMin(),
                r.departureTime(), r.status(), r.id());
    }

    public void delete(int id) throws SQLException {
        update("DELETE FROM routes WHERE route_id = ?", id);
    }
}
