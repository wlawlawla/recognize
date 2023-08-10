package com.recognize.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;

import static java.time.temporal.ChronoField.*;
import static java.time.temporal.ChronoField.NANO_OF_SECOND;


@Slf4j
public class DateUtils {

    public static final DateTimeFormatter ISO_DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;
    public static final DateTimeFormatter ISO_DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter NUMBER_DATE_FORMAT = DateTimeFormatter.BASIC_ISO_DATE;

    public static final DateTimeFormatter NUMBER_DATE_TIME_FORMAT;
    static {
        NUMBER_DATE_TIME_FORMAT = new DateTimeFormatterBuilder()
                .append(NUMBER_DATE_FORMAT)
                .appendValue(HOUR_OF_DAY, 2)
                .appendValue(MINUTE_OF_HOUR, 2)
                .appendValue(SECOND_OF_MINUTE, 2)
                .appendFraction(NANO_OF_SECOND, 0, 9, false).toFormatter();
    }

    public static LocalDate getISODate(String str) {
        if (str == null) {
            return null;
        }
        return LocalDate.parse(str, ISO_DATE_FORMAT);
    }

    public static LocalDateTime getISODateTime(String str) {
        return LocalDateTime.parse(str, ISO_DATETIME_FORMAT);
    }

    public static String getISODateStr(LocalDate date) {
        return ISO_DATE_FORMAT.format(date);
    }

    public static String getISODateStr(LocalDateTime dateTime) {
        return DateTimeFormatter.ISO_DATE_TIME.format(dateTime);
    }

    public static String getISODateStr(long dateTime) {
        LocalDate localDate = LocalDateTime.ofEpochSecond(dateTime / 1000, 0, ZoneOffset.UTC).toLocalDate();
        return getISODateStr(localDate);
    }

    /**
     * 格式化时间
     *
     * @param localDateTime 时间
     * @return 格式化后的时间
     */
    public static String getDateTimeStr(LocalDateTime localDateTime) {
        return ISO_DATETIME_FORMAT.format(localDateTime);
    }

    /**
     * @param date yyyy-MM-dd
     * @return Date yyyy年MM月dd日
     */
    public static String formateDate(LocalDate date) {
        if (date == null) {
            return "";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
        String dateStr = date.format(formatter);
        return dateStr;
    }

    /**
     * @param date yyyy-MM-dd HH24:mm:ss
     * @return Date yyyy年MM月dd日 HH24:mm:ss
     */
    public static String formateDateStr(LocalDateTime date) {
        if (date == null) {
            return "";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss");
        String dateStr = date.format(formatter);
        return dateStr;
    }

    public static LocalDate getISODateFromNumber(long date) {
        return LocalDate.parse(String.valueOf(date), NUMBER_DATE_FORMAT);
    }

    public static LocalDate getISODateFromNumber(String str) {
        if (StringUtils.isBlank(str)) {
            return null;
        }
        return LocalDate.parse(str, NUMBER_DATE_FORMAT);
    }

    public static String getNumberStrDate(LocalDate date) {
        return NUMBER_DATE_FORMAT.format(date);
    }

    public static String getISODateStrFromNumber(long date) {
        return getISODateStr(getISODateFromNumber(date));
    }

    @SuppressWarnings("deprecation")
    public static long getNumberDate(LocalDate date) {
        return Long.parseLong(NUMBER_DATE_FORMAT.format(date));
    }


    /**
     * 获取数字日期的当年第一天
     *
     * @param time
     * @return
     */
    public static long getFirstDayOfNumber(long time) {
        String str = String.valueOf(time);
        return Long.parseLong(str.substring(0, 4) + "0101");
    }

    /**
     * 传入一个日期 返回一个Long 类型的 20150301
     *
     * @param date
     * @return
     */
    public static Long getNumYearDate(LocalDate date) {
        String da = date.toString().substring(0, 4);
        return Long.parseLong(da);
    }

    /**
     * 将日期格式化为yyyy-MM-dd
     *
     * @param date 格式：yyyyMMdd, yyyy-MM-dd, yyyy/MM/dd, yyyy年MM月dd日
     * @return String
     */
    public static String getDateStr(String date) {
        if (StringUtils.isBlank(date)) {
            return null;
        } else {
            // remove all white spaces, including white spaces in the middle
            date = date.replaceAll("\\s+","");
            // in order to support format such as yyyy-MM-dd, yyyy/MM/dd, yyyy年MM月dd日, and also make single digit month
            // and day into two digits, which is MM, DD format.
            String[] strings = date.split("\\D");
            if(strings.length==3){
                for(int i = 0; i< strings.length; i++){
                    if(strings[i].length()==1){
                        strings[i]= "0"+ strings[i];
                    }
                }
                date = String.join("", strings);
            }
        }
        if (date.length() == 4) {
            return date;
        }
        if (date.length() > 4 && date.length() <= 6) {
            return date.substring(0, 4) + "-" + date.substring(4, 7);
        }
        if (date.length() > 6 && date.length() <= 8) {
            return date.substring(0, 4) + "-" + date.substring(4, 6) + "-" + date.substring(6, 8);
        }
        return null;
    }

    public static String getSeasonFromDate(LocalDate date) {
        String season = null;
        switch (date.getMonth()) {
            case JANUARY:
            case FEBRUARY:
            case MARCH:
                season = "spring";
                break;
            case APRIL:
            case MAY:
            case JUNE:
                season = "summer";
                break;
            case JULY:
            case AUGUST:
            case SEPTEMBER:
                season = "autumn";
                break;
            case OCTOBER:
            case NOVEMBER:
            case DECEMBER:
                season = "winter";
                break;
            default:
        }
        return season;
    }


    /**
     * @param date   (formatter:yyyy-MM-dd)
     * @param hour
     * @param minute
     * @param second
     * @return
     */
    public static LocalDateTime setHourMinuteSecond(LocalDate date, int hour, int minute, int second) {
        if (date == null) {
            return null;
        }
        LocalTime localTime = LocalTime.of(hour, minute, second);
        return LocalDateTime.of(date, localTime);
    }

    public static LocalDateTime getLocalDateTime(Object object){
        if (object == null){
            return null;
        }
        try {
/*            if (object instanceof oracle.sql.TIMESTAMP){
                return ((TIMESTAMP)object).timestampValue().toLocalDateTime();
            } else */
             if(object instanceof Timestamp){
                return ((Timestamp)object).toLocalDateTime();
            } else if (object instanceof LocalDate){
                return ((LocalDate) object).atStartOfDay();
            } else if (object instanceof LocalDateTime){
                return (LocalDateTime) object;
            } else {
                String timeStr = object + "";
                if (StringUtils.isBlank(timeStr)){
                    return null;
                }
                if (timeStr.contains(":")){
                    //yyyy-MM-dd HH:mm:ss
                    return getISODateTime(timeStr);
                }else {
                    //先格式化成yyyy-mm-dd 再转成localDateTime
                    LocalDate localDate = getISODate(getDateStr(timeStr));

                    return localDate != null ? localDate.atStartOfDay() : null;
                }
            }
        } catch (Exception e) {
            return null;
        }
    }
}
