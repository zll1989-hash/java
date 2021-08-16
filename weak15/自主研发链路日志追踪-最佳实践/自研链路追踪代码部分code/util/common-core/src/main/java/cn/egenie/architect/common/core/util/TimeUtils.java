package cn.egenie.architect.common.core.util;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

/**
 * @author lucien
 * @since 2021/01/05
 */
public class TimeUtils {

    public static final ZoneId ZONE_ID_CN = ZoneId.of("Asia/Shanghai");
    public static final LocalTime START_OF_DAY = LocalTime.of(0, 0, 0, 0);
    public static final LocalTime END_OF_DAY = LocalTime.of(23, 59, 59, 0);

    public static final String YYYYMMDDHHMMSSSSS = "yyyyMMddHHmmssSSS";
    public static final String YYYYMMDD = "yyyyMMdd";
    public static final String YYYYMM = "yyyyMM";
    public static final String DATE = "yyyy-MM-dd";

    public static final String MONTH = "yyyy-MM";
    public static final String DATE_HOUR = "yyyy-MM-dd HH";
    public static final String DATE_MINUTE = "yyyy-MM-dd HH:mm";
    public static final String DATE_TIME = "yyyy-MM-dd HH:mm:ss";

    public static final DateTimeFormatter YYYYMMDD_FORMATTER = DateTimeFormatter.ofPattern(YYYYMMDD).withZone(ZONE_ID_CN);
    public static final DateTimeFormatter YYYYMM_FORMATTER = DateTimeFormatter.ofPattern(YYYYMM).withZone(ZONE_ID_CN);
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE).withZone(ZONE_ID_CN);
    public static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern(MONTH).withZone(ZONE_ID_CN);
    public static final DateTimeFormatter DATE_HOUR_FORMATTER = DateTimeFormatter.ofPattern(DATE_HOUR).withZone(ZONE_ID_CN);
    public static final DateTimeFormatter DATE_MINUTE_FORMATTER = DateTimeFormatter.ofPattern(DATE_MINUTE).withZone(ZONE_ID_CN);
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME).withZone(ZONE_ID_CN);

    public static ZonedDateTime now() {
        return ZonedDateTime.now(ZONE_ID_CN);
    }

    public static long currentSecond() {
        return Instant.now().getEpochSecond();
    }

    public static long currentMilli() {
        return Instant.now().toEpochMilli();
    }

    public static String format(Date date, String format) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format).withZone(ZONE_ID_CN);
        return dateTimeFormatter.format(date.toInstant());
    }

    public static String format(Instant instant, String format) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format).withZone(ZONE_ID_CN);
        return dateTimeFormatter.format(instant);
    }

    public static String formatAsYYYYMMDD(Date date) {
        return YYYYMMDD_FORMATTER.format(date.toInstant());
    }

    public static String formatAsYYYYMMDD(Instant instant) {
        return YYYYMMDD_FORMATTER.format(instant);
    }

    public static String formatAsYYYYMM(Date date) {
        return YYYYMM_FORMATTER.format(date.toInstant());
    }

    public static String formatAsYYYYMM(Instant instant) {
        return YYYYMM_FORMATTER.format(instant);
    }

    public static String formatAsMonth(Date date) {
        return MONTH_FORMATTER.format(date.toInstant());
    }

    public static String formatAsMonth(Instant instant) {
        return MONTH_FORMATTER.format(instant);
    }

    public static String formatAsDate(Date date) {
        return DATE_FORMATTER.format(date.toInstant());
    }

    public static String formatAsDate(Instant instant) {
        return DATE_FORMATTER.format(instant);
    }

    public static String formatAsHour(Date date) {
        return DATE_HOUR_FORMATTER.format(date.toInstant());
    }

    public static String formatAsHour(Instant instant) {
        return DATE_HOUR_FORMATTER.format(instant);
    }

    public static String formatAsDateMinute(Date date) {
        return DATE_MINUTE_FORMATTER.format(date.toInstant());
    }

    public static String formatAsDateMinute(Instant instant) {
        return DATE_MINUTE_FORMATTER.format(instant);
    }

    public static String formatAsDateTime(Date date) {
        return DATE_TIME_FORMATTER.format(date.toInstant());
    }

    public static String formatAsDateTime(Instant instant) {
        return DATE_TIME_FORMATTER.format(instant);
    }

    public static ZonedDateTime ofDate(Date date) {
        return date.toInstant().atZone(ZONE_ID_CN);
    }

    public static ZonedDateTime ofMilli(long epochMilli) {
        return Instant.ofEpochMilli(epochMilli).atZone(ZONE_ID_CN);
    }

    public static ZonedDateTime ofSecond(long epochSecond) {
        return Instant.ofEpochSecond(epochSecond).atZone(ZONE_ID_CN);
    }

    public static Date toDate(ZonedDateTime zonedDateTime) {
        return Date.from(zonedDateTime.toInstant());
    }

    public static Date toDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZONE_ID_CN).toInstant());
    }

    public static Date parseAsDate(String str) {
        Assert.throwIfBlank(str, "Date must not be null");
        return Date.from(ZonedDateTime.parse(str, DATE_TIME_FORMATTER).toInstant());
    }

    public static Date parseAsDate(String str, String pattern) {
        Assert.throwIfTrue(StringUtils.isAnyBlank(str, pattern), "Date and Patterns must not be null");
        try {
            return DateUtils.parseDate(str, pattern);
        }
        catch (ParseException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static ZonedDateTime startOfMonth(ZonedDateTime dateTime) {
        return dateTime.with(TemporalAdjusters.firstDayOfMonth()).with(START_OF_DAY);
    }

    public static Date getMonthStartDate(Date date) {
        ZonedDateTime dateTime = ZonedDateTime.ofInstant(date.toInstant(), ZONE_ID_CN);
        Instant instant = dateTime.with(TemporalAdjusters.firstDayOfMonth()).with(START_OF_DAY).toInstant();
        return Date.from(instant);
    }

    public static Date getMonthEndDate(Date date) {
        ZonedDateTime dateTime = ZonedDateTime.ofInstant(date.toInstant(), ZONE_ID_CN);
        Instant instant = dateTime.with(TemporalAdjusters.lastDayOfMonth()).with(END_OF_DAY).toInstant();
        return Date.from(instant);
    }

    public static Date getLastMonthStartDate() {
        Instant instant = now().minusMonths(1).with(TemporalAdjusters.firstDayOfMonth()).with(START_OF_DAY).toInstant();
        return Date.from(instant);
    }

    public static Date getLastMonthEndDate() {
        Instant instant = now().minusMonths(1).with(TemporalAdjusters.lastDayOfMonth()).with(END_OF_DAY).toInstant();
        return Date.from(instant);
    }

    public static Date getDayStart(Date date) {
        ZonedDateTime dateTime = ZonedDateTime.ofInstant(date.toInstant(), ZONE_ID_CN);
        Instant instant = dateTime.with(START_OF_DAY).toInstant();
        return Date.from(instant);
    }

    public static Date getDayEnd(Date date) {
        ZonedDateTime dateTime = ZonedDateTime.ofInstant(date.toInstant(), ZONE_ID_CN);
        Instant instant = dateTime.with(END_OF_DAY).toInstant();
        return Date.from(instant);
    }

    /**
     * 本方法去掉了time部分，相当于对天做truncate
     * 例如start = 2020-09-04 23:59:59, end = 2020-09-05 00:00:00
     * 用本方法计算出来的结果是1
     */
    public static long daysBetween(Date start, Date end) {
        LocalDate startDate = ZonedDateTime.ofInstant(start.toInstant(), ZONE_ID_CN).toLocalDate();
        LocalDate endDate = ZonedDateTime.ofInstant(end.toInstant(), ZONE_ID_CN).toLocalDate();
        return ChronoUnit.DAYS.between(startDate, endDate);
    }

    /**
     * 支持string类型的month加法，例如输入为201912 + 5 = 202005
     */
    public static String plusMonths(String month, long monthsToAdd) {
        YearMonth yearMonth = YearMonth.parse(month, YYYYMM_FORMATTER);
        return yearMonth.plusMonths(monthsToAdd).format(YYYYMM_FORMATTER);
    }

    public static String minusMonths(String month, long monthsToSubtract) {
        YearMonth yearMonth = YearMonth.parse(month, YYYYMM_FORMATTER);
        return yearMonth.minusMonths(monthsToSubtract).format(YYYYMM_FORMATTER);
    }

    /**
     * 获取两个month之间的所有month，例如输入是201912和202005，
     * 返回值是[201912, 202001, 202002, 202003, 202004, 202005]
     */
    public static List<String> getMonthsBetween(String startMonth, String endMonth) {
        YearMonth startYearMonth = YearMonth.parse(startMonth, YYYYMM_FORMATTER);
        YearMonth endYearMonth = YearMonth.parse(endMonth, YYYYMM_FORMATTER);

        List<String> months = new ArrayList<>();
        YearMonth month = startYearMonth;
        while (!month.isAfter(endYearMonth)) {
            months.add(month.format(YYYYMM_FORMATTER));
            month = month.plusMonths(1);
        }

        return months;
    }

    public static int restDaysOfMonth(Date date) {
        LocalDate localDate = date.toInstant().atZone(ZONE_ID_CN).toLocalDate();
        int dayOfMonth = localDate.getDayOfMonth();
        int lengthOfMonth = localDate.lengthOfMonth();
        return lengthOfMonth - dayOfMonth + 1;
    }

    public static int lengthOfMonth(Date date) {
        return date.toInstant().atZone(ZONE_ID_CN).toLocalDate().lengthOfMonth();
    }
}
