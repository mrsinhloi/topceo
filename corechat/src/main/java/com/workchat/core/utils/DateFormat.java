package com.workchat.core.utils;

import java.util.Calendar;
import java.util.Date;

public class DateFormat {

    public static final String DATE_FORMAT_VN = "dd/MM/yyyy";
    public static final String DATE_FORMAT_EN = "yyyy-MM-dd";
    public static final String DATE_FORMAT_SHORT = "dd/MM/yy";
    public static final String DATE_FORMAT_LONG = "yyyy-MM-dd'T'HH:mm:ss";//"2016-12-23T09:57:56.523Z"

    public static final String DATE_FORMAT_MILISECOND_AZURE = "yyyyMMddHHmmss";
    public static final String DATE_FORMAT_HH_MM_AA = "hh:mm aa";
    public static final String DATE_FORMAT_HH_MM_AA_AND_DATE = "hh:mm aa, dd/MM/yyyy";
    public static final String DATE_FORMAT_HH_MM = "HH:mm";
    public static final String DATE_REMINDER_FORMAT = "yyyyMMdd";


    //////////////////////////////////////////////////////////////////////////////////////////////////
    public static final String DATE_FORMAT_SERVER = "yyyy-MM-dd'T'HH:mm:ss'Z'";////from server 2018-08-07T04:28:16Z
    public static final String DATE_FORMAT_VN_DDMMYYYY = "dd-MM-yyyy";
    public static final String DATE_FORMAT_VN_DDMMYYYY_HHMM = "dd-MM-yyyy HH:mm";
    public static final String DATE_FORMAT_VN_DDMMYYYY_HHMMSS = "dd-MM-yyyy HH:mm:ss";
    public static final String DATE_FORMAT_SHORT_MMM = "dd MMM";
    public static final String DATE_PLAN = "HH:mm, EEEE, dd MMM yyyy";
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public static Date currentDate() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime();
    }

}
