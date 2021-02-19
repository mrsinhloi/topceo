package com.workchat.core.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.workchat.core.chat.RecentChatType;
import com.workchat.core.chat.RecentChat_Adapter;
import com.workchat.core.config.ChatApplication;
import com.workchat.core.contacts.Contact_Fragment_3_All_Adapter;
import com.workchat.core.event.EventChatRecent;
import com.workchat.core.event.EventContact;
import com.workchat.core.mbn.models.UserChatCore;
import com.workchat.core.models.chat.RoomLog;
import com.workchat.core.models.realm.Room;
import com.workchat.core.models.realm.UserInfo;
import com.workchat.core.utils.MyUtils;
import com.workchat.corechat.R;
import com.workchat.corechat.R2;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link Search_Fragment_All#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Search_Fragment_All extends Fragment {

    public Search_Fragment_All() {
        // Required empty public constructor
    }


    boolean isForward = false;
    boolean isSharing = false;
    public static Search_Fragment_All newInstance(boolean isForward, boolean isSharing) {
        Search_Fragment_All fragment = new Search_Fragment_All();
        Bundle args = new Bundle();
        args.putBoolean(RoomLog.IS_FORWARD, isForward);
        args.putBoolean(RoomLog.IS_SHARING, isSharing);

        fragment.setArguments(args);
        return fragment;
    }

    private UserChatCore owner;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isForward = getArguments().getBoolean(RoomLog.IS_FORWARD);
            isSharing = getArguments().getBoolean(RoomLog.IS_SHARING);
        }
        setHasOptionsMenu(true);
        owner = ChatApplication.Companion.getUser();
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

    ///////////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////
    @BindView(R2.id.recyclerView1)
    RecyclerView rv1;
    @BindView(R2.id.recyclerView2)
    RecyclerView rv2;
    @BindView(R2.id.txtReadMore1)
    TextView txtReadMore1;
    @BindView(R2.id.txtReadMore2)
    TextView txtReadMore2;
    @BindView(R2.id.txtEmpty)TextView txtEmpty;

    @BindView(R2.id.linearContent)LinearLayout linearContent;
    @BindView(R2.id.linearContact)LinearLayout linearContact;
    @BindView(R2.id.linearChatRecent)LinearLayout linearRecentChat;
    @BindView(R2.id.nestedScrollView)
    NestedScrollView sv;


    ///////////////////////////////////////////////////////////////////////////////////////

    @BindView(R2.id.relativeSaveMessage)
    RelativeLayout relativeSaveMessage;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_search_all, container, false);
        ButterKnife.bind(this, v);

        realm = ChatApplication.Companion.getRealmChat();

        /////////////////////////////////////////////////////////////////////
        if(isForward){
            relativeSaveMessage.setVisibility(View.VISIBLE);
            relativeSaveMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(isSharing){
                        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(context);
                        dialog.setMessage(context.getString(R.string.send_to_room_x, context.getString(R.string.saved_messages)));
                        dialog.setTitle(R.string.app_name);
                        dialog.setNegativeButton(R.string.cancel, null);
                        dialog.setPositiveButton(R.string.ok, (arg0, arg1) -> {
                            arg0.dismiss();
                            MyUtils.chatWithUser(context, owner, isForward);

                            //dong man hinh search
                            if(isForward){
                                context.sendBroadcast(new Intent(MH09_SearchActivity.ACTION_FINISH));
                            }
                        });
                        android.app.AlertDialog alertDialog = dialog.create();
                        alertDialog.show();
                    }else{
                        MyUtils.chatWithUser(context, owner, isForward);

                        //dong man hinh search
                        if(isForward){
                            context.sendBroadcast(new Intent(MH09_SearchActivity.ACTION_FINISH));
                        }
                    }
                }
            });
        }else{
            relativeSaveMessage.setVisibility(View.GONE);
        }

        init();
        ////////////////////////////////////////////////////////
        initRecyclerView();



        /*registerHideKeyboard();
        sv.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {


                //hide keyboard when scroll
                if (v.getChildAt(v.getChildCount() - 1) != null) {

                    if (scrollY > oldScrollY) {
//                        Log.i(TAG, "Scroll DOWN");
                        //hide keyboard
                        if (isKeyboardShow) {
                            MyUtils.hideKeyboard(getActivity());
                        }

                    }
                    if (scrollY < oldScrollY) {
//                        Log.i(TAG, "Scroll UP");
//                        isScrollDown = false;
                        if (isKeyboardShow) {
                            MyUtils.hideKeyboard(getActivity());
                        }
                    }

                    if (scrollY == 0) {
//                        Log.i(TAG, "TOP SCROLL");
                        if (isKeyboardShow) {
                            MyUtils.hideKeyboard(getActivity());
                        }
                    }
//
                    *//*if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
//                        Log.i(TAG, "BOTTOM SCROLL");
                        loadMore();
                    }*//*


                }


            }
        });*/


        return v;
    }


    private boolean isKeyboardShow = false;

    private void registerHideKeyboard() {
        rv1.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = rv1.getRootView().getHeight() - rv1.getHeight();

                if (heightDiff > 100) {
//                    Log.e("MyActivity", "keyboard opened");
                    isKeyboardShow = true;
                } else {
//                    Log.e("MyActivity", "keyboard closed");
                    isKeyboardShow = false;

                }
            }
        });
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////

    private void initRecyclerView() {
        LinearLayoutManager mLayoutManager1 = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        rv1.setLayoutManager(mLayoutManager1);
        rv1.setNestedScrollingEnabled(false);
        rv1.setHasFixedSize(true);
        rv1.setItemAnimator(new DefaultItemAnimator());

        LinearLayoutManager mLayoutManager2 = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        rv2.setLayoutManager(mLayoutManager2);
        rv2.setNestedScrollingEnabled(false);
        rv2.setHasFixedSize(true);
        rv2.setItemAnimator(new DefaultItemAnimator());


    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    private void init(){
        //xem nhu chua co du lieu
        linearContent.setVisibility(View.GONE);
        linearContact.setVisibility(View.GONE);
        linearRecentChat.setVisibility(View.GONE);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    public static final int numberMore = 4;
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventContact event) {
        if(event!=null){
            if(event.getNumber()>0){
                txtEmpty.setVisibility(View.GONE);
                linearContent.setVisibility(View.VISIBLE);
                linearContact.setVisibility(View.VISIBLE);
                ArrayList<UserInfo> list = event.getList();

                MyUtils.sortList(list);
                Contact_Fragment_3_All_Adapter adapter = new Contact_Fragment_3_All_Adapter(list, getActivity());
                adapter.setIsForward(isForward);
                rv1.setAdapter(adapter);
                if(event.getNumber()> numberMore){
                    txtReadMore1.setVisibility(View.VISIBLE);
                    txtReadMore1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //scroll qua tab danh ba
                            Intent intent = new Intent(MH09_SearchActivity.ACTION_GO_TO_POSITION_SEARCH);
                            intent.putExtra(MH09_SearchActivity.ACTION_GO_TO_POSITION_SEARCH, 1);
                            context.sendBroadcast(intent);
                        }
                    });
                }else{
                    txtReadMore1.setVisibility(View.GONE);
                }
            }else{
                linearContact.setVisibility(View.GONE);
                if(linearRecentChat.getVisibility()==View.GONE){
                    txtEmpty.setVisibility(View.VISIBLE);
                    linearContent.setVisibility(View.GONE);
                }
            }
        }
    }

    RecentChat_Adapter adapter;
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventChatRecent event) {
        if(event!=null){
            if(event.getNumber()>0) {
                txtEmpty.setVisibility(View.GONE);
                linearContent.setVisibility(View.VISIBLE);
                linearRecentChat.setVisibility(View.VISIBLE);
                ArrayList<Room> list = event.getList();

                adapter = new RecentChat_Adapter(getActivity(), list, RecentChatType.ALL);
                adapter.setIsForward(isForward);
                adapter.setCanLongClick(isCanLongClick);
                rv2.setAdapter(adapter);

                if (event.getNumber() > numberMore) {
                    txtReadMore2.setVisibility(View.VISIBLE);
                    txtReadMore2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //scroll qua tab danh ba
                            Intent intent = new Intent(MH09_SearchActivity.ACTION_GO_TO_POSITION_SEARCH);
                            intent.putExtra(MH09_SearchActivity.ACTION_GO_TO_POSITION_SEARCH, 2);
                            context.sendBroadcast(intent);
                        }
                    });
                } else {
                    txtReadMore2.setVisibility(View.GONE);
                }
            }else{
                linearRecentChat.setVisibility(View.GONE);
                if(linearContact.getVisibility()==View.GONE){
                    txtEmpty.setVisibility(View.VISIBLE);
                    linearContent.setVisibility(View.GONE);
                }
            }
        }

    }

    private boolean isCanLongClick = true;
    public void setCanLongClick(boolean canLongClick) {
        this.isCanLongClick = canLongClick;
        if(adapter!=null){
            adapter.setCanLongClick(isCanLongClick);
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    private Realm realm;
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        /*if(realm != null) {
            realm.close();
            realm = null;
        }*/
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

}