package com.workchat.core.chat;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.havrylyuk.alphabetrecyclerview.OnHeaderClickListener;
import com.havrylyuk.alphabetrecyclerview.OnLetterClickListener;
import com.pchmn.materialchips.ChipsInput;
import com.pchmn.materialchips.model.ChipInterface;
import com.workchat.core.config.ChatApplication;
import com.workchat.core.contacts.alphabet_recycler.LetterSelector;
import com.workchat.core.contacts.alphabet_recycler.RecyclerSectionItemDecoration;
import com.workchat.core.mbn.models.UserChatCore;
import com.workchat.core.models.realm.UserInfo;
import com.workchat.core.utils.MyUtils;
import com.workchat.corechat.R;
import com.workchat.corechat.R2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import io.realm.Realm;
import io.realm.RealmResults;
import io.socket.client.Socket;

/**
 * Tìm user chat từ danh ba (app workchat)
 * Chuyen sang dung @{@link SearchUserChatFromRestApiActivity} search theo api cua module app
 */
@Deprecated
public class SearchUserChat2Activity extends AppCompatActivity implements OnHeaderClickListener, OnLetterClickListener {
    public static final String SEARCH_QUERY = "SEARCH_QUERY";

    public static final String IS_ADD_MEMBER = "IS_ADD_MEMBER";
    private boolean isAddMember = false;//add 1 lan 1 nguoi

    private Activity context = this;
    @BindView(R2.id.editText)
    EditText txt;
    @BindView(R2.id.imageView1)
    ImageView img;
    @BindView(R2.id.loading_progress)
    SmoothProgressBar progress_loading;
    @BindView(R2.id.rvPartner)
    RecyclerView rv;

    @BindView(R2.id.chips_input)
    ChipsInput chipsInput;
    @BindView(R2.id.imgChat)
    ImageView imgChat;

    Realm realm;

    private UserChatCore owner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user_chat_2);

        ButterKnife.bind(this);
        realm = ChatApplication.Companion.getRealmChat();
        owner = ChatApplication.Companion.getUser();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Drawable d = ChatApplication.Companion.getIconBackCustom();
        if (d != null) {
            toolbar.setNavigationIcon(d);
        }else {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        registerReceivers();
        initKeyBoardListener();

        //text search
        txt.getBackground().clearColorFilter();
        txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!TextUtils.isEmpty(charSequence)) {
//                    txt.setCompoundDrawables(getResources().getDrawable(R.drawable.ic_close_black_24dp),null,null,null);
                    img.setVisibility(View.VISIBLE);
                } else {
                    img.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        img.setVisibility(View.INVISIBLE);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txt.setText("");
                txt.requestFocus();
            }
        });

        txt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    searchUser(input);
                }
                return false;
            }
        });

        txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                //ngung bao nhieu ms thi moi search
                if (!mTyping) {
                    mTyping = true;
                }

                mTypingHandler.removeCallbacks(onTypingTimeout);
                mTypingHandler.postDelayed(onTypingTimeout, TYPING_TIMER_LENGTH);
            }
        });


        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        System.out.println("The RecyclerView is not scrolling");
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        MyUtils.hideKeyboard(context, txt);
                        break;
                    case RecyclerView.SCROLL_STATE_SETTLING:
                        System.out.println("Scroll Settling");
                        break;

                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dx > 0) {
                    System.out.println("Scrolled Right");
                } else if (dx < 0) {
                    System.out.println("Scrolled Left");
                } else {
                    System.out.println("No Horizontal Scrolled");
                }

                if (dy > 0) {
                    System.out.println("Scrolled Downwards");
                } else if (dy < 0) {
                    System.out.println("Scrolled Upwards");
                } else {
                    System.out.println("No Vertical Scrolled");
                }
            }
        });


        chipsInput.addChipsListener(new ChipsInput.ChipsListener() {
            @Override
            public void onChipAdded(ChipInterface chip, int newSize) {
                MyUtils.log("chip add");
                isAddChip = true;
            }

            @Override
            public void onChipRemoved(ChipInterface chip, int newSize) {
                MyUtils.log("chip remove");
            }

            @Override
            public void onTextChanged(CharSequence text) {
                if (!isAddChip) {
                    input = text.toString();
                    beginSearch();
                } else {
                    //dang add chip, neu la tim thi input!="" thi reset lai
                    isAddChip = false;
                    if (!TextUtils.isEmpty(input)) {
                        input = "";
                        beginSearch();
                    }
                }
            }
        });
        imgChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                imgChat.setEnabled(false);

                ArrayList<UserChatCore> list = (ArrayList<UserChatCore>) chipsInput.getSelectedChipList();
                if (list.size() > 0) {
                    if (MyUtils.checkInternetConnection(context)) {

                        if (isAddMember) {
                            //moi lan add 1 nguoi
                            Intent intent = new Intent();
                            intent.putParcelableArrayListExtra(UserChatCore.USER_MODEL, list);
                            setResult(Activity.RESULT_OK, intent);

                            finish();

                        } else {
                            //members [{userId, name, phone, email, avatar, url}], (roomName, roomAvatar có thể ko truyền)
                            JSONArray array = new JSONArray();
                            for (int i = 0; i < list.size(); i++) {
                                UserChatCore user = list.get(i);

                                JSONObject json = new JSONObject();
                                try {
                                    json.put("avatar", user.getAvatar());
                                    json.put("email", user.getEmail());
                                    json.put("name", user.getName());
                                    json.put("phone", user.getPhone());
                                    json.put("url", user.getUrl());
                                    json.put("userId", user.get_id());

                                    array.put(json);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }


                            Intent intent = new Intent(context, ChatActivity.class);
                            intent.putExtra(UserChatCore.JSON_CREATE_ROOM, array.toString());
                            context.startActivity(intent);

                            finish();
                        }

                    } else {
                        MyUtils.showThongBao(context);
                        imgChat.setEnabled(true);
                    }
                }
            }
        });


        /////////////////////////////////////////////////////////
        Bundle b = getIntent().getExtras();
        if (b != null) {
            isAddMember = b.getBoolean(IS_ADD_MEMBER, false);

        }


        //tao data ban dan
