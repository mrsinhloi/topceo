package com.workchat.core.contacts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.workchat.core.chat.ChatNhanh_Fragment_Bottom;
import com.workchat.core.config.ChatApplication;
import com.workchat.core.database.TinyDB;
import com.workchat.core.mbn.models.UserChatCore;
import com.workchat.core.models.realm.UserChat;
import com.workchat.core.models.realm.UserInfo;
import com.workchat.core.utils.MyUtils;
import com.workchat.corechat.R;
import com.workchat.corechat.R2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import io.socket.client.Ack;
import io.socket.client.Socket;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link Contact_Fragment_2_Online#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Contact_Fragment_2_Online extends Fragment {

    private static final String CONTACT_TYPE = "CONTACT_TYPE";


    public Contact_Fragment_2_Online() {
        // Required empty public constructor
    }

    private int contactType = ContactType.ALL;

    public static Contact_Fragment_2_Online newInstance(int contactType) {
        Contact_Fragment_2_Online fragment = new Contact_Fragment_2_Online();
        Bundle args = new Bundle();
        args.putInt(CONTACT_TYPE, contactType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            contactType = getArguments().getInt(CONTACT_TYPE);
        }
        setHasOptionsMenu(true);
    }

    private Context context;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @BindView(R2.id.recyclerView)
    RecyclerView rv;
    @BindView(R2.id.progressBar)
    ProgressBar progressBar;
    @BindView(R2.id.txtEmpty)
    TextView txtEmpty;


    private TinyDB db;
    private Realm realm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.contact_fragment_2, container, false);
        ButterKnife.bind(this, v);

        db = new TinyDB(context);

        realm = ChatApplication.Companion.getRealmChat();
        progressBar.setVisibility(View.GONE);
        txtEmpty.setVisibility(View.GONE);
        /////////////////////////////////////////////////////////////////////
        registerReceiver();

        ////////////////////////////////////////////////////////
        initRecyclerView();

        //doc danh sach online
        //doc len tu db realm
        readAndSetAdapter();

        return v;
    }


    /////////////////////////////////////////////////////////////////////////////////
    private Socket socket;

    @Override
    public void onResume() {
        super.onResume();

        //get data
        socket = ChatApplication.Companion.getSocket();

    }


    //    private AlphabetRecyclerViewAdapter adapter;
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

            socket.emit("removeContact", obj, new Ack() {
                @Override
                public void call(final Object... args) {
                    if (getActivity() != null && !getActivity().isFinishing())
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    JSONObject obj = new JSONObject(args[0].toString());
                                    int code = obj.getInt("errorCode");
                                    if (code == 0) {
                                        //xoa thanh cong
                                        MyUtils.showToast(context, R.string.delete_success);
                                        //remove khoi adapter
                                        adapter.removeItem(userId);

                                    } else {
                                        String message = obj.getString("error");
                                        MyUtils.showAlertDialog(context, message);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                }
            });
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////
    LinearLayoutManager mLayoutManager;
    private Contact_Fragment_3_All_Adapter adapter;

    private void initRecyclerView() {
        mLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        rv.setLayoutManager(mLayoutManager);


    }

    /////////////////////////////////////////////////////////////////////////////////
    public static final String ACTION_LOAD_CONTACT_ONLINE = "ACTION_LOAD_CONTACT_ONLINE";
    public static final String ACTION_SEARCH_ACCOUNT_LOCAL_2 = "ACTION_SEARCH_ACCOUNT_LOCAL_2";
    public static final String ACTION_REMOVE_CONTACT_TAB_ONLINE = "ACTION_REMOVE_CONTACT_TAB_ONLINE";
    public static final String ACTION_GO_TO_TOP_CONTACT_TAB_2 = "ACTION_GO_TO_TOP_CONTACT_TAB_2";
    public static final String ACTION_WHEN_USER_ONLINE = "ACTION_WHEN_USER_ONLINE";
    public static final String ACTION_WHEN_USER_OFFLINE = "ACTION_WHEN_USER_OFFLINE";


    BroadcastReceiver receiver;

    //todo kiểm tra khi add/remove user điều khiển load lại giao diện
    private void registerReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle b = intent.getExtras();
                if (intent.getAction().equalsIgnoreCase(ACTION_LOAD_CONTACT_ONLINE)) {

                    //cho 2 s de fragment_1 dong bo xong db
                    //doc len tu db realm
                    readAndSetAdapter();


                } else if (intent.getAction().equalsIgnoreCase(ACTION_SEARCH_ACCOUNT_LOCAL_2)) {
                    if (b != null) {
                        String keyword = b.getString(UserInfo.KEY_SEARCH);
                        search(keyword);
                    }
                } else if (intent.getAction().equalsIgnoreCase(ACTION_REMOVE_CONTACT_TAB_ONLINE)) {
                    if (b != null) {
                        UserInfo userInfo = b.getParcelable(UserInfo.USER_INFO);
                        if (userInfo != null) {
                            if (adapter != null) adapter.removeItem(userInfo.get_id());
                        }
                    }
                } else if (intent.getAction().equalsIgnoreCase(ACTION_GO_TO_TOP_CONTACT_TAB_2)) {
                    gotoTop();
                } else if (intent.getAction().equalsIgnoreCase(ACTION_WHEN_USER_ONLINE)) {
                    String userId = b.getString(UserChatCore.USER_ID, "");
                    if (!TextUtils.isEmpty(userId)) {
                        if (!userIds.contains(userId)) {
                            userIds.add(userId);
                            //doc tu database va load len
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    //5ce4fb3b1607b307eca1cc4b
                                    RealmResults<UserInfo> list = realm.where(UserInfo.class).in("_id", new String[]{userId}).findAll();
                                    if (adapter != null && list.size() > 0) {
                                        adapter.addItem(list.get(0), 0);

                                    }
                                }
                            });

                        }

                    }
                } else if (intent.getAction().equalsIgnoreCase(ACTION_WHEN_USER_OFFLINE)) {
                    String userId = b.getString(UserChatCore.USER_ID, "");
                    if (!TextUtils.isEmpty(userId)) {
                        if (userIds.contains(userId)) {
                            userIds.remove(userId);
                            if (adapter != null) {
                                adapter.removeItem(userId);
                            }
                        }

                    }
                }


            }
        };
        context.registerReceiver(receiver, new IntentFilter(ACTION_LOAD_CONTACT_ONLINE));
        context.registerReceiver(receiver, new IntentFilter(ACTION_SEARCH_ACCOUNT_LOCAL_2));
        context.registerReceiver(receiver, new IntentFilter(ACTION_REMOVE_CONTACT_TAB_ONLINE));
        context.registerReceiver(receiver, new IntentFilter(ACTION_GO_TO_TOP_CONTACT_TAB_2));
        context.registerReceiver(receiver, new IntentFilter(ACTION_WHEN_USER_ONLINE));
        context.registerReceiver(receiver, new IntentFilter(ACTION_WHEN_USER_OFFLINE));

    }

    private void search(String query) {
        if (adapter != null) {
            adapter.filter(query);
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    private void gotoTop() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                rv.scrollToPosition(0);
                getContext().sendBroadcast(new Intent(ChatNhanh_Fragment_Bottom.ACTION_EXPAND_TOOLBAR));
            }
        }, 200);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            context.unregisterReceiver(receiver);
        }
        /*if (realm != null && !realm.isClosed()) {
            realm.close();
        }*/
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    private void readAndSetAdapter() {
        if (getActivity() != null && !getActivity().isFinishing()) {
            try {
                getUserOnline();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void getListUserFromDB() {
        if (userIds.size() > 0) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<UserInfo> list = realm.where(UserInfo.class).in("_id", userIds.toArray(new Long[userIds.size()])).findAll();
                    listUsers = new ArrayList<UserInfo>(realm.copyFromRealm(list));

                    MyUtils.sortList(listUsers);
                    adapter = new Contact_Fragment_3_All_Adapter(listUsers, getActivity());
                    rv.setAdapter(adapter);

                    //CHUAN BI DU LIEU SEARCH
                    if (adapter != null) {
                        adapter.beginSearch();
                    }

                }
            });


        }
    }

    public static ArrayList<String> userIds = new ArrayList<String>();

    private void getUserOnline() {
        String s = db.getString(UserChat.LIST_USER_ONLINE);
        if (!TextUtils.isEmpty(s)) {
            try {

                //{"errorCode":0,"error":null,"data":{"userIds":["5d08e4e0d0d1cd2254ee6526","5d0a6e1bc1313d0ad8b34409","5d09b51fc3f5e71bf4d6924c"]}}
                JSONObject json = new JSONObject(s);
                JSONObject data = json.getJSONObject("data");
                if (data != null && data.has("userIds")) {
                    JSONArray array = data.getJSONArray("userIds");
                    if (array != null && array.length() > 0) {
                        Type collectionType1 = new TypeToken<List<Long>>() {
                        }.getType();
                        userIds = new Gson().fromJson(array.toString(), collectionType1);
                        getListUserFromDB();
                    }
                }
//                s = s.substring(s.indexOf("["), s.indexOf("]") + 1);
//                JsonArray obj = (JsonArray) new JsonParser().parse(s);


            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
