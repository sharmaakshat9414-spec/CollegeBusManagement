package com.piet.busmanagement.model;

public record StudentBusAllocation(int id, int studentId, int busId, int routeId, String studentName,
                                   String regNo, String busNumber, String routeName, String pickupStop,
                                   String status) {
}
