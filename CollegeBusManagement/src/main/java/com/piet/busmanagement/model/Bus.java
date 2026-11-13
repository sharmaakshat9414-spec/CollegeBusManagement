package com.piet.busmanagement.model;

/** driverName / routeName come from the bus's active assignment (display only). */
public record Bus(int id, String busNumber, String registrationNo, String busType, int capacity,
                  String status, String driverName, String routeName) {
}
