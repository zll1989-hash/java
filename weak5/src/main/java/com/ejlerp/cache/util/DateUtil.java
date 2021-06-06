package com.ejlerp.cache.util;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * DateUtil
 *
 * @author Eric
 * @date 16/6/1
 */
public class DateUtil {
    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static Logger logger = LoggerFactory.getLogger(DateUtil.class);


    /**
     * 转换日期为字符串类型（pattern自行指定）
     *
     * @param date
     * @param pattern
     * @return
     */
    public static String date2String(Date date, String pattern) {
        try {
            DateTime dt = new DateTime(date.getTime());
            return dt.toString(pattern);
        } catch (Exception e) {
            logger.error("Failed to output as string, pattern is: " + pattern, e);
            return null;
        }
    }

}
