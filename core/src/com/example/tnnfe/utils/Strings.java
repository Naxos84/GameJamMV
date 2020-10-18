package com.example.tnnfe.utils;

public final class Strings {

    private Strings() {
    }

    public static boolean isNullOrEmpty(String string) {

        if (string == null || string.length() == 0) {
            return true;
        }

        return false;
    }

    public static boolean isNotNullOrEmpty(String string) {

        if (string != null || string.length() > 0) {
            return true;
        }

        return false;
    }
}
