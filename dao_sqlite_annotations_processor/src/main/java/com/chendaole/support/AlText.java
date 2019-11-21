package com.chendaole.support;

public class AlText {
    public static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }

    public static String captureName(String name) {
        name = name.substring(0, 1).toUpperCase() + name.substring(1);
        return  name;
    }
}
