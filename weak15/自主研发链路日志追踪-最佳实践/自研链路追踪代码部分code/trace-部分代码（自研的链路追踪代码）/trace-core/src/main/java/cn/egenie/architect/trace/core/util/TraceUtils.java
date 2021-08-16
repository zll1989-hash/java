package cn.egenie.architect.trace.core.util;

import java.util.UUID;

/**
 * @author lucien
 * @since 2021/08/06 2021/01/22
 */
public class TraceUtils {

    public static String getSimpleName(String name) {
        String[] names = name.split("\\.");
        int length = names.length;
        if (length > 2) {
            return names[length - 2] + "." + names[length - 1];
        }
        else {
            return name;
        }
    }

    public static String buildTraceId() {
        return UUID.randomUUID()
                .toString()
                .replace("-", "")
                .toLowerCase();
    }
}
