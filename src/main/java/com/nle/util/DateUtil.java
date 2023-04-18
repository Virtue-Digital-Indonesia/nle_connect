package com.nle.util;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
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

    public static String getCancelExpiration(String pattern) {
        String dateTimePattern = pattern+"'T'HH:mm:ss";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateTimePattern);
        String cancelExpiration = formatter.format(LocalDateTime.now().plusSeconds(3));

        return cancelExpiration;
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

    public static String getDateOfPattern(String date){
        String pattern = "yyyy-MM-dd";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        OffsetDateTime parseDateTime = OffsetDateTime.parse(date);
        String getDate = formatter.format(parseDateTime);

        return getDate;
    }
}
