package com.piet.busmanagement.model;

/** busNumber / routeName / createdOn are display-only values filled by queries. */
public record Student(int id, int userId, String fullName, String regNo, String email, String phone,
                      String section, String status, String busNumber, String routeName, String createdOn) {
}
