package com.workchat.core.contacts;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.squareup.picasso.Picasso;
import com.workchat.core.chat.ChatUserDetailActivity;
import com.workchat.core.config.ChatApplication;
import com.workchat.core.event.EventContact;
import com.workchat.core.mbn.models.UserChatCore;
import com.workchat.core.models.realm.UserInfo;
import com.workchat.core.search.MH09_SearchActivity;
import com.workchat.core.utils.MyUtils;
import com.workchat.core.widgets.CircleTransform;
import com.workchat.corechat.R;
import com.workchat.corechat.R2;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.socket.client.Ack;
import io.socket.client.Socket;

/**
 * Created by MrPhuong on 2016-10-31.
 */

public class Contact_Fragment_3_All_Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;


    private boolean isForward = false;
    private boolean isSharing = false;

    public void setIsForward(boolean isForward) {
        this.isForward = isForward;
    }

    public void clear() {
        if (items != null) {
            items.clear();
            notifyDataSetChanged();
        }
    }

    public void setSharing(boolean sharing) {
        isSharing = sharing;
    }


    public class HeaderHolder extends RecyclerView.ViewHolder {
        @BindView(R2.id.imageView1)
        ImageView img1;
        @BindView(R2.id.textView1)
        TextView txt1;

        public HeaderHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R2.id.img1)
        ImageView img1;
        @BindView(R2.id.img2)
        ImageView img2;
        @BindView(R2.id.img3)
        ImageView img3;
        @BindView(R2.id.txt1)
        AppCompatTextView txt1;
        @BindView(R2.id.txt2)
        AppCompatTextView txt2;
        @BindView(R2.id.txt3)
        AppCompatTextView txt3;
        @BindView(R2.id.linearNameMyXteam)
        LinearLayout linearNameMyXteam;

        @BindView(R2.id.linearRoot)
        LinearLayout linearRoot;
        @BindView(R2.id.btnInvite)
        TextView btnInvite;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    private ArrayList<UserInfo> items = new ArrayList<>();

    public void setList(ArrayList<UserInfo> list) {
        this.items = new ArrayList<>(list);
    }

    public void addMore(ArrayList<UserInfo> list) {
        if (list != null && list.size() > 0) {
            int sum = items.size();
            this.items.addAll(sum, list);
            notifyItemRangeInserted(sum, list.size());
        }
    }

    public void addItem(UserInfo item, int position) {
        if (item != null) {
            try {
                int pos = findPosition(item.get_id());
                if (pos == -1) {//chua co thi moi them vao
                    items.add(position, item);
                    notifyItemInserted(position);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

    public void addItem(UserInfo item) {
        items.add(item);
        notifyDataSetChanged();

    }

    public void replaceItem(UserInfo item) {

        boolean isExist = false;
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).get_id().equals(item.get_id())) {
                items.set(i, item);
                isExist = true;
                break;
            }
        }
        if (isExist == false) {
            items.add(item);
        }

        notifyDataSetChanged();

    }

    private int findPosition(String id) {
        int position = -1;
        if (items != null && items.size() > 0) {
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i).isValid() && items.get(i).get_id().equals(id)) {
                    position = i;
                    break;
                }
            }
        }
        return position;
    }

    public void removeItem(String id) {

        int position = findPosition(id);
        if (position > -1) {
            items.remove(position);
            notifyItemRemoved(position);
        }

    }

    public void addItemOnTop(UserInfo userInfo) {
        if (userInfo != null) {
            int position = findPosition(userInfo.get_id());
            if (position > -1) {//co thi thay the
                items.set(position, userInfo);
                notifyItemChanged(position);
            } else {//ko co thi them vao dau
                items.add(0, userInfo);
                notifyItemInserted(0);
            }
        }

    }


    private int width = 0;
    private Activity context;
    private UserChatCore owner;

    public Contact_Fragment_3_All_Adapter(ArrayList<UserInfo> list, Activity context) {
        this.context = context;
        width = context.getResources().getDimensionPixelOffset(R.dimen.avatar_size_medium);
        owner = ChatApplication.Companion.getUser();
        items = list;


    }


    public ArrayList<UserInfo> getItems() {
        return items;
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) != null && items.get(position).isValid() && items.get(position).isHeader()) {
            return TYPE_HEADER;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.contact_fragment_3_row, parent, false);
            return new Contact_Fragment_3_All_Adapter.MyViewHolder(itemView);
        } else if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.contact_fragment_1_header, parent, false);
            return new HeaderHolder(view);
        }
        throw new RuntimeException("No match for " + viewType + ".");

    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder rootHolder, final int position) {

        final UserInfo item = items.get(position);

        if (rootHolder instanceof MyViewHolder) {
            MyViewHolder holder = (MyViewHolder) rootHolder;
            if (item != null && item.isValid()) {
                //set image
                if (!TextUtils.isEmpty(item.getAvatar())) {
                    Picasso.get()
                            .load(item.getAvatar())
                            .centerCrop()
                            .resize(width, width)
                            .transform(new CircleTransform(context))
                            .placeholder(R.drawable.icon_no_image)
                            .into(holder.img1);
                } else {
//                holder.img1.setImageResource(R.drawable.icon_no_image);

                    ColorGenerator generator = ColorGenerator.MATERIAL;
                    int color = generator.getColor(item.getName());

                    TextDrawable drawable = TextDrawable.builder()
                            .buildRound(item.getName().substring(0, 1), color);
                    holder.img1.setImageDrawable(drawable);
                }
                holder.txt1.setText(item.getName());

                ////
                String phoneAndEmail = "";
                if (!TextUtils.isEmpty(item.getPhone())) {
                    phoneAndEmail = item.getPhone();
                }
                if (!TextUtils.isEmpty(item.getEmail())) {
                    phoneAndEmail += (!TextUtils.isEmpty(phoneAndEmail)) ? (" / " + item.getEmail()) : item.getEmail();
                }
                holder.txt2.setText(phoneAndEmail);

                ////
                if (item.isHaveMBNAccount()) {
                    holder.linearNameMyXteam.setVisibility(View.VISIBLE);
                    holder.txt3.setText(item.getNameMBN());

                } else {
                    holder.linearNameMyXteam.setVisibility(View.GONE);
                }

                if (!item.getName().equalsIgnoreCase(item.getNameMBN())) {
                    holder.txt1.setVisibility(View.VISIBLE);
                } else {
                    holder.txt1.setVisibility(View.GONE);
                }


                holder.linearRoot.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (item != null) {

                            //Myxteam ko bat buoc co sdt, nen co email cung chat dc
//                        String phone = item.getPhone();
//                        if (!TextUtils.isEmpty(phone) && MyUtils.isMobilePhone(phone)) {
                            if (!item.isHaveMBNAccount()) {

//                            MyUtils.createUserTempAndGoToChat(context, item.getPhone(), owner.getPhone(), isForward);

                                android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(context);
                                dialog.setMessage(R.string.luva_invite_user_by_sms_alert);
//                            dialog.setTitle(title);
                                dialog.setPositiveButton(R.string.ok, (arg0, arg1) -> {

                                    arg0.dismiss();
                                    String phone = item.getPhone();
                                    MyUtils.inviteSms(context, phone, context.getString(R.string.luva_invite_user_by_sms_content));

                                });
                                dialog.setNegativeButton(R.string.cancel, (arg0, arg1) -> {
                                    arg0.dismiss();
                                });
                                android.app.AlertDialog alertDialog = dialog.create();
                                alertDialog.show();
                            } else {
                                if(isSharing){
                                    android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(context);
                                    dialog.setMessage(context.getString(R.string.send_to_room_x, item.getName()));
                                    dialog.setTitle(R.string.app_name);
                                    dialog.setNegativeButton(R.string.cancel, null);
                                    dialog.setPositiveButton(R.string.ok, (arg0, arg1) -> {
                                        arg0.dismiss();
                                        //chat voi nguoi da co tai khoan mbn
                                        MyUtils.chatWithUser(context, item, isForward);
                                        //dong man hinh search
                                        if (isForward) {
                                            context.sendBroadcast(new Intent(MH09_SearchActivity.ACTION_FINISH));
                                        }
                                    });
                                    android.app.AlertDialog alertDialog = dialog.create();
                                    alertDialog.show();

                                }else{
                                    //chat voi nguoi da co tai khoan mbn
                                    MyUtils.chatWithUser(context, item, isForward);
                                    //dong man hinh search
                                    if (isForward) {
                                        context.sendBroadcast(new Intent(MH09_SearchActivity.ACTION_FINISH));
                                    }
                                }

                            }


//                        } else {
//                            MyUtils.showAlertDialog(context, R.string.contact_have_not_mobile_phone);
//                        }
                        }

                    }
                });

                holder.linearRoot.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        //neu nam trong contact chat thi moi hien,
                        if (item.isInChatContact()) {
                            showActionMenu(holder.txt1, item, position);
                            return true;
                        } else {
                            return false;
                        }
                    }
                });

                holder.btnInvite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.linearRoot.performClick();
                    }
                });

                if (!item.isHaveMBNAccount()) {
                    holder.img2.setVisibility(View.GONE);
                    holder.img3.setVisibility(View.GONE);
                    holder.btnInvite.setVisibility(View.VISIBLE);
                } else {
                    holder.img3.setVisibility(View.VISIBLE);
                    holder.btnInvite.setVisibility(View.GONE);

                    //KO HIEN NUT XOA
                    holder.img2.setVisibility(View.GONE);
                    //xoa khoi danh ba
                    holder.img2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(context);
                            alertDialogBuilder.setMessage(R.string.confirm_delete_contact);
                            alertDialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                            alertDialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    arg0.dismiss();

                                    if (context != null) {
                                        Intent intent = new Intent(Contact_Fragment_3_All.ACTION_REMOVE_CONTACT);
                                        intent.putExtra(UserInfo.USER_INFO, item);
                                        context.sendBroadcast(intent);
                                    }
                                }
                            });

                            android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                        }
                    });

            /*holder.linearRoot.setOnClickListener(new ChatView.OnClickListener() {
                @Override
                public void onClick(ChatView v) {
                    //chat voi nguoi da co tai khoan mbn
                    MyUtils.chatWithUser(context, item, owner.get_id());
                }
            });*/

                    if (owner != null && !owner.get_id().equals(item.get_id())) {
                        holder.img3.setVisibility(View.VISIBLE);
                        holder.img3.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //vào màn hình chi tiết owner
                                Intent intent = new Intent(context, ChatUserDetailActivity.class);
                                intent.putExtra(UserInfo.USER_INFO, item);
                                context.startActivity(intent);
                            }
                        });
                    } else {
                        holder.img3.setVisibility(View.GONE);
                    }

                }
            }
        } else if (rootHolder instanceof HeaderHolder) {
            HeaderHolder holder = (HeaderHolder) rootHolder;
            if (item != null && item.isHeader()) {
                holder.img1.setImageResource(item.getHeaderIcon());
                holder.txt1.setText(item.getHeaderString());
            }
        }


    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    //////////////////////////////////////////////////////////////////////////////////////////////


    /////////////////////////////////////////////////////////////////////////////////
    SortedSet<Character> characters;

    public void initHeadersLetters() {
        //sap theo thu tu tieng viet
//        Collator collator = Collator.getInstance(new Locale("vi"));
        characters = new TreeSet<>();
        if (null != items) {
            for (UserInfo item : items) {
                if (!TextUtils.isEmpty(item.getName())) {
                    char c = item.getName().charAt(0);
                    c = Character.toUpperCase(c);
                    c = MyUtils.getUnsignedChar(c);
                    characters.add(c);
                }
            }


            setHeadersLetters(characters);

        }
    }

    public Set<Character> getHeadersLetters() {
        initHeadersLetters();
        return characters;
    }

    public void setHeadersLetters(SortedSet<Character> headersLetters) {
        this.characters = headersLetters;
    }
    /////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////////////////////////////////////////////////////////////////
    private ArrayList<UserInfo> root = new ArrayList<UserInfo>();

    public void beginSearch() {//luu lai danh sach
        if (root != null && root.size() > 0) {
            root.clear();
        } else {
            root = new ArrayList<>();
        }

        for (UserInfo item : items) {
            if (!item.isHeader()) {
                root.add(item);
            }

        }
    }

    public void filter(String query) {

        /*new AsyncTask<Void,Void, Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                ArrayList<UserInfo> temp=new ArrayList<UserInfo>();
                for (int i = 0; i < root.size(); i++) {
                    UserInfo arr=root.get(i);
                    String name = arr.getNameUnsigned();//MyUtils.getUnsignedString(arr.getName());
                    if(name.toLowerCase().contains(query.toLowerCase())||
                            arr.getPhone().contains(query.toLowerCase())){
                        temp.add(arr);
                    }
                }

                entityList=new ArrayList<UserInfo>();
                entityList.addAll(temp);

                sortList();
                initHeadersLetters();

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                notifyDataSetChanged();
            }
        }.execute();*/

        ArrayList<UserInfo> temp = new ArrayList<UserInfo>();
        query = MyUtils.getUnsignedString(query);
        query = query.toLowerCase();
        for (int i = 0; i < root.size(); i++) {
            UserInfo arr = root.get(i);
            String name = (arr.getName() == null) ? "" : MyUtils.getUnsignedString(arr.getName().toLowerCase());
            String nameMBN = (arr.getNameMBN() == null) ? "" : MyUtils.getUnsignedString(arr.getNameMBN().toLowerCase());
            String phone = (arr.getPhone() == null) ? "" : arr.getPhone().toLowerCase();
            String email = (arr.getEmail() == null) ? "" : arr.getEmail().toLowerCase();
            if (name.contains(query) ||
                    nameMBN.contains(query) ||
                    phone.contains(query) ||
                    email.contains(query)
            ) {
                temp.add(arr);
            }
        }

        items = new ArrayList<UserInfo>();
        items.addAll(temp);

        //BAO CHO FRAGMENT SEARCH ALL
        EventBus.getDefault().post(new EventContact(items, items.size()));
        //SET TITLE
        Intent intent = new Intent(MH09_SearchActivity.ACTION_SET_TAB_TITLE_SEARCH);
        intent.putExtra(MH09_SearchActivity.ACTION_GO_TO_POSITION_SEARCH, 1);
        int size = items.size();
        if (TextUtils.isEmpty(query)) {//ko co tu khoa thi ko set so luong
            size = 0;
        }
        intent.putExtra(MH09_SearchActivity.SEARCH_NUMBER_FOUND, size);
        context.sendBroadcast(intent);

//        sortList();
//        initHeadersLetters();
        notifyDataSetChanged();


    }


    public void endSearch() {//reset ve danh sach ban dau
        if (items.size() < root.size()) {
           /* new AsyncTask<Void,Void, Void>(){
                @Override
                protected Void doInBackground(Void... voids) {
                    entityList=new ArrayList<>();
                    entityList.addAll(root);

                    sortList();
                    initHeadersLetters();

                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    notifyDataSetChanged();
                }
            }.execute();*/

//            setData(root);

            items.clear();
            items.addAll(root);

//            sortList();
//            initHeadersLetters();
            notifyDataSetChanged();
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    public int getPosition(Character letter) {
        int position = -1;
        if (items != null && items.size() > 0) {
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i).getName().charAt(0) == letter) {
                    position = i;
                    break;
                }
            }
        }
        return position;
    }


    //////////////////////////////////////////////////////////////////////////////////////////////
    private void showActionMenu(View view, final UserInfo user, int position) {
        PopupMenu popup = new PopupMenu(context, view);
        //app:showAsAction="ifRoom|withText" NOT WORKING
        try {
            Field[] fields = popup.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popup);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Menu menu = popup.getMenu();
        popup.getMenuInflater().inflate(R.menu.menu_action_pin_contact, menu);

        //neu la pin adapter thi khong co copy va forward tin
        if (user.isPin()) {
            menu.findItem(R.id.action_1).setVisible(false);//pin
            menu.findItem(R.id.action_2).setVisible(true);//unpin
        } else {
            menu.findItem(R.id.action_1).setVisible(true);//pin
            menu.findItem(R.id.action_2).setVisible(false);//unpin
        }


        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.action_1) {
                    setPinContact(true, user.get_id(), position);
                } else if (itemId == R.id.action_2) {
                    setPinContact(false, user.get_id(), position);
                }
                return true;
            }
        });
        popup.show();
    }

    private void setPinContact(final boolean isPin, String userId, int position) {
        Socket socket = ChatApplication.Companion.getSocket();
        if (socket != null && socket.connected() && !TextUtils.isEmpty(userId)) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("contactId", userId);
            } catch (Exception e) {
                e.printStackTrace();
            }

            socket.emit(isPin ? "pinContact" : "unpinContact", obj, (Ack) args -> {
                // socket return sasUrl, sasThumbUrl (nếu là image)
                Realm realm = ChatApplication.Companion.getRealmChat();
                realm.beginTransaction();
                UserInfo item = realm.where(UserInfo.class).equalTo("_id", userId).findFirst();
                if (item != null) {
                    item.setPin(isPin);
                }
                realm.commitTransaction();

                //danh sach sap xep section ben ngoai nen ko notifydatasetchange dc, phai tao lai
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //de cac man hinh search tu doi trang thai sau khi pin/unpin
                        items.get(position).setPin(isPin);
                        notifyItemChanged(position);
                        MyUtils.showToast(context, R.string.success);
                    }
                });

                //load lai danh sach contact chat
                context.sendBroadcast(new Intent(Contact_Fragment_1_Chat.ACTION_LOAD_CONTACT_HAVE_MBN_ACCOUNT));


            });
        }
    }


}