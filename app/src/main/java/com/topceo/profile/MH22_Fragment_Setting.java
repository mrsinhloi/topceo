package com.topceo.profile;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;

import com.topceo.BuildConfig;
import com.topceo.R;
import com.topceo.activity.MH01_MainActivity;
import com.topceo.activity.MH08_SuggestActivity;
import com.topceo.activity.MH13_InsightActivity;
import com.topceo.activity.MH14_TermsOfServiceActivity;
import com.topceo.activity.MH16_ReferalActivity;
import com.topceo.config.MyApplication;
import com.topceo.db.TinyDB;
import com.topceo.language.LocalizationUtil;
import com.topceo.login.MH15_SigninActivity;
import com.topceo.mediaplayer.audio.PlayerService;
import com.topceo.objects.other.User;
import com.topceo.selections.SelectFavoritesActivity;
import com.topceo.services.Webservices;
import com.topceo.shopping.PaymentActivity;
import com.topceo.utils.MyUtils;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.onesignal.OneSignal;

import org.michaelbel.bottomsheet.BottomSheet;

import bolts.Continuation;
import bolts.Task;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link MH22_Fragment_Setting#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MH22_Fragment_Setting extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_TITLE = "title_fragment";
    private String fragment_title;


    public MH22_Fragment_Setting() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param title Parameter 1.
     * @return A new instance of fragment Temp_ParentFragment.
     */
    public static MH22_Fragment_Setting newInstance(String title) {
        MH22_Fragment_Setting fragment = new MH22_Fragment_Setting();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }


    private TinyDB db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            fragment_title = getArguments().getString(ARG_TITLE);
        }
        db = new TinyDB(getContext());
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    /*@BindView(R.id.imageView1)
    ImageView img1;
    @BindView(R.id.textView1)
    AppCompatTextView txt1;
    @BindView(R.id.textView2)
    AppCompatTextView txt2;*/
    @BindView(R.id.textView4)
    AppCompatTextView txt4;

    @BindView(R.id.linear1)
    LinearLayout linear1;
    @BindView(R.id.linear2)
    LinearLayout linear2;
    /*@BindView(R.id.linear3)
    LinearLayout linear3;
    @BindView(R.id.linear4)
    LinearLayout linear4;*/
    @BindView(R.id.linear5)
    LinearLayout linear5;
    @BindView(R.id.linear6)
    LinearLayout linear6;
    @BindView(R.id.linear7)
    LinearLayout linear7;
    @BindView(R.id.linear8)
    LinearLayout linear8;
    @BindView(R.id.linear81)
    LinearLayout linear81;

    @BindView(R.id.linear9)
    LinearLayout linear9;

    @BindView(R.id.linear10)
    LinearLayout linear10;

    @BindView(R.id.linear12)
    LinearLayout linear12;
    @BindView(R.id.linear22)
    LinearLayout linear22;



    @BindView(R.id.txtLanguage)
    TextView txtLanguage;

