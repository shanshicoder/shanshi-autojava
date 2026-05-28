package com.shanshi.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    public static final String YYYY_MM_DD = "yyyy_MM_dd";
    public static final String YYYY_MM_DD_OBLIQUE = "yyyy/MM/dd";
    public static final String YYYY_MM_DD_LINE = "yyyy-MM-dd";
    public static final String DATE_TIME_F = "yyyy-MM-dd HH:mm:ss";

    public static String formatDate(Date date, String pattern){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(date);
    }

    public static Date parseDate(String date, String pattern){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        try {
            return simpleDateFormat.parse(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

    }
}
