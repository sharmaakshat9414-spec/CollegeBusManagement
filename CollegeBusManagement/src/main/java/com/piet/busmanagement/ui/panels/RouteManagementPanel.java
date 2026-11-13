package com.piet.busmanagement.ui.panels;

import com.piet.busmanagement.dao.RouteDAO;
import com.piet.busmanagement.model.Route;
import com.piet.busmanagement.ui.dialogs.RouteFormDialog;

import java.util.ArrayList;
import java.util.List;

public class RouteManagementPanel extends CrudPanel {

    private final RouteDAO dao = new RouteDAO();

    public RouteManagementPanel() {
        super("Route Management", "Define routes, stops, distance and timings",
                new String[]{"Route ID", "Route Name", "Start", "Destination", "Stops", "Distance (km)", "Est. Time (min)",
                        "Departure", "Status"},
                "Status", new String[]{"ACTIVE", "INACTIVE"});
    }

    @Override
    protected List<Object[]> fetchRows(String keyword, String filter) throws Exception {
        List<Object[]> rows = new ArrayList<>();
        for (Route r : dao.search(keyword, filter)) {
            rows.add(new Object[]{r.id(), r.routeName(), r.startPoint(), r.destination(), r.stops(),
                    r.distanceKm(), r.estTimeMin(), r.departureTime(), r.status()});
        }
        return rows;
    }

    @Override
    protected void addRecord() {
        new RouteFormDialog(owner(), null).setVisible(true);
        refresh();
    }

    @Override
    protected void editRecord(int id) throws Exception {
        Route r = dao.findById(id);
        if (r != null) {
            new RouteFormDialog(owner(), r).setVisible(true);
            refresh();
        }
    }

    @Override
    protected void deleteRecord(int id) throws Exception {
        dao.delete(id);
    }
}
