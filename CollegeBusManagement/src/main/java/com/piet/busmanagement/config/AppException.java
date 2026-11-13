package com.piet.busmanagement.config;

/** A business-rule problem whose message is safe to show directly to the user. */
public class AppException extends Exception {
    public AppException(String message) {
        super(message);
    }
}
