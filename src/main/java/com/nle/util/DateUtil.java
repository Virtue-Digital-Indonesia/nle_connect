package com.nle.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {

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
}
