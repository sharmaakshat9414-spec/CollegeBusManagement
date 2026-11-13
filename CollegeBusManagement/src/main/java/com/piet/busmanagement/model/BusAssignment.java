package com.piet.busmanagement.model;

public record BusAssignment(int id, int busId, int driverId, int routeId, String busNumber,
                            String driverName, String routeName, String assignmentDate, String status) {
}