//    @BindView(R.id.relativeUser)
//    RelativeLayout relativeUser;

    @BindView(R.id.vipLinear)
    LinearLayout vipLinear;
    /*@BindView(R.id.vipSeparate)
    View vipSeparate;*/

    @BindView(R.id.linear11)
    LinearLayout linear11;
    @BindView(R.id.switch11)
    SwitchMaterial switch1;

    private void controlAdmin() {
        if (user != null) {
            if (user.getRoleId() == User.ADMIN_ROLE_ID) {
                linear11.setVisibility(View.VISIBLE);

                boolean checked = db.getBoolean(User.IS_SHOW_ADMIN_PAGE, false);
                switch1.setChecked(checked);
                switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        db.putBoolean(User.IS_SHOW_ADMIN_PAGE, isChecked);
                        //tao lai page home
                        requestRestartApp(R.string.restart_app_desc_for_admin_screen, false);
                    }
                });
            } else {
                linear11.setVisibility(View.GONE);
            }
        }
    }

    private User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_setting, container, false);
        ButterKnife.bind(this, v);


        //init
        registerReceiver();
        ///////////////////////////////
        loadUserInfo();


        ///Info
        //copyright
        String copyright = getString(R.string.copyright, BuildConfig.VERSION_NAME);
        txt4.setText(copyright);

        linear1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MH08_SuggestActivity.class);
                startActivity(intent);
            }
        });

        linear2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), MH20_UserEditProfileActivity.class));
            }
        });
        /*relativeUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linear2.performClick();
            }
        });
        txt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linear2.performClick();
            }
        });
        txt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linear2.performClick();
            }
        });*/
        /*linear3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MH09_FollowersActivity.class);
                startActivity(intent);
            }
        });
        linear4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MH10_FollowingsActivity.class);
                startActivity(intent);
            }
        });*/

        //logout
        linear5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyUtils.checkInternetConnection(getContext())) {

                    new AlertDialog.Builder(getContext())
                            .setMessage(R.string.confirm_logout)
                            .setCancelable(true)
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();

                                    //tat nhac
                                    getContext().stopService(new Intent(getContext(), PlayerService.class));

                                    //xoa token chatcore
                                    MyApplication.removeOneSignalUserIdAndCloseSocketWhenLogout();

                                    //logout thi xoa token sns, tranh tinh trang tu goi notify cho chinh minh
                                    removeTokenMTP();


                                    MyApplication.whenLogout();

                                    db.putObject(User.USER, null);
                                    db.putBoolean(TinyDB.IS_LOGINED, false);
                                    //showcase logout ko can chay lai
                                    db.putBoolean(TinyDB.IS_SHOWCASE, false);
                                    db.clear();

                                    startActivity(new Intent(getContext(), MH15_SigninActivity.class));


                                    //xoa tat ca notification
                                    // Clear all notification
                                    OneSignal.clearOneSignalNotifications();
                                    OneSignal.setSubscription(false);
                                    NotificationManager nMgr = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                                    nMgr.cancelAll();


                                    //xoa db
                                    Realm realm = Realm.getDefaultInstance();
                                    realm.beginTransaction();
                                    realm.deleteAll();
                                    realm.commitTransaction();
                                    realm.close();


                                    getContext().sendBroadcast(new Intent(MH01_MainActivity.ACTION_FINISH));
                                    getContext().sendBroadcast(new Intent(MH22_SettingActivity.ACTION_FINISH));


                                    MyUtils.writeListUserFollowingToFile(null);


                                }
                            })
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();


                } else {
                    MyUtils.showThongBao(getContext());
                }
            }
        });

        linear6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), MH14_TermsOfServiceActivity.class));
            }
        });

        linear7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //vao man hinh cau hinh 10 thuoc tinh notify
                startActivity(new Intent(getContext(), MH21_NotifySettingActivity.class));
            }
        });

        linear8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), MH13_InsightActivity.class).putExtra(MH13_InsightActivity.IS_INSIGHT, true));
            }
        });
        linear81.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), MH13_InsightActivity.class).putExtra(MH13_InsightActivity.IS_INSIGHT, false));
            }
        });


        linear9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //vao man hinh suggest
                startActivity(new Intent(getContext(), SelectFavoritesActivity.class));
            }
        });

        linear12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //tao lai page home
                requestRestartApp(R.string.restart_app_for_showcase, true);
            }
        });

        vipLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(context);
                dialog.setMessage(R.string.update_to_vip_confirm);
                dialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        gotoPayment();
                    }
                });
                dialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                android.app.AlertDialog alertDialog = dialog.create();
                alertDialog.show();

                Button btn = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                btn.setTextColor(Color.BLACK);
            }
        });

        linear22.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, MH16_ReferalActivity.class));
            }
        });

        controlAdmin();

        initBottomSheet();


        return v;
    }

    private void gotoPayment() {
        Intent intent = new Intent(context, PaymentActivity.class);
//        intent.putExtra(Media.MEDIA_ID, mediaId);//ko truyen la mua vip
        context.startActivity(intent);
    }

    private void loadUserInfo() {
        if (db != null) {
            Object obj = db.getObject(User.USER, User.class);
            if (obj != null) {
                user = (User) obj;
                if (user != null) {
                    //load avatar
                    /*loadAvatar(user.getAvatarMedium());

                    //set ten, sdt
                    String name = user.getFullName();
                    if (TextUtils.isEmpty(name)) {
                        name = user.getUserName();
                    } else {
                        name += " (" + user.getUserName() + ")";
                    }
                    txt1.setText(name);
                    txt2.setText(user.getPhone());
                    txt2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MyUtils.copy(getContext(), user.getPhone());
                        }
                    });*/

                    if (user.isVip() || user.getUserId() == User.ADMIN_ROLE_ID) {
                        vipLinear.setVisibility(View.GONE);
//                        vipSeparate.setVisibility(View.GONE);
                    } else {
                        vipLinear.setVisibility(View.VISIBLE);
//                        vipSeparate.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }

    private void loadAvatar(String avatar) {
        /*if (!TextUtils.isEmpty(avatar)) {
            //load photo
            int size = getResources().getDimensionPixelSize(R.dimen.avatar_size_64);
            if (img1 != null) {
                Glide.with(context)
                        .load(avatar)
                        .override(size, size)
                        .transform(new GlideCircleTransform(context))
                        .placeholder(R.drawable.ic_no_avatar)
                        .into(img1);


            }
        }*/
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.context = null;
    }


    private void removeTokenMTP() {

        Webservices.deleteUserEndpoint(getContext()).continueWith(new Continuation<Object, Void>() {
            @Override
            public Void then(Task<Object> task) throws Exception {
                if (task.getError() == null) {
                    if (task.getResult() != null) {
                        boolean isSuccess = (boolean) task.getResult();
                        if (isSuccess) {
                            MyUtils.showToastDebug(getContext(), "Xóa token thành công");
                        }
                    }
                }
                return null;
            }
        });
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static final String ACTION_REFRESH_USER_INFO = "ACTION_REFRESH_USER_INFO_" + MH22_Fragment_Setting.class.getSimpleName();
    private BroadcastReceiver receiver;

    private void registerReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction().equalsIgnoreCase(ACTION_REFRESH_USER_INFO)) {
                    loadUserInfo();
                }

            }
        };
        getContext().registerReceiver(receiver, new IntentFilter(ACTION_REFRESH_USER_INFO));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            if (getContext() != null) {
                getContext().unregisterReceiver(receiver);
            }
        }
    }

    private Context context;
    int position = 0;

    private void initBottomSheet() {

        //language
        //hien thi ngon ngu dang chon
        position = 0;
        boolean isVietnamese = LocalizationUtil.INSTANCE.isVietnamese(context);
        if (isVietnamese) {
            txtLanguage.setText(R.string.vietnamese);
            position = 0;
        } else {
            txtLanguage.setText(R.string.english);
            position = 1;
        }

        linear10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null && !getActivity().isFinishing()) {
                    BottomSheet.Builder buider = new BottomSheet.Builder(getActivity());
                    buider.setTitle(getString(R.string.change_language));
                    buider.setMenu(R.menu.menu_action_change_language, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            int newPosition = which;
                            if (newPosition != position && newPosition != 2) {

                                requestRestartApp(R.string.restart_app_desc_for_language, false);

                                position = newPosition;
                                //luu lai ngon ngu moi
                                switch (newPosition) {
                                    case 0:
                                        db.putString(LocalizationUtil.SELECTED_LANGUAGE_KEY, LocalizationUtil.LANGUAGE_VI);
                                        MyApplication.initLanguage(context);
                                        break;

                                    case 1:
                                        db.putString(LocalizationUtil.SELECTED_LANGUAGE_KEY, LocalizationUtil.LANGUAGE_EN);
                                        MyApplication.initLanguage(context);
                                        break;
                                }


                            }

                            dialog.dismiss();
                        }
                    }).show();
                }
            }
        });

    }


    private void requestRestartApp(int message, boolean isCanCancel) {
        //yc khoi dong lai ung dung
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setTitle(R.string.restart_app);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                if (isCanCancel) {
                    //showcase
                    db.putBoolean(TinyDB.IS_SHOWCASE, true);
                }

                if (context != null) {
                    context.sendBroadcast(new Intent(MH01_MainActivity.ACTION_RESTART_APP));
                    if (getActivity() != null) {
                        getActivity().finish();
                    }
                }
            }
        });
        if (isCanCancel) {
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
        }

        android.app.AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        alertDialog.show();

        Button btn = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        btn.setTextColor(Color.BLACK);
    }

}

