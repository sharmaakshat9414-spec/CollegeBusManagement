package com.piet.busmanagement.model;

public record Route(int id, String routeName, String startPoint, String destination, String stops,
                    double distanceKm, int estTimeMin, String departureTime, String status) {
}
