package com.workchat.core.plan;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.text.TextUtils;

import androidx.core.app.ActivityCompat;

import com.workchat.core.utils.MyUtils;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class CalendarManager {
    private Context context;

    public CalendarManager(Context ctx) {
        context = ctx;
    }

    private long getEventId(String chatLogId) {
        long eventId = 0;
        if (!TextUtils.isEmpty(chatLogId)) {
            String logIdOnlyNumber = chatLogId.replaceAll("[^0-9]", "");
            try {
                eventId = Long.parseLong(logIdOnlyNumber);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return eventId;
    }

    public void createEvent(String chatLogId, PlanModelLocal plan) {
        //lay chat log id la mot chuoi string, extract tat ca cac so gom lai lam event_id
        if (!TextUtils.isEmpty(chatLogId) && plan != null) {
            long eventId = getEventId(chatLogId);
            if (eventId > 0) {
                //Vì sau khi xoá event, trạng thái db của Calendar chưa update ngay,
                // nên khi check tồn tại vẫn còn, phải có khoảng thời gian timeout, nên cứ gọi tạo mới
                //kiem tra neu khong ton tai thi moi them moi
//                boolean isExists = isEventInCalendar(context, eventId);
//                if(!isExists){
                //add vao calendar
                boolean success = createEvent(plan, eventId);
//                    Toast.makeText(context, "create event "+success, Toast.LENGTH_SHORT).show();
//                }

            }

        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private boolean createEvent(PlanModelLocal plan, long chatLogId) {
        boolean created = false;
        if (plan != null) {

            //phai co quyen tao lich
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }

            int calendarId = getCalendarPrimaryId();
            long startMillis = 0;
            long endMillis = 0;
            Calendar beginTime = getCalendar(plan);
            startMillis = beginTime.getTimeInMillis();

            Calendar endTime = getCalendar(plan);
            endTime.add(Calendar.MINUTE, plan.getDuration());
            endMillis = endTime.getTimeInMillis();


            ContentResolver cr = context.getContentResolver();
            ContentValues values = new ContentValues();
            values.put(CalendarContract.Events.CALENDAR_ID, calendarId);
            values.put(CalendarContract.Events._ID, chatLogId);
            values.put(CalendarContract.Events.DTSTART, startMillis);
            values.put(CalendarContract.Events.DTEND, endMillis);
            values.put(CalendarContract.Events.TITLE, plan.getTitle());
            values.put(CalendarContract.Events.DESCRIPTION, plan.getNote());
            values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());

            Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

            // get the event ID that is the last element in the Uri
//            long eventID = Long.parseLong(uri.getLastPathSegment());
//            MyUtils.log("hello");
            created = true;
        }

        return created;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private MyCalendar[] getCalendar(Context c) {
        MyCalendar[] cals = null;

        String projection[] = {"_id", "calendar_displayName"};
        Uri calendars;
        calendars = Uri.parse("content://com.android.calendar/calendars");

        ContentResolver contentResolver = c.getContentResolver();
        Cursor managedCursor = contentResolver.query(calendars, projection, "", null, null);

        if (managedCursor.moveToFirst()) {
            cals = new MyCalendar[managedCursor.getCount()];

            String calName;
            int calID;
            int cont = 0;
            int nameCol = managedCursor.getColumnIndex(projection[1]);
            int idCol = managedCursor.getColumnIndex(projection[0]);
            do {
                calName = managedCursor.getString(nameCol);
                calID = managedCursor.getInt(idCol);
                cals[cont] = new MyCalendar(calName, calID);
                cont++;
            } while (managedCursor.moveToNext());
            managedCursor.close();
        }
        return cals;

    }

    private int getCalendarPrimaryId() {
        int id = 1;
        MyCalendar[] cals = getCalendar(context);
        if (cals != null) {
            for (int i = 0; i < cals.length; i++) {
                MyCalendar cal = cals[i];
                if (MyUtils.isEmailValid(cal.getCalName())) {
                    id = cal.getCalID();
                    break;
                }
            }
        }
        return id;
    }

    private Calendar getCalendar(PlanModelLocal plan) {
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        if (plan != null) {
            cal.set(Calendar.YEAR, plan.getYear());
            cal.set(Calendar.MONTH, plan.getMonth());
            cal.set(Calendar.DAY_OF_MONTH, plan.getDay());
            cal.set(Calendar.HOUR_OF_DAY, plan.getHour());
            cal.set(Calendar.MINUTE, plan.getMinute());
        }

        return cal;
    }

    public boolean deleteEvent(String chatLogId) {
        //phai co quyen
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        if (!TextUtils.isEmpty(chatLogId)) {
            long eventId = getEventId(chatLogId);
            if (eventId > 0) {

                boolean isExists = isEventInCalendar(context, eventId);
                if (isExists) {
                    //kiem tra neu co ton tai event thi xoa
                    ContentResolver cr = context.getContentResolver();
                    Uri deleteUri = null;
                    deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId);
                    int rows = cr.delete(deleteUri, null, null);
                    return rows > 0;
                }
            }
        }
        return false;
    }

    public boolean isEventInCalendar(Context context, long eventId) {

        Cursor cursor = context.getContentResolver().query(
                Uri.parse("content://com.android.calendar/events"),
                new String[]{"_id"}, " _id = ? ",
                new String[]{String.valueOf(eventId)}, null);

        if (cursor.moveToFirst()) {
            //Yes Event Exist...
            return true;
        }
        return false;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
}
