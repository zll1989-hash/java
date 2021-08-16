package cn.egenie.architect.common.core.util;

import java.util.UUID;

import cn.egenie.architect.common.core.constants.Constants;

/**
 * @author lucien
 * @since 2021/01/22
 */
public class UuidUtils {
    public static String genUuid() {
        return UUID.randomUUID()
                .toString()
                .replaceAll(Constants.MIDDLE_LINE, Constants.EMPTY_STRING)
                .toLowerCase();
    }
}
