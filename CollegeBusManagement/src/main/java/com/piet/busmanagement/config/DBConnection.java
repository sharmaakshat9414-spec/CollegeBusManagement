package com.piet.busmanagement.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Central place for the MySQL connection.
 * >>> Change USERNAME and PASSWORD below to match your MySQL installation. <<<
 */
public final class DBConnection {

    // ======================= EDIT THESE VALUES =======================
    public static final String HOST     = "localhost";
    public static final String PORT     = "3306";
    public static final String DATABASE = "college_bus_db";
    public static final String USERNAME = "root";
    public static final String PASSWORD = "Akshat@123";
    // =================================================================

    private static final String URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8";

    private DBConnection() {
    }

    /** Opens a new connection. Always use it inside try-with-resources. */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    /** Returns null when the connection works, otherwise the error message. */
    public static String testConnection() {
        try (Connection ignored = getConnection()) {
            return null;
        } catch (SQLException e) {
            return e.getMessage();
        }
    }
}
