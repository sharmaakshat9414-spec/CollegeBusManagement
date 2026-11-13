package com.piet.busmanagement.ui.dialogs;

import com.piet.busmanagement.dao.RouteDAO;
import com.piet.busmanagement.model.Route;
import com.piet.busmanagement.ui.components.FormHelper;

import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.Window;

public class RouteFormDialog extends BaseFormDialog {

    private final RouteDAO dao = new RouteDAO();
    private final Route existing;
    private final JTextField name = FormHelper.textField("e.g. Route 6 - Tonk Road", null);
    private final JTextField start = FormHelper.textField("Starting point", null);
    private final JTextField destination = FormHelper.textField("Destination", null);
    private final JTextArea stops = new JTextArea(3, 20);
    private final JTextField distance = FormHelper.textField("Distance in km", null);
    private final JTextField time = FormHelper.textField("Minutes", null);
    private final JTextField departure = FormHelper.textField("HH:mm (24-hour), e.g. 07:15", null);
    private final JComboBox<String> status = FormHelper.combo("ACTIVE", "INACTIVE");

    public RouteFormDialog(Window owner, Route existing) {
        super(owner, existing == null ? "Add Route" : "Edit Route", "Separate stops with commas, in travel order");
        this.existing = existing;
        stops.setLineWrap(true);
        stops.setWrapStyleWord(true);
        addField("Route Name *", name);
        addField("Starting Point *", start);
        addField("Destination *", destination);
        addField("Stops *", new JScrollPane(stops));
        addField("Distance (km) *", distance);
        addField("Est. Time (min) *", time);
        addField("Departure Time *", departure);
        addField("Status *", status);
        if (existing != null) {
            name.setText(existing.routeName());
            start.setText(existing.startPoint());
            destination.setText(existing.destination());
            stops.setText(existing.stops());
            distance.setText(String.valueOf(existing.distanceKm()));
            time.setText(String.valueOf(existing.estTimeMin()));
            departure.setText(existing.departureTime());
            status.setSelectedItem(existing.status());
        }
        finish(owner);
    }

    @Override
    protected boolean onSave() throws Exception {
        if (FormHelper.isBlank(name.getText()) || FormHelper.isBlank(start.getText()) || FormHelper.isBlank(destination.getText())
                || FormHelper.isBlank(stops.getText()) || FormHelper.isBlank(distance.getText())
                || FormHelper.isBlank(time.getText()) || FormHelper.isBlank(departure.getText())) {
            FormHelper.warn(this, "Please fill all required fields.");
            return false;
        }
        double km;
        int minutes;
        try {
            km = Double.parseDouble(text(distance));
            minutes = Integer.parseInt(text(time));
        } catch (NumberFormatException e) {
            FormHelper.warn(this, "Distance and estimated time must be valid numbers.");
            return false;
        }
        if (km <= 0 || minutes <= 0) {
            FormHelper.warn(this, "Distance and estimated time must be greater than zero.");
            return false;
        }
        if (!text(departure).matches("^([01]\\d|2[0-3]):[0-5]\\d$")) {
            FormHelper.warn(this, "Departure time must be in HH:mm format, for example 07:15.");
            return false;
        }
        Route r = new Route(existing == null ? 0 : existing.id(), text(name), text(start), text(destination),
                text(stops), km, minutes, text(departure), (String) status.getSelectedItem());
        if (existing == null) {
            dao.add(r);
            FormHelper.info(this, "Route added successfully.");
        } else {
            dao.update(r);
            FormHelper.info(this, "Route updated successfully.");
        }
        return true;
    }
}