//        searchUser("a");

        //DOC TU MEMORY
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<UserInfo> list = realm.where(UserInfo.class).sort("name").findAll();
                listUsers = new ArrayList<UserInfo>();
                listUsers.addAll(list);
//                            MyUtils.howLong(start, "read "+listUsers.size());
                if (listUsers != null && listUsers.size() > 0) {

                } else {

                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initAdapterContacts();
                    }
                });
            }
        });

    }

    private boolean isAddChip = false;

    private void beginSearch() {
        //ngung bao nhieu ms thi moi search
        if (!mTyping) {
            mTyping = true;
        }

        mTypingHandler.removeCallbacks(onTypingTimeout);
        mTypingHandler.postDelayed(onTypingTimeout, TYPING_TIMER_LENGTH);
    }

    private void initAdapterContacts() {
        //tao lai user tu userInfo
        if (listUsers != null && listUsers.size() > 0) {

            List<UserChatCore> users = new ArrayList<UserChatCore>();
            for (int i = 0; i < listUsers.size(); i++) {

                UserInfo info = listUsers.get(i);
                UserChatCore user = new UserChatCore();
                user.setAvatar(info.getAvatar());
                user.setEmail(info.getEmail());
                user.setName(info.getName());
                user.setNameMBN(info.getNameMBN());
                user.set_id(info.get_id());
                user.setUrl(info.getUrl());
                user.setPhone(info.getPhone());
                user.setToken(info.getToken());

                users.add(user);
            }

            initRecyclerView(users);

        }

    }

    ///////////////////////////////////////////
    SearchUserChatAdapter adapter;

    void searchUser(String query) {
        if (!TextUtils.isEmpty(query)) {
            if (adapter != null) {
                adapter.getFilter().filter(query);

                //bo recorator
                if (rv.getItemDecorationCount() > 0) {
                    rv.removeItemDecorationAt(0);
                }
                //an selector
                mLetterSelector.setVisibility(View.GONE);
            }
        } else {
            //restore
            initAdapterContacts();
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    private String input = "";
    /////////////////////////////////////////////////////////////////////////////////////////////
    private static final int TYPING_TIMER_LENGTH = 600;
    private boolean mTyping = false;
    private Handler mTypingHandler = new Handler();
    private Runnable onTypingTimeout = new Runnable() {
        @Override
        public void run() {
            if (!mTyping) return;

            mTyping = false;
            //dang stop thi search
            searchUser(input);
        }
    };

    /////////////////////////////////////////////////////////////////////////////////////
    public static final String ADD_CHIP = "ADD_CHIP";
    private BroadcastReceiver receiver;

    private void registerReceivers() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle b = intent.getExtras();
                if (intent.getAction().equals(ADD_CHIP)) {
                    if (b != null) {
                        UserChatCore user = b.getParcelable(UserChatCore.USER_MODEL);

                        if (chipsInput != null) {
//                            input = "";
                            //neu chua co tai khoan MBN thi tao moi
                            if (TextUtils.isEmpty(user.get_id())) {
                                if (owner != null) {
//                                    createUserTemp(context, user.getName(), user.getPhone(), owner.get_id());


                                    android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(context);
                                    dialog.setMessage(R.string.luva_invite_user_by_sms_alert);
                                    dialog.setPositiveButton(R.string.ok, (arg0, arg1) -> {

                                        arg0.dismiss();
                                        MyUtils.inviteSms(context, user.getPhone(), context.getString(R.string.luva_invite_user_by_sms_content));

                                    });
                                    dialog.setNegativeButton(R.string.cancel, (arg0, arg1) -> {
                                        arg0.dismiss();
                                    });
                                    android.app.AlertDialog alertDialog = dialog.create();
                                    alertDialog.show();


                                }
                            } else {
                                chipsInput.addChip(user);
                            }

                        }


                    }
                }
            }
        };
        registerReceiver(receiver, new IntentFilter(ADD_CHIP));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
        /*if (realm != null && !realm.isClosed()) {
            realm.close();
        }*/
    }


    //TODO CHECK PHONE NUMBER
    /*private void createUserTemp(final Context context, String name, String phone, final String ownerId) {
        if (context != null) {
            if (!TextUtils.isEmpty(phone)) {

                phone = MyUtils.getPhoneNumberOnly(phone);

                String countryCode = PhoneUtils.getDefaultCountryNameCode();
                boolean isValid = PhoneUtils.isValidNumber(phone, countryCode);
                if (isValid) {
                    String phoneValided = PhoneUtils.getE164FormattedMobileNumber(phone, countryCode);
                    if (phoneValided != null) {

                        Socket socket = ChatApplication.Companion.getSocket();
                        if (socket != null && socket.connected()) {
                            JSONObject obj = new JSONObject();
                            try {
                                obj.put("phone", phoneValided);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            socket.emit("getOrCreateAccountByPhone", obj, new Ack() {
                                @Override
                                public void call(Object... args) {
                                    final UserMBN user = ParseJson.ParseUserLuva(args);
                                    if (user != null && user.get_id() > 0) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (user.getPhone().equalsIgnoreCase(owner.getPhone())) {
                                                    MyUtils.showAlertDialog(context, R.string.phone_of_owner);
                                                } else {
                                                    if (chipsInput != null) {
                                                        chipsInput.addChip(user);
                                                    }

                                                }


                                            }
                                        });
                                    }

                                }
                            });
                        }

                    } else {
                        MyUtils.showToast(context, R.string.number_phone_incorrect);
                    }

                } else {
                    MyUtils.showToast(context, R.string.number_phone_incorrect);
                }
            } else {
                MyUtils.showToast(context, R.string.please_input_phone);
            }


        }
    }*/

    //////////////////////////////////////////////////////////////////////////////////////////
    private ArrayList<UserInfo> listUsers = new ArrayList<>();

    /////////////////////////////////////////////////////////////////////////////////
    private Socket socket;

    @Override
    protected void onResume() {
        super.onResume();

        //get data
        socket = ChatApplication.Companion.getSocket();
        if (socket != null && !socket.connected()) {
            socket.connect();
        }

    }


    private void initRecyclerView(List<UserChatCore> users) {
        if (users != null && users.size() > 0) {
            //
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
            rv.setLayoutManager(mLayoutManager);
            rv.setNestedScrollingEnabled(false);
            adapter = new SearchUserChatAdapter(users, context, owner);

            isSetDecoration = false;
            setDecoration();
            rv.setAdapter(adapter);
            initSelector();

        }
    }

    private void initSelector() {
        if (adapter != null) {
            //khoi tao an di, sau khi co danh sach letter thi moi hien ra
            mLetterSelector.setVisibility(View.GONE);
            mLetterSelector.setOnLetterChangedListener(new LetterSelector.OnLetterChangedListener() {
                @Override
                public void onLetterChanged(String s) {
                    mHandler.removeCallbacks(mRunnable);
                    mTipView.setText(s);
                    mTipView.setVisibility(View.VISIBLE);
                    mHandler.postDelayed(mRunnable, 500);

                    onLetterClick(s.charAt(0));
                }
            });

            //set character keys
            try {
                //tao lai letters
                TreeSet<Character> treeSet = (TreeSet<Character>) adapter.getHeadersLetters();
                Character[] letters = treeSet.toArray(new Character[treeSet.size()]);
                String[] arr = new String[letters.length];
                for (int i = 0; i < letters.length; i++) {
                    arr[i] = letters[i] + "";
                }

                if (arr != null && arr.length > 0) {
                    mLetterSelector.setArrayLetter(arr);

                    if (!isShowKeyboard) {
                        mLetterSelector.setVisibility(View.VISIBLE);
                    }
                } else {
                    mLetterSelector.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
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

    private RecyclerSectionItemDecoration sectionItemDecoration;
    private boolean isSetDecoration = false;

    private void setDecoration() {
        if (isSetDecoration == false) {
            sectionItemDecoration =
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

    private RecyclerSectionItemDecoration.SectionCallback getSectionCallback(final List<UserChatCore> people) {
        return new RecyclerSectionItemDecoration.SectionCallback() {
            @Override
            public boolean isSection(int position) {
                boolean isSection = false;
                boolean condition2 = false;
                if (position >= 0 && position - 1 >= 0 && position < people.size() && position - 1 < people.size()) {

                    String name1 = people.get(position).getName();
                    String name2 = people.get(position - 1).getName();
                    if (!TextUtils.isEmpty(name1) && !TextUtils.isEmpty(name2)) {

                        char char1 = name1.charAt(0);
                        char1 = MyUtils.getUnsignedChar(char1);
                        char1 = Character.toUpperCase(char1);

                        char char2 = name2.charAt(0);
                        char2 = MyUtils.getUnsignedChar(char2);
                        char2 = Character.toUpperCase(char2);


                        condition2 = char1 != char2;
                    }
                }
                if (position == 0 || condition2) {
                    isSection = true;
                }
                return isSection;
            }

            @Override
            public CharSequence getSectionHeader(int position) {
                if (people != null && position < people.size()) {
                    char c = people.get(position).getName().charAt(0);
                    c = MyUtils.getUnsignedChar(c);
                    c = Character.toUpperCase(c);
                    return String.valueOf(c);
                } else {
                    return "#";
                }
            }
        };
    }

    @Override
    public void onHeaderClick(View header, long headerId) {
        //ko can implement
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

    ///////////////////////////////////////////////////////////////////////////////////////////////
    private boolean isShowKeyboard = false;

    private void initKeyBoardListener() {
        // Минимальное значение клавиатуры. Threshold for minimal keyboard height.
        final int MIN_KEYBOARD_HEIGHT_PX = 150;
        // Окно верхнего уровня view. Top-level window decor view.
        final View decorView = getWindow().getDecorView();
        // Регистрируем глобальный слушатель. Register global layout listener.
        decorView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            // Видимый прямоугольник внутри окна. Retrieve visible rectangle inside window.
            private final Rect windowVisibleDisplayFrame = new Rect();
            private int lastVisibleDecorViewHeight;

            @Override
            public void onGlobalLayout() {
                decorView.getWindowVisibleDisplayFrame(windowVisibleDisplayFrame);
                final int visibleDecorViewHeight = windowVisibleDisplayFrame.height();

                if (lastVisibleDecorViewHeight != 0) {
                    if (lastVisibleDecorViewHeight > visibleDecorViewHeight + MIN_KEYBOARD_HEIGHT_PX) {
                        Log.d("Pasha", "SHOW");
                        isShowKeyboard = true;
                        mLetterSelector.setVisibility(View.GONE);
                    } else if (lastVisibleDecorViewHeight + MIN_KEYBOARD_HEIGHT_PX < visibleDecorViewHeight) {
                        Log.d("Pasha", "HIDE");
                        isShowKeyboard = false;
                        //neu khong co input thi show selector
                        if (TextUtils.isEmpty(input)) {
                            mLetterSelector.setVisibility(View.VISIBLE);
                        }
                    }
                }
                // Сохраняем текущую высоту view до следующего вызова.
                // Save current decor view height for the next call.
                lastVisibleDecorViewHeight = visibleDecorViewHeight;
            }
        });
    }
}
