package com.jhs.http;

public enum HttpMethod {
    GET,HEAD;
    public static final int MAX_LENGTH;
    static {
        int maxLength = -1;
        for (HttpMethod method : HttpMethod.values()) {
            if (method.name().length() > maxLength) {
                maxLength = method.name().length();
            }
        }
        MAX_LENGTH = maxLength;
    }
}
