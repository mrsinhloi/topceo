package com.topceo.selections;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.androidnetworking.error.ANError;
import com.topceo.R;
import com.topceo.config.MyApplication;
import com.topceo.db.TinyDB;
import com.topceo.objects.other.User;
import com.topceo.selections.hashtags.HashtagCategory;
import com.topceo.services.Webservices;
import com.topceo.utils.DateFormat;
import com.topceo.utils.MyUtils;
import com.topceo.utils.ProgressUtils;

import java.util.Calendar;

import bolts.Continuation;
import bolts.Task;
import butterknife.BindView;
import butterknife.ButterKnife;

public class Fragment_4_Update_User_Info extends Fragment {

    public static Fragment_4_Update_User_Info newInstance(HashtagCategory category) {
        Fragment_4_Update_User_Info f = new Fragment_4_Update_User_Info();
        Bundle b = new Bundle();
        b.putParcelable(HashtagCategory.HASH_TAG_CATEGORY, category);
        f.setArguments(b);
        return f;
    }

    HashtagCategory category;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            category = getArguments().getParcelable(HashtagCategory.HASH_TAG_CATEGORY);
//            category.setCanSkip(false);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Nullable
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.btnNext)
    TextView btnNext;
    @BindView(R.id.btnPrevious)
    TextView btnPrevious;
    @BindView(R.id.title)
    TextView title;
    /*@BindView(R.id.subTitle)
    TextView subTitle;*/

    @BindView(R.id.linear1)
    LinearLayout linear1;
    @BindView(R.id.linear2)
    LinearLayout linear2;
    @BindView(R.id.linear3)
    LinearLayout linear3;
    @BindView(R.id.txt1)
    TextView txt1;
    @BindView(R.id.txt2)
    TextView txt2;
    @BindView(R.id.txt3)
    TextView txt3;


    private User user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_8_update_user_info, container, false);
        ButterKnife.bind(this, v);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        TinyDB db = new TinyDB(getContext());
        Object obj = db.getObject(User.USER, User.class);
        if (obj != null) {
            user = (User) obj;
        }
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTitles();
        initUI();
    }

    private void setTitles() {
        if (category != null) {
            title.setText(category.getCategoryName());

            boolean isCanSkip = category.isCanSkip();//chỉ hiển thị text, và click vào thì cho qua
            if (isCanSkip) {
                btnNext.setText(R.string.skip);
                setStateButtonNext(true);
            } else {
                btnNext.setText(R.string.next);
                setStateButtonNext(false);
            }

        }
    }


    private void setStateButtonNext(boolean enable) {
        if (enable) {
//            ViewCompat.setBackgroundTintList(btnNext, ContextCompat.getColorStateList(getContext(), android.R.color.black));
            btnNext.setBackgroundResource(R.drawable.bg_sky_radian_rounded);
            btnNext.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        } else {
//            ViewCompat.setBackgroundTintList(btnNext, ContextCompat.getColorStateList(getContext(), R.color.grey_300));
            btnNext.setBackgroundResource(R.drawable.bg_sky_disable_rounded);
            btnNext.setTextColor(ContextCompat.getColor(getContext(), R.color.grey_700));
        }
        btnNext.setEnabled(enable);
    }

    private void initUI() {

        //birthday
        initDate();

        if (!TextUtils.isEmpty(user.getBirthday())) {

            String day = MyUtils.convertDate(user.getBirthday(), DateFormat.DATE_FORMAT_EN, DateFormat.DATE_FORMAT_VN_DDMMYYYY);
            txt1.setText(day);
            String[] arr = day.split("-");
            if (arr != null && arr.length == 3) {
                mDay = Integer.parseInt(arr[0]);
                mMonth = Integer.parseInt(arr[1]) - 1;
                mYear = Integer.parseInt(arr[2]);
                whenHaveInfo();
                birthday = user.getBirthday();
            }
        }

        txt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog == null || dialog.isShowing() == false) {
                    oldString = txt1.getText().toString();

                    try {
                        dialog = new DatePickerDialog(
                                getContext(),
                                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                                mDateSetListener,
                                mYear, mMonth, mDay);
                        dialog.setCanceledOnTouchOutside(true);
                        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialogInterface) {
                                txt1.setText(oldString);
                                dialogInterface.dismiss();
                            }
                        });
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        initDate();
                    }
                }
            }
        });

        //gender
        int[] gendersValue = getResources().getIntArray(R.array.arr_gender_value);
        String[] genders = getResources().getStringArray(R.array.arr_gender);

        int gender = user.getGender();
        int position = -1;
        for (int i = 0; i < gendersValue.length; i++) {
            if (gendersValue[i] == gender) {
                position = i;
                break;
            }
        }
        if (position > -1) {
            genderSelected = gendersValue[position];
            String name = genders[position];
            txt2.setText(name);
            whenHaveInfo();
        }
        txt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(R.string.gender);
                builder.setItems(R.array.arr_gender, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        genderSelected = gendersValue[which];
                        String name = genders[which];
                        txt2.setText(name);

                        whenHaveInfo();
                    }
                });
                builder.setNegativeButton(R.string.cancel, null);
                builder.create().show();
            }
        });

        //marital status
        String[] keywords = getResources().getStringArray(R.array.arr_marital_status_keyword);
        String[] appears = getResources().getStringArray(R.array.arr_marital_status_appear);

        String keywordMaritalStatus = user.getMaritalStatus();
        int pos = -1;
        if(!TextUtils.isEmpty(keywordMaritalStatus)){
            for (int i = 0; i < keywords.length; i++) {
                if(keywords[i].equalsIgnoreCase(keywordMaritalStatus)){
                    pos = i;
                    break;
                }
            }
        }
        if(pos>-1){
            String name = appears[pos];
            txt3.setText(name);

            maritalStatus = keywords[pos];
            whenHaveInfo();
        }
        txt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(R.string.marital_status);
                builder.setItems(R.array.arr_marital_status_appear, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = appears[which];
                        txt3.setText(name);

                        maritalStatus = keywords[which];
                        whenHaveInfo();
                    }
                });
                builder.setNegativeButton(R.string.cancel, null);
                builder.create().show();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserProfile();
            }
        });

        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getContext() != null) {
                    getContext().sendBroadcast(new Intent(SelectFavoritesActivity.ACTION_PREVIOUS));
                }
            }
        });

        //TRANG DAU TIEN KO HIEN NUT BACK
        if (category.getOrder() == 1) {
            btnPrevious.setVisibility(View.GONE);
        } else {
            btnPrevious.setVisibility(View.VISIBLE);
        }

    }

    private void initDate() {
        final Calendar c = Calendar.getInstance();
        c.add(Calendar.YEAR, -18);
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
    }

    private int genderSelected = -1;
    private String maritalStatus = "";
    private String birthday = "";
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    private String oldString = "";
    private Dialog dialog = null;
    private int mYear, mMonth, mDay;
    private DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {

                public void onDateSet(DatePicker view, int year,
                                      int monthOfYear, int dayOfMonth) {
                    mYear = year;
                    mMonth = monthOfYear;
                    mDay = dayOfMonth;

                    String day = String.valueOf(mDay);
                    if (mDay < 10) {
                        day = "0" + day;
                    }
                    String month = String.valueOf(mMonth + 1);
                    if (mMonth + 1 < 10) {
                        month = "0" + month;
                    }

                    oldString = txt1.getText().toString();
                    txt1.setText(new StringBuilder()
                            // Month is 0 based so add 1
                            .append(day).append("/")
                            .append(month).append("/")
                            .append(mYear).append(""));

                    birthday = mYear + "-" + month + "-" + day;
                    whenHaveInfo();
                }
            };
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static final int NUMBER_INFO_REQUEST = 3;
    private int numberInfo = 0;

    private void whenHaveInfo() {
        numberInfo++;
        if(numberInfo>NUMBER_INFO_REQUEST){
            numberInfo=NUMBER_INFO_REQUEST;
        }

        //ko dc skip thi phai nhap du thong tin
        if (numberInfo == NUMBER_INFO_REQUEST) {
            btnNext.setText(R.string.next);
            setStateButtonNext(true);
        }
    }


    private void callNext() {
        if (getContext() != null) {
            getContext().sendBroadcast(new Intent(SelectFavoritesActivity.ACTION_NEXT));
        }
    }

    private void updateUserProfile() {
        if (MyUtils.checkInternetConnection(getContext())) {
            //neu co thong tin thi update
            if (numberInfo == NUMBER_INFO_REQUEST) {
                updateInfo();
            } else {
                //chua co thong tin thi ty vao canskip
                if (category.isCanSkip()) {
                    callNext();
                } else {
                    MyUtils.showAlertDialog(getContext(), R.string.please_input_info);
                }
            }
        } else {
            MyUtils.showThongBao(getContext());
        }

    }

    private void updateInfo() {
        /*final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getText(R.string.updating));
        progressDialog.show();*/
        ProgressUtils.show(getContext());
        Webservices.updateUserProfile2(birthday, genderSelected, maritalStatus).continueWith(new Continuation<Object, Void>() {
            @Override
            public Void then(Task<Object> task) throws Exception {
                if (task.getError() == null) {//ko co exception
                    boolean ok = (boolean) task.getResult();
                    if (ok) {
                        callNext();
                    }
                } else {
//                    if(!TextUtils.isEmpty(task.getError().getMessage().trim()))MyUtils.showToast(context, task.getError().getMessage());

                    ANError error = (ANError) task.getError();
                    boolean isLostCookie = MyApplication.controlException(error);
                    if (isLostCookie) {
                        MyApplication.initCookie(getContext()).continueWith(new Continuation<Object, Void>() {
                            @Override
                            public Void then(Task<Object> task) throws Exception {
                                if (task.getResult() != null) {
                                    User kq = (User) task.getResult();
                                    if (kq != null) {
                                        updateUserProfile();
                                    }
                                }
                                return null;
                            }
                        });
                    } else {
                        if (!TextUtils.isEmpty(error.getMessage())) {
                            MyUtils.showAlertDialog(getContext(), error.getMessage());
                        } else if (!TextUtils.isEmpty(error.getErrorBody())) {
                            MyUtils.showAlertDialog(getContext(), error.getErrorBody());
                        }
                    }
                }

                ProgressUtils.hide();
                return null;
            }
        });
    }

}
