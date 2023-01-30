package com.nle.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    public static String getNowString(String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        String now = formatter.format(LocalDateTime.now());
        return now;
    }

    public static String getTomorrowString(String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        String now = formatter.format(LocalDateTime.now().plusDays(1));
        return now;
    }

    public static String getYesterdayString(String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        String yesterday = formatter.format(LocalDateTime.now().minusDays(1));
        return yesterday;
    }

    public static LocalDateTime convertLocalDateWithTimeZone(LocalDateTime oldDateTime, String timeZone){
        if (oldDateTime == null)
            return null;

        if (timeZone == null || timeZone.equals(""))
            timeZone = "GMT+7";

        LocalDateTime newDateTime = oldDateTime.atZone(ZoneId.of("UTC")).
                withZoneSameInstant(ZoneId.of(timeZone)).toLocalDateTime();

        return newDateTime;
    }
}
