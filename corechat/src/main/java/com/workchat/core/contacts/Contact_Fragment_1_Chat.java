package com.workchat.core.contacts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.workchat.core.chat.ChatNhanh_Fragment_Bottom;
import com.workchat.core.chat.receiver.NetworkChangeReceiver;
import com.workchat.core.config.ChatApplication;
import com.workchat.core.models.realm.UserInfo;
import com.workchat.core.utils.MyUtils;
import com.workchat.corechat.R;
import com.workchat.corechat.R2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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
 * Use the {@link Contact_Fragment_1_Chat#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Contact_Fragment_1_Chat extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String CONTACT_TYPE = "CONTACT_TYPE";


    public Contact_Fragment_1_Chat() {
        // Required empty public constructor
    }

    private int contactType = ContactType.ALL;

    public static Contact_Fragment_1_Chat newInstance(int contactType) {
        Contact_Fragment_1_Chat fragment = new Contact_Fragment_1_Chat();
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
        initSocket();

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
    /////////////////////////////////////////////////////////////////////////////////////


    private Realm realm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.contact_fragment_1, container, false);
        ButterKnife.bind(this, v);


        realm = ChatApplication.Companion.getRealmChat();
        progressBar.setVisibility(View.GONE);
        txtEmpty.setVisibility(View.GONE);

        registerReceiver();
        initRecyclerView();
        readAndSetAdapter();

        return v;
    }


    /////////////////////////////////////////////////////////////////////////////////
    private Socket socket;

    private void initSocket() {
        //get data
        socket = ChatApplication.Companion.getSocket();
    }

    @Override
    public void onResume() {
        super.onResume();

        initSocket();

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
    public static final String ACTION_LOAD_CONTACT_HAVE_MBN_ACCOUNT = "ACTION_LOAD_CONTACT_HAVE_MBN_ACCOUNT";
    public static final String ACTION_SEARCH_ACCOUNT_LOCAL_3 = "ACTION_SEARCH_ACCOUNT_LOCAL_3";
    public static final String ACTION_GO_TO_TOP_CONTACT_TAB_3 = "ACTION_GO_TO_TOP_CONTACT_TAB_3";
    public static final String ACTION_REMOVE_CONTACT_TAB_CHATNHANH = "ACTION_REMOVE_CONTACT_TAB_CHATNHANH";
    public static final String ACTION_LOAD_CONTACT_IF_ADAPTER_NULL = "ACTION_LOAD_CONTACT_IF_ADAPTER_NULL";

    BroadcastReceiver receiver;

    //todo kiểm tra khi add/remove user điều khiển load lại giao diện
    private void registerReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle b = intent.getExtras();
                if (intent.getAction().equalsIgnoreCase(ACTION_LOAD_CONTACT_HAVE_MBN_ACCOUNT)) {
                    //doc len tu db realm
                    readAndSetAdapter();

                }else if (intent.getAction().equalsIgnoreCase(ACTION_LOAD_CONTACT_IF_ADAPTER_NULL)) {
                    if(adapter==null || (adapter!=null && adapter.getItemCount()==0)){
                        //doc len tu db realm
                        readAndSetAdapter();
                    }

                } else if (intent.getAction().equalsIgnoreCase(ACTION_SEARCH_ACCOUNT_LOCAL_3)) {
                    if (b != null) {
                        String keyword = b.getString(UserInfo.KEY_SEARCH);
                        search(keyword);
                    }
                } else if (intent.getAction().equalsIgnoreCase(ACTION_GO_TO_TOP_CONTACT_TAB_3)) {
                    gotoTop();
                } else if (intent.getAction().equalsIgnoreCase(ACTION_REMOVE_CONTACT_TAB_CHATNHANH)) {
                    if (b != null) {
                        UserInfo userInfo = b.getParcelable(UserInfo.USER_INFO);
                        if (userInfo != null) {
                            adapter.removeItem(userInfo.get_id());

                            //danh dau database ko con trong danh ba nua
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    UserInfo user = realm.where(UserInfo.class).equalTo("_id", userInfo.get_id()).findFirst();
                                    if (user != null) {
                                        user.setInChatContact(false);
                                    }

                                }
                            });


                        }
                    }
                } else if (intent.getAction().equalsIgnoreCase(NetworkChangeReceiver.ACTION_CONNECTIVITY_CHANGE)) {
                    if (!isInitialStickyBroadcast()) {
                        //neu co mang lai ma list chua co thi goi de lay lai
                        if (MyUtils.checkInternetConnection(getContext())) {
                            if (adapter == null || (adapter != null && adapter.getItemCount() == 0)) {
                                readAndSetAdapter();
                            }
                        }
                    }
                }

            }
        };
        context.registerReceiver(receiver, new IntentFilter(ACTION_LOAD_CONTACT_HAVE_MBN_ACCOUNT));
        context.registerReceiver(receiver, new IntentFilter(ACTION_SEARCH_ACCOUNT_LOCAL_3));
        context.registerReceiver(receiver, new IntentFilter(ACTION_GO_TO_TOP_CONTACT_TAB_3));
        context.registerReceiver(receiver, new IntentFilter(ACTION_REMOVE_CONTACT_TAB_CHATNHANH));
        context.registerReceiver(receiver, new IntentFilter(ACTION_LOAD_CONTACT_IF_ADAPTER_NULL));

        //lang nghe ket noi mang thay doi, gởi broadcast đến màn hình Main và màn hình Chat
        context.registerReceiver(receiver, new IntentFilter(NetworkChangeReceiver.ACTION_CONNECTIVITY_CHANGE));

    }

    private void search(String query) {
        if (adapter != null) {
            adapter.filter(query);
        }
    }

    //scroll to top
    private void restoreScrollPositionAfterAdAdded() {
        if (rv != null) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) rv.getLayoutManager();
            if (layoutManager != null) {
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (firstVisibleItemPosition == 0) {
                    layoutManager.scrollToPosition(0);
                }
            }
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

    ///////////////////////////////////////////////////////////////////////////////////////
    private boolean isFirst = true;

    private void readAndSetAdapter() {
        try {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    for (int i = 0; i < 3; i++) {
                        RealmResults<UserInfo> list = realm.where(UserInfo.class).equalTo("isInChatContact", true).findAll();
                        if(list.size()==0){
                            SystemClock.sleep(300);
                        }

                        if(list.size()>0){
                            if (listUsers != null) {
                                listUsers.clear();
                            }
                            listUsers = new ArrayList<UserInfo>(realm.copyFromRealm(list));
                            setAdapter();
                        }
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void setAdapter() {

        if(listUsers.size()>0){
            //tao section header
            ArrayList<UserInfo> list1 = new ArrayList<UserInfo>();//pin list
            ArrayList<UserInfo> list2 = new ArrayList<UserInfo>();

            int size = listUsers.size();
            for (int i = 0; i < size; i++) {
                UserInfo item = listUsers.get(i);
                if (item.isPin()) {
                    list1.add(item);
                } else {
                    list2.add(item);
                }
            }

            //tao header string
            if (list1.size() > 0) {

                //sort list roi gan header len dau
                MyUtils.sortList(list1);

                UserInfo header1 = new UserInfo();
                header1.setHeader(true);
                header1.setHeaderIcon(R.drawable.ic_pin_24dp);
                header1.setHeaderString(getString(R.string.pinned_number, list1.size()));
                list1.add(0, header1);
            }

            if (list2.size() > 0) {

                //sort list roi gan header len dau
                MyUtils.sortList(list2);


                UserInfo header1 = new UserInfo();
                header1.setHeader(true);
                header1.setHeaderIcon(R.drawable.ic_contacts_black_20dp);
                header1.setHeaderString(getString(R.string.contacts_number, list2.size()));
                list2.add(0, header1);
            }

            //tao lai list all
            listUsers.clear();
            listUsers.addAll(list1);
            listUsers.addAll(list2);


            //tao adapter
            adapter = new Contact_Fragment_3_All_Adapter(listUsers, getActivity());
            rv.setAdapter(adapter);

            //CHUAN BI DU LIEU SEARCH
            if (adapter != null) {
                adapter.beginSearch();
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////


}
