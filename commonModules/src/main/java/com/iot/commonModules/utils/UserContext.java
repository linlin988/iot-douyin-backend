package com.iot.commonModules.utils;

public class UserContext {
    private static final ThreadLocal<Long> userContext = new ThreadLocal<>();

    public static void setUser(Long userId) {
        userContext.set(userId);
    }
    public static Long getUser() {
        return userContext.get();
    }
    public static void removeUser() {
        userContext.remove();
    }
}
