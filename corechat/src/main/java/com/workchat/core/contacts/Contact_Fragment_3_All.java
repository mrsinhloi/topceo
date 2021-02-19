package com.workchat.core.contacts;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.havrylyuk.alphabetrecyclerview.AlphabeticalDialog;
import com.havrylyuk.alphabetrecyclerview.OnHeaderClickListener;
import com.havrylyuk.alphabetrecyclerview.OnLetterClickListener;
import com.workchat.core.channel.MH03_ChannelActivity;
import com.workchat.core.channel.UserCheckInfo;
import com.workchat.core.chat.ChatNhanh_Fragment_Bottom;
import com.workchat.core.chat.SearchUserChat2Activity;
import com.workchat.core.chat.SearchUserChatFromRestApiActivity;
import com.workchat.core.chat.receiver.NetworkChangeReceiver;
import com.workchat.core.config.ChatApplication;
import com.workchat.core.contacts.alphabet_recycler.LetterSelector;
import com.workchat.core.contacts.alphabet_recycler.RecyclerSectionItemDecoration;
import com.workchat.core.database.TinyDB;
import com.workchat.core.event.EventContact;
import com.workchat.core.models.chat.RoomLog;
import com.workchat.core.models.realm.UserInfo;
import com.workchat.core.utils.DateFormat;
import com.workchat.core.utils.MyUtils;
import com.workchat.core.utils.PhoneUtils;
import com.workchat.corechat.R;
import com.workchat.corechat.R2;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import io.socket.client.Ack;
import io.socket.client.Socket;

public class Contact_Fragment_3_All extends Fragment implements OnHeaderClickListener, OnLetterClickListener {

    public Contact_Fragment_3_All() {
    }

    boolean isForward = false;
    boolean isSharing =false;

    public static String SHOW_HEADER = "HIDE_SYNC_BAR";

    public static Contact_Fragment_3_All newInstance(boolean isForward, boolean isSharing, boolean isShowHeader) {
        Contact_Fragment_3_All fragment = new Contact_Fragment_3_All();
        Bundle args = new Bundle();
        args.putBoolean(RoomLog.IS_FORWARD, isForward);
        args.putBoolean(RoomLog.IS_SHARING, isSharing);
        args.putBoolean(SHOW_HEADER, isShowHeader);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isForward = getArguments().getBoolean(RoomLog.IS_FORWARD);
            isSharing = getArguments().getBoolean(RoomLog.IS_SHARING);
            isShowHeader = getArguments().getBoolean(SHOW_HEADER);
        }
    }


    private Context context;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }


    private boolean isShowHeader = false;

    public void showHeader(boolean isShowHeader) {
        this.isShowHeader = isShowHeader;
    }

    private boolean isCanSearch = false;

    public void setCanSearch(boolean canSearch) {
        isCanSearch = canSearch;
    }

    @BindView(R2.id.recyclerView)
    RecyclerView rv;
    @BindView(R2.id.linear1)
    LinearLayout linear1;
    @BindView(R2.id.linear2)
    LinearLayout linear2;
    @BindView(R2.id.txtReading)
    TextView txtReading;
    @BindView(R2.id.imgDongBo)
    ImageView imgDongBo;
    @BindView(R2.id.txtEmpty)
    TextView txtEmpty;
    @BindView(R2.id.linearHeader)
    LinearLayout linearHeader;
    @BindView(R2.id.progressBar)
    ProgressBar progressBar;

    private void showProgressBar() {
//        if(progressBar!=null)progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        if (progressBar != null && progressBar.getVisibility() == View.VISIBLE)
            progressBar.setVisibility(View.GONE);
    }

    private TinyDB db;
    Realm realm;
    ///////////////////////////////////////////////////////////////////////////////////////

    private String ownerId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        isSocketConnected();
