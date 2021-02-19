package com.workchat.core.plan;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.workchat.core.chat.ChatActivity;
import com.workchat.core.chat.locations.MyLocation;
import com.workchat.core.chat.locations.SearchLocationActivity;
import com.workchat.core.database.TinyDB;
import com.workchat.core.models.chat.LocaleHelper;
import com.workchat.core.utils.MyUtils;
import com.workchat.core.utils.PermissionUtil;
import com.workchat.core.widgets.MyEditText;
import com.workchat.corechat.R;
import com.workchat.corechat.R2;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MH01_Create_Plan extends AppCompatActivity {
    private static final String TAG = "MH01_CREATE_PLAN";

    private Activity context = this;
    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    @BindView(R2.id.title)
    TextView title;
    private TinyDB db;


    @BindView(R2.id.txt1)
    MyEditText txt1;
    @BindView(R2.id.txt2)
    TextView txt2;
    @BindView(R2.id.txt3)
    TextView txt3;
    @BindView(R2.id.txt4)
    TextView txt4;
    @BindView(R2.id.txt5)
    MyEditText txt5;
    @BindView(R2.id.btnSend)
    Button btnSend;


    String languageSelected = LocaleHelper.LANGUAGE_TIENG_VIET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mh01_create_plan);
        ButterKnife.bind(this);
        db = new TinyDB(this);

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_green_500_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        languageSelected = db.getString(LocaleHelper.SELECTED_LANGUAGE, LocaleHelper.LANGUAGE_TIENG_VIET);


        //date time
        initDateTime();
        //duration
        setDurationString();
        txt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choseDuration();
            }
        });

        //send
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check ngay hop le
                Calendar cal = getCalendar();
                Calendar current = Calendar.getInstance();
                if (cal.getTimeInMillis() >= current.getTimeInMillis()) {//thoi gian phai o tuong lai
                    if (MyUtils.checkInternetConnection(context)) {
                        createSchedule();
                    } else {
                        MyUtils.showThongBao(context);
                    }
                } else {
                    MyUtils.showToast(context, R.string.invalid_date_and_hour);
                }

            }
        });

        txt4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPlace();
            }
        });

        //btn link google calendar
        initButtonLink();

    }

    //DATE TIME////////////////////////////////////////////////////////////////////////////////////
    private int day = 0, month = 0, year = 0, hour = 0, minute = 0;

    private void setDateTimeDefault() {

        Calendar cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);
        hour = cal.get(Calendar.HOUR_OF_DAY);
        minute = cal.get(Calendar.MINUTE);


    }

    private void initDateTime() {
        //Default
        setDateTimeDefault();


        //set thoi gian
        setTimeUI();

        txt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choseDateTime();
            }
        });

    }

    private Calendar getCalendar() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);

        return cal;
    }

    private long timestamp = SystemClock.elapsedRealtime();

    /**
     * Vừa set giao diện vừa kiểm tra thời gian set có hợp lệ không?
     * Nếu không hợp lệ không cho lưu
     *
     * @return
     */
    private void setTimeUI() {
        //set time UI
        Calendar cal = getCalendar();
        Calendar current = Calendar.getInstance();
        if (cal.getTimeInMillis() >= current.getTimeInMillis()) {
            String time = MyUtils.getTimePlan(cal, languageSelected);
            txt2.setText(time);
            timestamp = cal.getTimeInMillis() / 1000;//tinh theo giay
        } else {
            //ngay khong hop le thi khong luu
            //set ve default
            setDateTimeDefault();
            setTimeUI();
        }
    }


    private DatePicker datePicker;
    private TimePicker timePicker;

    /*private void choseDateTime() {

        boolean wrapInScrollView = true;
        MaterialDialog dialogCalendar;
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
                .title(R.string.date_and_hour)
                .titleGravity(GravityEnum.CENTER)
                .customView(R.layout.mh01_dialog_datetime_picker, wrapInScrollView)
                .positiveText(R.string.ok)
                .negativeText(R.string.cancel)
                .neutralText(R.string.delete)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        //date picker
                        year = datePicker.getYear();
                        month = datePicker.getMonth();
                        day = datePicker.getDayOfMonth();

                        //time picker
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            hour = timePicker.getHour();
                            minute = timePicker.getMinute();
                        } else {
                            hour = timePicker.getCurrentHour();
                            minute = timePicker.getCurrentMinute();
                        }

                        //set lai time
                        setTimeUI();

                        //dong
                        dialog.dismiss();
                    }
                })
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        //hien thi ngay mac dinh la hom nay
                        setDateTimeDefault();
                        setTimeUI();

                    }
                });

        dialogCalendar = builder.build();
        View v = dialogCalendar.getCustomView();

        //DATE
        datePicker = (DatePicker) v.findViewById(R.id.datePicker);
        datePicker.setMinDate(SystemClock.elapsedRealtime());
        datePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int ofYear, int monthOfYear, int dayOfMonth) {
                year = ofYear;
                month = monthOfYear;
                day = dayOfMonth;

            }
        });


        //TIME
        timePicker = (TimePicker) v.findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);
        //khoi tao time picker theo thoi gian da set
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timePicker.setHour(hour);
            timePicker.setMinute(minute);
        } else {
            timePicker.setCurrentHour(hour);
            timePicker.setCurrentMinute(minute);
        }
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                //khi dang chon thi ko lam gi, khi nhan ok thi moi set lai gia tri
            }
        });

        //SHOW CHON
        dialogCalendar.show();
    }*/

    private void choseDateTime() {

        View v = getLayoutInflater().inflate(R.layout.mh01_dialog_datetime_picker, null);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context)
                .setTitle(R.string.date_and_hour)
                .setView(v)
                .setNegativeButton(R.string.cancel, null)
                .setNeutralButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //hien thi ngay mac dinh la hom nay
                        setDateTimeDefault();
                        setTimeUI();
                    }
                })
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //date picker
                        year = datePicker.getYear();
                        month = datePicker.getMonth();
                        day = datePicker.getDayOfMonth();

                        //time picker
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            hour = timePicker.getHour();
                            minute = timePicker.getMinute();
                        } else {
                            hour = timePicker.getCurrentHour();
                            minute = timePicker.getCurrentMinute();
                        }

                        //set lai time
                        setTimeUI();

                        //dong
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();


        //DATE
        datePicker = (DatePicker) v.findViewById(R.id.datePicker);
        datePicker.setMinDate(SystemClock.elapsedRealtime());
        datePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int ofYear, int monthOfYear, int dayOfMonth) {
                year = ofYear;
                month = monthOfYear;
                day = dayOfMonth;

            }
        });


        //TIME
        timePicker = (TimePicker) v.findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);
        //khoi tao time picker theo thoi gian da set
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timePicker.setHour(hour);
            timePicker.setMinute(minute);
        } else {
            timePicker.setCurrentHour(hour);
            timePicker.setCurrentMinute(minute);
        }
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                //khi dang chon thi ko lam gi, khi nhan ok thi moi set lai gia tri
            }
        });

        //SHOW CHON
        dialog.show();
    }

    //DATE TIME////////////////////////////////////////////////////////////////////////////////////
    private NumberPicker nPicker1, nPicker2, nPicker3;
    private int number1 = 0, number2 = 0, number3 = 30;//mac dinh 30 phut
    private int minutes = number3;

    private void setDurationString() {
        minutes = 0;
        String duration = "";
        if (number1 > 0) {
            duration += number1 + getString(R.string.day) + " ";
            minutes += number1 * 24 * 60;
        }
        if (number2 > 0) {
            duration += number2 + getString(R.string.hour) + " ";
            minutes += number2 * 60;
        }
        if (number3 > 0) {
            duration += number3 + getString(R.string.minute);
            minutes += number3;
        }
        txt3.setText(duration);
    }

    private void choseDuration() {

        View v = getLayoutInflater().inflate(R.layout.mh01_dialog_number_picker, null);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context)
                .setTitle(R.string.duration)
                .setView(v)
                .setNeutralButton(R.string.cancel, null)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        number1 = nPicker1.getValue();
                        number2 = nPicker2.getValue();
                        number3 = nPicker3.getValue();

                        //set string
                        setDurationString();

                        //dong
                        dialog.dismiss();
                    }
                });


        AlertDialog dialog = builder.create();


        nPicker1 = (NumberPicker) v.findViewById(R.id.numberPicker1);
        nPicker2 = (NumberPicker) v.findViewById(R.id.numberPicker2);
        nPicker3 = (NumberPicker) v.findViewById(R.id.numberPicker3);

        nPicker1.setMinValue(0);
        nPicker1.setMaxValue(30);

        nPicker2.setMinValue(0);
        nPicker2.setMaxValue(23);

        nPicker3.setMinValue(0);
        nPicker3.setMaxValue(59);

        nPicker1.setValue(number1);
        nPicker2.setValue(number2);
        nPicker3.setValue(number3);

        //SHOW CHON
        dialog.show();
    }

    private void createSchedule() {
        if (MyUtils.checkInternetConnection(context)) {
            String title = txt1.getText().toString().trim();
            if (TextUtils.isEmpty(title)) {
                txt1.requestFocus();
                return;
            }

            String note = txt5.getText().toString().trim();

            PlanModelLocal plan = new PlanModelLocal(title, timestamp, minutes, place, note,
                    year, month, day, hour, minute);
            Intent intent = new Intent(ChatActivity.ACTION_SEND_PLAN);
            intent.putExtra(PlanModelLocal.PLAN_MODEL, plan);
            sendBroadcast(intent);


            finish();
        } else {
            MyUtils.showThongBao(context);
        }

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    private void selectPlace() {
        Intent intent = new Intent(this, SearchLocationActivity.class);
        startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        MyUtils.hideKeyboard(context);
    }

    com.workchat.core.plan.Place place;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                /*Bundle b = data.getExtras();
                if (b != null) {
                    place = b.getParcelable(Place.PLACE_MODEL);
                    if (place != null) {
                        txt4.setText(place.getAddress());
                        MyUtils.hideKeyboard(context, txt1);

                    }
                }*/

                Bundle b = data.getExtras();
                if (b != null) {
                    MyLocation location = b.getParcelable(MyLocation.MY_LOCATION);
                    if (location != null && location.getLat() > 0 && location.getLon() > 0) {
                        double lat = location.getLat();
                        double lon = location.getLon();
                        String address = location.getAddress();
                        place = new com.workchat.core.plan.Place(
                                String.valueOf(lat),
                                String.valueOf(lon), address);
                        txt4.setText(place.getAddress());
                        MyUtils.hideKeyboard(context, txt1);
                    }
                }
            }
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////
    @BindView(R2.id.btnLink)
    Button btnLink;
    @BindView(R2.id.linearLink)
    LinearLayout linearLink;

    private void initButtonLink() {
        btnLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Verify that all required contact permissions have been granted.
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR)
                        != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Contacts permissions have not been granted.
                    Log.i(TAG, "Contact permissions has NOT been granted. Requesting permissions.");
                    requestCalendarPermission();

                } else {

                    // Contact permissions have been granted. Show the contacts fragment.
                    Log.i(TAG, "Contact permissions have already been granted. Displaying contact details.");
                    linearLink.setVisibility(View.GONE);

                }
            }
        });

        //neu da gan quyen thi hide linearLink
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {


        } else {
            // Contact permissions have been granted. Show the contacts fragment.
            Log.i(TAG, "Contact permissions have already been granted. Displaying contact details.");
            linearLink.setVisibility(View.GONE);

        }


    }

    private static final int REQUEST_CALENDAR = 1;
    private static String[] PERMISSIONS_CALENDAR = {
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR
    };

    private void requestCalendarPermission() {
        // BEGIN_INCLUDE(contacts_permission_request)
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_CALENDAR)
                || ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_CALENDAR)) {

            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example, if the request has been denied previously.
            Log.i(TAG,
                    "Displaying contacts permission rationale to provide additional context.");

            // Display a SnackBar with an explanation and a button to trigger the request.
            Snackbar.make(linearLink, R.string.link_with_google_calendar,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(context, PERMISSIONS_CALENDAR, REQUEST_CALENDAR);
                        }
                    })
                    .show();
        } else {
            // Contact permissions have not been granted yet. Request them directly.
            ActivityCompat.requestPermissions(context, PERMISSIONS_CALENDAR, REQUEST_CALENDAR);
        }
        // END_INCLUDE(contacts_permission_request)
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CALENDAR) {
            if (PermissionUtil.verifyPermissions(grantResults)) {
                // All required permissions have been granted, display contacts fragment.
                /*Snackbar.make(linearLink, R.string.permision_available_contacts,
                        Snackbar.LENGTH_SHORT)
                        .show();*/
            } else {
                Log.i(TAG, "Contacts permissions were NOT granted.");
                /*Snackbar.make(mLayout, R.string.permissions_not_granted,
                        Snackbar.LENGTH_SHORT)
                        .show();*/
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////


}
