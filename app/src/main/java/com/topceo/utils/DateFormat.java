package com.topceo.utils;

import android.content.Context;

import com.topceo.R;

import java.util.Calendar;
import java.util.Date;

public class DateFormat {


	public static final String DATE_FORMAT_EN="yyyy-MM-dd";
	public static final String DATE_FORMAT_SHORT="dd/MM/yy";
	public static final String DATE_FORMAT_LONG="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";//from server

	public static final String DATE_FORMAT_MILISECOND_AZURE="yyyyMMddHHmmss";
	public static final String DATE_FORMAT_HH_MM_AA ="hh:mm aa";
	public static final String DATE_REMINDER_FORMAT="yyyyMMdd";


	//////////////////////////////////////////////////////////////////////////////////////////////////
	public static final String DATE_FORMAT_SERVER ="yyyy-MM-dd HH:mm:ss";
	public static final String DATE_FORMAT_VN_DDMMYYYY="dd-MM-yyyy";
	public static final String DATE_FORMAT_VN_DDMMYYYY_HHMM="dd-MM-yyyy HH:mm";
	public static final String DATE_FORMAT_VN_DDMMYYYY_HHMMSS="dd-MM-yyyy HH:mm:ss";

	////////////////////////////////////////////////////////////////////////////////////////////////
	private static final int SECOND_MILLIS = 1000;
	private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
	private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
	private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

	public static Date currentDate() {
		Calendar calendar = Calendar.getInstance();
		return calendar.getTime();
	}

	public static String getTimeAgo(long time, Context context) {
		if (time < 1000000000000L) {
			// if timestamp given in seconds, convert to millis
			time *= 1000;
		}

		long now = currentDate().getTime();
		if (time > now || time <= 0) {
			return context.getString(R.string.in_the_future);
		}

		final long diff = now - time;
		if (diff < MINUTE_MILLIS) {
			return context.getString(R.string.moments_ago);
		} else if (diff < 2 * MINUTE_MILLIS) {
			return context.getString(R.string.a_minute_ago);
		} else if (diff < 50 * MINUTE_MILLIS) {
			return diff / MINUTE_MILLIS + " "+ context.getString(R.string.minutes_ago);
		} else if (diff < 90 * MINUTE_MILLIS) {
			return context.getString(R.string.an_hour_ago);
		} else if (diff < 24 * HOUR_MILLIS) {
			return diff / HOUR_MILLIS + " "+context.getString(R.string.hours_ago);
		} else if (diff < 48 * HOUR_MILLIS) {
			return context.getString(R.string.yesterday);
		} else {
			return diff / DAY_MILLIS + " " +context.getString(R.string.days_ago);
		}
	}



}