//        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.contact_fragment_3, container, false);
        ButterKnife.bind(this, v);

        if (linearHeader != null) {
            linearHeader.setVisibility(isShowHeader ? View.VISIBLE : View.GONE);
        }

        db = new TinyDB(context);
        token = ChatApplication.Companion.getTokenUser();
        realm = ChatApplication.Companion.getRealmChat();
        hideProgressBar();
        txtEmpty.setVisibility(View.GONE);
        try {
            if(ChatApplication.Companion.getUser()!=null) {
                ownerId = ChatApplication.Companion.getUser().get_id();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        /////////////////////////////////////////////////////////////////////
        //read contact
        isHaveListSave = db.getBoolean(UserInfo.IS_SAVE_LIST_USER_INFO, false);
        ////////////////////////////////////////////////////////
        registerReceiver();

        linear1.setOnClickListener(view -> {
            Intent intent = new Intent(context, SearchUserChatFromRestApiActivity.class);
            startActivity(intent);
        });
        linear2.setOnClickListener(view -> {
            Intent intent = new Intent(context, MH03_ChannelActivity.class);
            startActivity(intent);
        });

        imgDongBo.setOnClickListener(v1 -> {
            if (MyUtils.checkInternetConnection(context)) {
//                    db.putBoolean(UserInfo.IS_SAVE_LIST_USER_INFO, false);
                isHaveListSave = false;

                //y/c quyen va doc contacts
                numberShow = 0;
                requestPermission();
            } else {
                MyUtils.showThongBao(context);
            }
        });
        /*txtReading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgDongBo.performClick();
            }
        });*/


        //an tinh nang
        linear1.setVisibility(View.GONE);
        linear2.setVisibility(View.GONE);

        //set text init
        setTextInitReading();

        //vua vao doc len tu db, khi nao nhan nut dong bo thi moi yc quyen vao dong bo
        readAndSetAdapter();


        return v;
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////
    public static final String ACTION_REMOVE_CONTACT = "ACTION_REMOVE_CONTACT";
    public static final String ACTION_INVITE_NUMBER_PHONE = "ACTION_INVITE_NUMBER_PHONE";
    public static final String ACTION_SEARCH_ACCOUNT_LOCAL_1 = "ACTION_SEARCH_ACCOUNT_LOCAL_1";
    public static final String ACTION_GO_TO_TOP_CONTACT_TAB_1 = "ACTION_GO_TO_TOP_CONTACT_TAB_1";
    public static final String ACTION_GET_CONTACT_LIST_MBN = "ACTION_GET_CONTACT_LIST_MBN";

    BroadcastReceiver receiver;

    private void registerReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle b = intent.getExtras();
                if (intent.getAction().equalsIgnoreCase(ACTION_REMOVE_CONTACT)) {
                    if (b != null) {
                        UserInfo info = b.getParcelable(UserInfo.USER_INFO);
                        removeContact(info.get_id());
                    }
                } else if (intent.getAction().equalsIgnoreCase(ACTION_INVITE_NUMBER_PHONE)) {
                    if (b != null) {
                        UserInfo info = b.getParcelable(UserInfo.USER_INFO);
                        inviteBySMS(info);
                    }
                } else if (intent.getAction().equalsIgnoreCase(ACTION_SEARCH_ACCOUNT_LOCAL_1)) {
                    if (b != null) {
                        String keyword = b.getString(UserInfo.KEY_SEARCH);
                        search(keyword);
                    }
                } else if (intent.getAction().equalsIgnoreCase(ACTION_GO_TO_TOP_CONTACT_TAB_1)) {
                    gotoTop();
                } else if (intent.getAction().equalsIgnoreCase(ACTION_GET_CONTACT_LIST_MBN)) {
                    step3GetContactListAndShowAdapter();
                } else if (intent.getAction().equals(NetworkChangeReceiver.ACTION_CONNECTIVITY_CHANGE)) {
                    if (!isInitialStickyBroadcast()) {
                        //neu co ket noi internet lai va danh sach trang thi load lai
                        if (MyUtils.checkInternetConnection(context)) {
                            if (adapter == null || adapter.getItemCount() == 0) {
                                isFirst = true;
                                readAndSetAdapter();
                            }
                        }
                    }
                }

            }
        };
        context.registerReceiver(receiver, new IntentFilter(ACTION_REMOVE_CONTACT));
        context.registerReceiver(receiver, new IntentFilter(ACTION_INVITE_NUMBER_PHONE));
        context.registerReceiver(receiver, new IntentFilter(ACTION_SEARCH_ACCOUNT_LOCAL_1));
        context.registerReceiver(receiver, new IntentFilter(ACTION_GO_TO_TOP_CONTACT_TAB_1));
        context.registerReceiver(receiver, new IntentFilter(ACTION_GET_CONTACT_LIST_MBN));
        getContext().registerReceiver(receiver, new IntentFilter(NetworkChangeReceiver.ACTION_CONNECTIVITY_CHANGE));
    }

    private void gotoTop() {
        new Handler().postDelayed(() -> {
            rv.scrollToPosition(0);
            if (getContext() != null) {
                getContext().sendBroadcast(new Intent(ChatNhanh_Fragment_Bottom.ACTION_EXPAND_TOOLBAR));
            }
        }, 200);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            context.unregisterReceiver(receiver);
        }
        /*if (realm != null && !realm.isClosed()) {
            realm.close();
        }*/

        if (readingTask != null && readingTask.getStatus() != AsyncTask.Status.FINISHED) {
            readingTask.cancel(true);
        }
    }

    private Socket socket;

    private boolean isSocketConnected() {
        socket = ChatApplication.Companion.getSocket();
        return socket != null && socket.connected();
    }

    @Override
    public void onResume() {
        super.onResume();
        socket = ChatApplication.Companion.getSocket();
    }

    private ArrayList<UserInfo> listUsers = new ArrayList<>();

    ///////////////////////////////////////////////////////////////////////////////////////
    private void removeContact(final String userId) {
        if (socket != null && socket.connected()) {
            JSONObject obj = new JSONObject();
            try {
                ArrayList<String> ids = new ArrayList<>();
                ids.add(userId);

                String listString = new Gson().toJson(
                        ids,
                        new TypeToken<ArrayList<String>>() {
                        }.getType());
                JSONArray array = new JSONArray(listString);

                obj.put("contacts", array);
            } catch (Exception e) {
                e.printStackTrace();
            }

            socket.emit("removeContact", obj, (Ack) args -> {
                if (getActivity() != null && !getActivity().isFinishing())
                    getActivity().runOnUiThread(() -> {
                        try {
                            JSONObject obj1 = new JSONObject(args[0].toString());
                            int code = obj1.getInt("errorCode");
                            if (code == 0) {
                                //xoa thanh cong
                                MyUtils.showToast(context, R.string.delete_success);
                                //remove khoi adapter
                                adapter.removeItem(userId);

                            } else {
                                String message = obj1.getString("error");
                                MyUtils.showAlertDialog(context, message);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });
            });
        }
    }

    void inviteBySMS(UserInfo userInfo) {
        if (userInfo != null) {
            MyUtils.inviteSms(context, userInfo.getPhone());
        }
    }

    int counter;

    boolean isHaveListSave = false;

    public void step1ReadLocalContact() {

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED) {
            return;
        }

        if (readingTask != null && readingTask.getStatus() != AsyncTask.Status.FINISHED) {
            readingTask.cancel(true);
        }
        readingTask = new ReadingTask();
        readingTask.execute();

    }

    private ReadingTask readingTask;

    long start = SystemClock.elapsedRealtime();

    class ReadingTask extends AsyncTask<Void, String, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            start = SystemClock.elapsedRealtime();
            if (!isHaveListSave) {
                //neu co internet thi moi hien dong bo
                if (MyUtils.checkInternetConnection(context)) {
                    txtReading.setText(R.string.synchronyzing);
                    txtReading.setVisibility(View.VISIBLE);
                    showProgressBar();
                }
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
//            txtReading.setText(values[0]);
            //doc dc 50 phan tu thi hien thi truoc, background van tiep tuc dong bo
            //hien thi danh ba dang co, neu lan dau tien chua co data thi moi hien danh sach tam nay
            if (adapter == null) {
                setAdapterAndLetters();
            }
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            listUsers = new ArrayList<UserInfo>();

            if (!isHaveListSave) {

                String phoneNumber = null;
                String email = null;

                Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
                String _ID = ContactsContract.Contacts._ID;
                String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
                String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

                Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
                String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

                UserInfo user;

                ContentResolver contentResolver = context.getContentResolver();
                String[] selectionArgs = null;
                String sortOrder = ContactsContract.Contacts.DISPLAY_NAME;
                Cursor cursor = contentResolver.query(CONTENT_URI, null, null, selectionArgs, sortOrder);

                // Iterate every contact in the phone
                if (cursor.getCount() > 0) {

                    counter = 0;
                    while (cursor.moveToNext()) {
                        user = new UserInfo();
                        counter++;

//                        String s = "Reading... ";//+ counter++ + "/" + cursor.getCount();

                        //doc dc 50 phan tu thi hien thi truoc, background van tiep tuc dong bo
                        if (counter == 50) {
                            publishProgress("");
                        }

                        String contact_id = cursor.getString(cursor.getColumnIndex(_ID));
                        String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));

                        //kiem tra ten rong truoc
                        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(name.trim())) {
                            int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));

                            //This is to read multiple phone numbers associated with the same contact
                            Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[]{contact_id}, null);
                            while (phoneCursor.moveToNext()) {
                                phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));

                                //ko rong va chieu dai >= 10
                                if (!TextUtils.isEmpty(phoneNumber) && phoneNumber.trim().length() > 9) {

                                    phoneNumber = MyUtils.getPhoneNumberOnly(phoneNumber);
                                    user.setName(name.trim());
                                    user.setPhone(phoneNumber.trim());
                                    break;
                                }

                            }
                            phoneCursor.close();

                            // Add the contact to the ArrayList, co ten va phone thi moi add vao
                            if (!TextUtils.isEmpty(user.getName()) &&
                                    !TextUtils.isEmpty(user.getPhone())) {

                                //phone = phone_national
                                String phoneNational = PhoneUtils.getNationalFormattedMobileNumber(user.getPhone(), PhoneUtils.DEFAULT_ISO_COUNTRY);
                                if (!TextUtils.isEmpty(phoneNational)) {
                                    user.setPhoneNational(phoneNational);
                                    user.setPhone(phoneNational);
                                }

                                //E164
                                String phoneInternaltionHavePlus = PhoneUtils.getE164FormattedMobileNumber(user.getPhone(), PhoneUtils.DEFAULT_ISO_COUNTRY);
                                if (!TextUtils.isEmpty(phoneInternaltionHavePlus)) {
                                    user.setPhoneE164(phoneInternaltionHavePlus);
                                }

                                user.setEmail("");
                                user.setLocalContact(true);
                                listUsers.add(user);

                            }
                        }
                    }
                }
                return true;
            } else {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {//doc tu danh ba len thanh cong
                if (listUsers != null && listUsers.size() > 0) {//CO DANH BA
                    MyUtils.howLong(start, "read contact step 1 read local " + listUsers.size());

                    //step1: doc danh sach local vao listUsers
                    //step2: goi danh sach local len server, tra ve danh sach matching theo phone, dong bo 2 list nay
                    //step3: lay danh sach danh ba chat, dong bo 2 list nay
                    //step4: luu db va load len lai
                    step2CheckPhoneNumberFollowPage();
                    hideProgressBar();

                } else {//KHONG CO DANH BA
                    //lay danh ba tu server
                    step3GetContactListAndShowAdapter();
                    hideProgressBar();
                }
            } else {
                //lay danh ba tu server
//                step3GetContactListAndShowAdapter();
                //Da co list save, doc len thoi
                readAndSetAdapter();
                hideProgressBar();
            }


        }
    }

    private void readAndSetAdapter() {
        try {
            realm.executeTransaction(realm -> {
                RealmResults<UserInfo> list = realm.where(UserInfo.class).sort("name").findAll();
                ArrayList<UserInfo> temp = new ArrayList<>(realm.copyFromRealm(list));
                if (temp.size() > 0) {
                    //Logger.d("temp.size() 0");
                    listUsers = new ArrayList<>(temp);
                    setAdapterAndLetters();


                    //CHUAN BI DU LIEU SEARCH
                    if (adapter != null) {
                        adapter.beginSearch();
                    }

                    //BAO CHO FRAGMENT SEARCH ALL
                    EventBus.getDefault().post(new EventContact(listUsers, listUsers.size()));
                } else {
                    //Logger.d("temp.size() 1");
                    if (isFirst) {
                        isFirst = false;
//                        imgDongBo.performClick();
                        step3GetContactListAndShowAdapter();
                    } else {
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }
            });

        } catch (Exception e) {
            db.putBoolean(UserInfo.IS_SAVE_LIST_USER_INFO, true);
            isHaveListSave = true;
            e.printStackTrace();
        }
    }

    boolean isFirst = true;

    ///////////////////////////////////////////////////////////////////////////////////////////////
    private void setAdapterAndLetters() {

        initRecyclerView();

        try {
            //tao lai letters
            TreeSet<Character> treeSet = (TreeSet<Character>) adapter.getHeadersLetters();
            Character[] letters = treeSet.toArray(new Character[treeSet.size()]);
            String[] arr = new String[letters.length];
            for (int i = 0; i < letters.length; i++) {
                arr[i] = letters[i] + "";
            }
            mLetterSelector.setArrayLetter(arr);
            mLetterSelector.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        hideProgressBar();
    }

    @BindView(R2.id.letterSelector)
    LetterSelector mLetterSelector;
    @BindView(R2.id.tipView)
    TextView mTipView;
    private android.os.Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            mTipView.setVisibility(View.GONE);
        }
    };

    private Contact_Fragment_3_All_Adapter adapter;
    private boolean isSetDecoration = false;

    private void setDecoration() {
        if (!isSetDecoration) {
            RecyclerSectionItemDecoration sectionItemDecoration =
                    new RecyclerSectionItemDecoration(getResources().getDimensionPixelSize(R.dimen.recycler_section_header_height),
                            true,
                            getSectionCallback(adapter.getItems()));
            if (rv.getItemDecorationCount() > 0) {
                rv.removeItemDecorationAt(0);
            }
            rv.addItemDecoration(sectionItemDecoration);
            isSetDecoration = true;
        }
    }

    private void initRecyclerView() {

        /*DividerItemDecoration divider = new DividerItemDecoration(
                rv.getContext(),
                DividerItemDecoration.VERTICAL
        );
        if (getContext() != null) {
            divider.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(getContext(), R.drawable.line_separate_rv)));
        }
        rv.addItemDecoration(divider);*/

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        rv.setLayoutManager(mLayoutManager);
        if (listUsers != null) {

            MyUtils.sortList(listUsers);
            adapter = new Contact_Fragment_3_All_Adapter(listUsers, getActivity());
            adapter.setIsForward(isForward);
            adapter.setSharing(isSharing);
            isSetDecoration = false;
            setDecoration();
            rv.setAdapter(adapter);

            //khoi tao an di, sau khi co danh sach letter thi moi hien ra
            mLetterSelector.setVisibility(View.GONE);
            mLetterSelector.setOnLetterChangedListener(s -> {
                mHandler.removeCallbacks(mRunnable);
                mTipView.setText(s);
                mTipView.setVisibility(View.VISIBLE);
                mHandler.postDelayed(mRunnable, 500);

                onLetterClick(s.charAt(0));
            });
        }
    }

    private RecyclerSectionItemDecoration.SectionCallback getSectionCallback(final List<UserInfo> people) {
        return new RecyclerSectionItemDecoration.SectionCallback() {
            @Override
            public boolean isSection(int position) {
                boolean isSection = false;
                boolean condition2 = false;
                if (position >= 0 && position - 1 >= 0 && position < people.size() && position - 1 < people.size()) {

                    char char1 = people.get(position).getName().charAt(0);
                    char1 = MyUtils.getUnsignedChar(char1);
                    char1 = Character.toUpperCase(char1);

                    char char2 = people.get(position - 1).getName().charAt(0);
                    char2 = MyUtils.getUnsignedChar(char2);
                    char2 = Character.toUpperCase(char2);


                    condition2 = char1 != char2;
                }
                if (position == 0 || condition2) {
                    isSection = true;
                }
                return isSection;
            }

            @Override
            public CharSequence getSectionHeader(int position) {
                if (people != null && position < people.size()) {
                    UserInfo item = people.get(position);
                    if (item.isValid() && item != null) {
                        char c = item.getName().charAt(0);
                        c = MyUtils.getUnsignedChar(c);
                        c = Character.toUpperCase(c);
                        return String.valueOf(c);
                    } else {
                        return "#";
                    }
                } else {
                    return "#";
                }
            }
        };
    }

    @Override
    public void onHeaderClick(View header, long headerId) {
        Character clickedHeaderLetter;
        TextView headerTextView = (TextView) header.findViewById(R.id.txtHeader);
        if (null != headerTextView && !TextUtils.isEmpty(headerTextView.getText())) {
            clickedHeaderLetter = headerTextView.getText().charAt(0);
            AlphabeticalDialog dialog = (AlphabeticalDialog) getChildFragmentManager()
                    .findFragmentByTag(AlphabeticalDialog.ALPHABETICAL_DIALOG_TAG);
            if (dialog == null) {
                dialog = AlphabeticalDialog.newInstance(clickedHeaderLetter,
                        (TreeSet<Character>) adapter.getHeadersLetters());
            }
            //set custom tiles background color
            dialog.setTilesColor(getResources().getColor(android.R.color.holo_green_dark));
            //set custom tiles text color
            dialog.setLettersColor(getResources().getColor(android.R.color.white));
            if (!dialog.isAdded()) {
                dialog.show(getChildFragmentManager().beginTransaction()
                        , AlphabeticalDialog.ALPHABETICAL_DIALOG_TAG);
            }
        }
    }

    @Override
    public void onLetterClick(Character letter) {
        if (adapter != null) {
            if ("A".equalsIgnoreCase(letter.toString())) {
                //scroll ve dau
                rv.scrollToPosition(0);
            } else {
                //2. Tìm position
                int position = adapter.getPosition(letter);
                if (position >= 0) {
                    rv.scrollToPosition(position);
                }
            }

        }
    }

    private String token = "";

    private void setTextInitReading() {
        //set gia tri ban dau
        String date = MyUtils.getCurrentDate(DateFormat.DATE_FORMAT_VN_DDMMYYYY_HHMMSS);
        String d = db.getString(TinyDB.DATE_SYNC_LASTEST);
        if (!TextUtils.isEmpty(d)) {
            date = d;
        }
        String txt = getString(R.string.synchrony_lastest, date);
        txtReading.setText(txt);
    }

    private void setTextLastSync() {
        //luu lai time cuoi
        String date = MyUtils.getCurrentDate(DateFormat.DATE_FORMAT_VN_DDMMYYYY_HHMMSS);
        //moi dong bo
        db.putString(TinyDB.DATE_SYNC_LASTEST, date);
        String txt = getString(R.string.synchrony_lastest, date);
        txtReading.setText(txt);
    }

    /**
     * Add những user có tài khoản mbn vào danh bạ chat, ai đã có trong danh bạ thì không truyền lên
     */
    /*private void addContact(ArrayList<UserCheckInfo> list) {
        if (socket != null && socket.connected()) {
            if (list != null) {
                JSONObject obj = new JSONObject();
                try {
                    ArrayList<String> ids = new ArrayList<>();

                    for (int i = 0; i < list.size(); i++) {
                        long id = list.get(i).getUserId();
//                        if (!TextUtils.isEmpty(id)) {
                            //da co trong danh ba thi khong add lai
                           *//* if (!idContacts.contains(id)) {
                                ids.add(id);
                            }*//*
//                        }
                    }

                    //khong co thi khong can add
                    if (ids.size() == 0) {
                        return;
                    }

                    String listString = new Gson().toJson(
                            ids,
                            new TypeToken<ArrayList<String>>() {
                            }.getType());
                    JSONArray array = new JSONArray(listString);

                    obj.put("contacts", array);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                socket.emit("addContact", obj, (Ack) args -> {
                    if (getActivity() != null && !getActivity().isFinishing())
                        getActivity().runOnUiThread(() -> {
                            try {
                                JSONObject obj1 = new JSONObject(args[0].toString());
                                int code = obj1.getInt("errorCode");
                                if (code == 0) {
                                    //xoa thanh cong
//                                        MyUtils.showToast(context, R.string.synchrony_success);

                                    //update adapter
                                *//*UserInfo info = UserInfo.parseUserInfo(context, args);
                                if(info!=null){
                                    info.setLocalContact(false);
                                    adapter.replaceItem(info);
                                }*//*

                                } else {
                                    String message = obj1.getString("error");
                                    MyUtils.showAlertDialog(context, message);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        });
                });
            }
        }
    }*/
    private void search(String query) {
        if (adapter != null && isCanSearch) {
            adapter.filter(query);

            isSetDecoration = false;
            setDecoration();
            rv.setAdapter(adapter);

            if (adapter.getItemCount() > 0) {
                txtEmpty.setVisibility(View.GONE);
            } else {
                txtEmpty.setVisibility(View.VISIBLE);
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    public void sortList() {
        //Sort the listServer if necessary
        if (null != listUsers && listUsers.size() > 0) {
            MyUtils.sortList(listUsers);
        }
    }


    private UserInfo isUserExist(UserInfo user) {
        if (user != null) {

            String email = user.getEmail();
            if (!TextUtils.isEmpty(email)) {
                realm.beginTransaction();
                UserInfo item = realm.where(UserInfo.class).equalTo("email", email).findFirst();
                realm.commitTransaction();
                if (item != null) {//3
                    user.setLocalId(item.getLocalId());
                    user.setLocalContact(false);
                    user.setName(item.getName());
                    return user;
                } else {
                    if (!TextUtils.isEmpty(user.getPhone())) {
                        String phoneNational = PhoneUtils.getNationalFormattedMobileNumber(user.getPhone(), PhoneUtils.DEFAULT_ISO_COUNTRY);
                        realm.beginTransaction();
                        item = realm.where(UserInfo.class).equalTo("phone", phoneNational).findFirst();
                        realm.commitTransaction();
                        if (item != null) {//1
                            user.setLocalId(item.getLocalId());
                            user.setLocalContact(true);
                            user.setName(item.getName());
                            return user;
                        } else {
                            //phone international
                            String phoneInternaltionHavePlus = PhoneUtils.getE164FormattedMobileNumber(user.getPhone(), PhoneUtils.DEFAULT_ISO_COUNTRY);
                            realm.beginTransaction();
                            item = realm.where(UserInfo.class).equalTo("phone", phoneInternaltionHavePlus).findFirst();
                            realm.commitTransaction();
                            if (item != null) {//2
                                user.setLocalId(item.getLocalId());
                                user.setLocalContact(true);
                                user.setName(item.getName());
                                return user;
                            }
                        }
                    }
                }
            }


        }
        return null;
    }

    private int findInListLocal(String phone) {
        int position = -1;
        if (listUsers.size() > 0) {
            for (int i = 0; i < listUsers.size(); i++) {
                UserInfo item = listUsers.get(i);
//                boolean equal = MyUtils.comparePhoneNumber(item.getPhone(), phone);
                String numberE164 = PhoneUtils.getE164FormattedMobileNumber(phone, PhoneUtils.DEFAULT_ISO_COUNTRY);
                if (!TextUtils.isEmpty(numberE164)) {
                    boolean equal = numberE164.equalsIgnoreCase(item.getPhoneE164());
                    if (equal) {
                        position = i;
                        break;
                    }
                }
            }
        }
        return position;
    }

    //CHECK PERMISSION
    /////////////////////////////////////////////////////////////////////////////////////////////////
    private static final int PERMISSION_REQUEST_CODE = 200;
    String[] arrPermissions = {
            Manifest.permission.READ_CONTACTS};

    private boolean checkPermission() {

        boolean result = true;
        for (int i = 0; i < arrPermissions.length; i++) {
            int grant = ContextCompat.checkSelfPermission(context, arrPermissions[i]);
            if (grant == PackageManager.PERMISSION_DENIED) {
                result = false;
                break;
            }
        }

        return result;
    }

    android.app.AlertDialog alertDialog;

    /**
     * Manifest.permission.ACCESS_COARSE_LOCATION,
     * Manifest.permission.ACCESS_FINE_LOCATION,
     */
    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean check = checkPermission();
            if (!check) {

                //load cai dang co san
                if (adapter == null || (adapter != null && adapter.getItemCount() == 0)) {
                    //Da co list save, doc len thoi
                    readAndSetAdapter();
                    hideProgressBar();
                }

                numberShow++;
                if (numberShow >= 2) {
                    if (alertDialog != null && alertDialog.isShowing()) {
                        alertDialog.dismiss();
                    }
                    android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(context);
                    alertDialogBuilder.setMessage(R.string.deny_permission_notify);
                    alertDialogBuilder.setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            arg0.dismiss();
                            MyUtils.goToSettings(context);
                        }
                    });

                    alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                } else {
                    requestPermissions(arrPermissions, PERMISSION_REQUEST_CODE);
                }

            } else {
                step1ReadLocalContact();
            }

        } else {
            step1ReadLocalContact();
        }
    }

    private int numberShow = 0;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                requestPermission();
                break;
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////
    //END CHECK PERMISSION
    ////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////
    private int page = 500;
    private int pageSum = 1;

    private void step2CheckPhoneNumberFollowPage() {
        start = SystemClock.elapsedRealtime();
        //neu danh sach co thi lam tiep, ko co thi qua buoc 3
        if (listUsers != null && listUsers.size() > 0) {
            pageSum = listUsers.size() / page;// 1200 => 2
            int phanDu = listUsers.size() % page;// 1200 => 200
            if (phanDu > 0) {
                pageSum++;
            }

            //goi len theo tung page
            checkPhoneNumber(1);

        } else {
            step3GetContactListAndShowAdapter();
        }
    }

    private int tryNumber = 0;
    private int tryNumberMax = 3;

    /**
     * Gởi từng page lên server: server lưu lại danh bạ mà không tạo ra user tạm,
     * đồng thời trả về danh sách user đã có tài khoản trong hệ thống luva
     *
     * @param pageNumber
     */
    private void checkPhoneNumber(final int pageNumber) {
        if (pageNumber <= pageSum) {
            if (isSocketConnected()) {
                if (listUsers != null && listUsers.size() > 0) {
                    showProgressBar();

                    //1*500-500=0, 2*500-500=500, 3*500-500=1000
                    int from = pageNumber * page - page;
                    //1*500 = 500, 2*500=1000, 3*500 = 1500
                    int to = pageNumber * page;
                    //==> 0-500, 500-1000, 1000-1500
                    //size = 1200, to = 1500 thi lay 1200
                    if (to > listUsers.size()) {
                        to = listUsers.size();
                    }

                    JSONArray array = new JSONArray();
                    for (int i = from; i < to; i++) {
                        UserInfo user = listUsers.get(i);
                        JSONObject item = new JSONObject();
                        try {
                            if (!TextUtils.isEmpty(user.getPhone())) {
                                item.put("phone", user.getPhone());
                                item.put("name", user.getName());
                                array.put(item);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                    JSONObject obj = new JSONObject();
                    try {
                        obj.put("token", token);
                        obj.put("contacts", array);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    socket.emit("checkPhoneNumber", obj, (Ack) args -> {
                        if (getActivity() != null && !getActivity().isFinishing())
                            getActivity().runOnUiThread(() -> {
                                try {
                                    JSONObject obj1 = new JSONObject(args[0].toString());
                                    int code = obj1.getInt("errorCode");
                                    if (code == 0) {

                                        //thu lai toi da 3 lan
                                        if (pageNumber == 1) {
                                            tryNumber = 0;
                                        }

                                        ArrayList<UserCheckInfo> list = UserCheckInfo.parse(context, args);

                                        //DONG BO DANH SACH TRUOC KHI HIEN LEN
                                        if (list != null && list.size() > 0) {
                                            for (int i = 0; i < listUsers.size(); i++) {
                                                UserInfo user = listUsers.get(i);

                                                //Chua sync thi moi sync
                                                if (!user.isSynContactSuccess()) {
                                                    for (int j = 0; j < list.size(); j++) {
                                                        UserCheckInfo check = list.get(j);
                                                        //so sanh sdt
//                                                    boolean equal = MyUtils.comparePhoneNumber(check.getPhone(), user.getPhone());
                                                        String numberE164 = PhoneUtils.getE164FormattedMobileNumber(check.getPhone(), PhoneUtils.DEFAULT_ISO_COUNTRY);
                                                        if (!TextUtils.isEmpty(numberE164)) {
                                                            boolean equal = numberE164.equalsIgnoreCase(user.getPhoneE164());
                                                            if (equal) {

                                                                //set lai thong tin cho listUsers
                                                                user.set_id(check.getUserId());
                                                                user.setNameMBN(check.getName());
                                                                user.setAvatar(check.getAvatar());
                                                                user.setEmail(check.getEmail());
                                                                user.setLocalContact(true);
                                                                user.setHaveMBNAccount(true);
                                                                user.setSynContactSuccess(true);

                                                                listUsers.set(i, user);
                                                                break;
                                                            }
                                                        }
                                                    }
                                                }

                                            }

                                        }

                                        //du co du lieu hay khong thi den page cuoi cung cung phai luu lai va hien thi len cho user
                                        //neu la page cuoi cung thi moi luu lai
                                        if (pageNumber != pageSum) {
                                            //tiep tuc goi
                                            checkPhoneNumber(pageNumber + 1);
                                        }

                                        //neu la page cuoi cung thi hien thi du lieu len
                                        if (pageNumber == pageSum) {

                                            //luu xuong db
                                            //luu lai de lan sau load len
                                            if (listUsers != null && listUsers.size() > 0) {
                                                // Get a Realm instance for this thread
                                                try {
                                                    //sort truoc de vao khong can sort lai
                                                    sortList();

                                                    //xoa de khong reference den cac item dang sap sua xoa
                                                    if (rv.getItemDecorationCount() > 0) {
                                                        rv.removeItemDecorationAt(0);
                                                    }

                                                    //luu moi thi xoa toan bo data truoc do
                                                    realm.beginTransaction();
                                                    realm.delete(UserInfo.class);
                                                    for (int i = 0; i < listUsers.size(); i++) {
                                                        // This will update an existing object with the same primary key
                                                        // or create a new object if an object with no primary key = 42
                                                        realm.copyToRealmOrUpdate(listUsers.get(i));
                                                    }
                                                    realm.commitTransaction();

                                                    //xem nhu dong bo xong, con list contact, moi lan vao lai se dong bo lai
                                                    db.putBoolean(UserInfo.IS_SAVE_LIST_USER_INFO, true);
                                                    isHaveListSave = true;

                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                            MyUtils.howLong(start, "read contact step 2");
                                            //lay danh sach danh ba chat, dong bo voi danh sach nay
                                            step3GetContactListAndShowAdapter();


                                        }
                                    } else {
                                        //xem nhu chua dong bo
//                                                db.putBoolean(UserInfo.IS_SAVE_LIST_USER_INFO, false);
                                        //lay lai page nay neu bi loi, thu lai toi da 3 lan
                                        tryNumber++;
                                        if (tryNumber <= tryNumberMax) {
                                            checkPhoneNumber(pageNumber);
                                        } else {
                                            //lay danh ba tu server
                                            step3GetContactListAndShowAdapter();
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    step3GetContactListAndShowAdapter();
                                }
                                hideProgressBar();
                            });
                    });
                } else {
                    step3GetContactListAndShowAdapter();
                }
            } else {
                step3GetContactListAndShowAdapter();
                hideProgressBar();
            }
        }
    }


    private void step3GetContactListAndShowAdapter() {
        start = SystemClock.elapsedRealtime();
        if (socket != null && socket.connected()) {

            JSONObject obj = new JSONObject();
            try {
                obj.put("pageIndex", 0);
                obj.put("itemPerPage", 0);//lay het

            } catch (Exception e) {
                e.printStackTrace();
            }

            socket.emit("getContactList", obj, (Ack) args -> {
                if (getActivity() != null && !getActivity().isFinishing())
                    getActivity().runOnUiThread(() -> {
                        try {
                            JSONObject obj1 = new JSONObject(args[0].toString());
                            int code = obj1.getInt("errorCode");
                            if (code == 0) {//thanh cong


                                //new list
                                ArrayList<UserInfo> list = UserInfo.parse(context, args);
                                MyUtils.howLong(start, "read contact step 3 server contact " + list.size());


                                if (list != null && list.size() > 0) {
                                    //Dong bo voi db
                                    PhoneUtils.setPhoneForList(list);
                                    long start = SystemClock.elapsedRealtime();
                                    realm.executeTransaction(new Realm.Transaction() {
                                        @Override
                                        public void execute(Realm realm) {
                                            for (int i = 0; i < list.size(); i++) {
                                                UserInfo user = list.get(i);
                                                MyUtils.syncOneContact(realm, user);
                                            }

                                            //hoan thanh step 1
                                            MyUtils.howLong(start, "sync contact step 0 - " + list.size());

                                            //update lai fragment 1, 2
                                            //neu dong bo thi bao 2 màn hinh 1,2 load lai danh sach, nguoc lai thi ko lam gi
                                            // LỌC DANH SÁCH USER ONLINE
                                            context.sendBroadcast(new Intent(Contact_Fragment_2_Online.ACTION_LOAD_CONTACT_ONLINE));

                                            // LỌC DANH SÁCH USER CÓ TÀI KHOẢN MBN
                                            context.sendBroadcast(new Intent(Contact_Fragment_1_Chat.ACTION_LOAD_CONTACT_HAVE_MBN_ACCOUNT));

                                        }
                                    });
                                }


                                //doc database va set adapter
                                readAndSetAdapter();
                                //time lastest sync
                                setTextLastSync();

                            } else {
                                //xem nhu chua dong bo
//                                        db.putBoolean(UserInfo.IS_SAVE_LIST_USER_INFO, false);

                                //dong bo duoc hay khong thi cung hien danh sach dang co
                                readAndSetAdapter();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            //dong bo duoc hay khong thi cung hien danh sach dang co
                            readAndSetAdapter();
                        }
                    });
            });
        } else {
            //dong bo duoc hay khong thi cung hien danh sach dang co
            readAndSetAdapter();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Danh sách danh bạ online
     */
    /*private void getContactListOnly() {
        if (socket != null && socket.connected()) {

            JSONObject obj = new JSONObject();
            try {
                obj.put("pageIndex", 0);
                obj.put("itemPerPage", 0); //lay het
            } catch (Exception e) {
                e.printStackTrace();
            }

            socket.emit("getContactList", obj, (Ack) args -> {
                if (getActivity() != null && !getActivity().isFinishing())
                    getActivity().runOnUiThread(() -> {

                        //dong bo 1
                        try {
                            JSONObject obj1 = new JSONObject(args[0].toString());
                            int code = obj1.getInt("errorCode");
                            if (code == 0) {//thanh cong
                                ArrayList<UserInfo> list = UserInfo.parse(context, args);

                                //DONG BO DANH SACH TRUOC KHI HIEN LEN
                                if (list != null && list.size() > 0) {
                                    if(listUsers.size()>0){
                                        for (int i = 0; i < listUsers.size(); i++) {
                                            UserInfo user = listUsers.get(i);
                                            for (int j = 0; j < list.size(); j++) {
                                                UserInfo check = list.get(j);
                                                //so sanh sdt
                                                boolean equal = MyUtils.comparePhoneNumber(check.getPhone(), user.getPhone());
                                                if (equal) {

                                                    //set lai thong tin cho listUsers
                                                    user.set_id(check.get_id());
                                                    user.setAvatar(check.getAvatar());
                                                    user.setLocalContact(false);
                                                    user.setHaveMBNAccount(true);
                                                    user.setNameMBN(check.getName());
                                                    user.setPhone(check.getPhone());//phone da dc quoc te hoa +84xxxx
                                                    user.setEmail(check.getEmail());

                                                    listUsers.set(i, user);
                                                    break;
                                                }
                                            }
                                        }
                                    }else{
                                        listUsers.addAll(list);
                                    }

                                }


                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        //dong bo 2
                        //dong bo 2: local vs server
                        step2CheckPhoneNumberFollowPage();
                    });
            });
        }
    }*/


}
