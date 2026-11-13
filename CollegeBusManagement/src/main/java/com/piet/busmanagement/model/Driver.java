package com.piet.busmanagement.model;

public record Driver(int id, int userId, String name, String phone, String licenseNo, String address,
                     String email, String status, String busNumber) {
}
