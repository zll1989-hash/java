package cn.egenie.architect.common.core.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.commons.lang3.StringUtils;

/**
 * @author lucien
 * @since 2021/01/15
 */
public class MoneyUtils {
    private static final BigDecimal HUNDRED_DECIMAL = new BigDecimal("100");
    private static final BigDecimal WAN_DECIMAL = new BigDecimal("10000");

    public static long yuan2cent(double money) {
        return yuan2cent(String.valueOf(money));
    }


    public static long yuan2cent(String money) {
        if (StringUtils.isBlank(money)) {
            return 0L;
        }

        return new BigDecimal(money)
                .multiply(HUNDRED_DECIMAL)
                .setScale(0, RoundingMode.HALF_UP)
                .longValue();
    }

    public static Double cent2Yuan(double money) {
        return cent2Yuan(String.valueOf(money));
    }

    private static Double cent2Yuan(String money) {
        if (StringUtils.isBlank(money)) {
            return 0D;
        }
        return new BigDecimal(money)
                .divide(HUNDRED_DECIMAL, 2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    public static double divideWan(long money) {
        return new BigDecimal(String.valueOf(money))
                .divide(WAN_DECIMAL, 2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
