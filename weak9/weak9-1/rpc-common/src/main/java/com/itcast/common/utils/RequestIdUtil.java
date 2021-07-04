package com.itcast.common.utils;

import java.util.UUID;

public class RequestIdUtil {

    public static String requestId() {
        return UUID.randomUUID().toString();
    }
}
