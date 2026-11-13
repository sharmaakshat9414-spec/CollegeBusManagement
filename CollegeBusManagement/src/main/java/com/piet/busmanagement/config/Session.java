package com.piet.busmanagement.config;

import com.piet.busmanagement.model.User;

/** Holds the currently logged-in user. */
public final class Session {

    private static User currentUser;

    private Session() {
    }

    public static void start(User user) {
        currentUser = user;
    }

    public static User getUser() {
        return currentUser;
    }

    public static void clear() {
        currentUser = null;
    }
}
