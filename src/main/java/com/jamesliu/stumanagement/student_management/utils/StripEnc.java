package com.jamesliu.stumanagement.student_management.utils;

public class StripEnc {
    public static String stripEnc(String value) {
        if (value != null && value.startsWith("ENC(") && value.endsWith(")")) {
            return value.substring(4, value.length() - 1);
        }
        return value;
    }
}
