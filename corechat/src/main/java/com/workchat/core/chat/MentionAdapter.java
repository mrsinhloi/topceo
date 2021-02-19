package com.workchat.core.chat;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.workchat.core.models.chat.Member;
import com.workchat.core.models.realm.UserChat;
import com.workchat.core.utils.MyUtils;
import com.workchat.corechat.R;

import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class MentionAdapter extends ArrayAdapter<Member> implements Filterable {

    private ArrayList<Member> root = new ArrayList<>();
    private ArrayList<Member> data = new ArrayList<>();

    private LayoutInflater inflater;
    private Context context;
    private int size = 40;
    public MentionAdapter(Context context, int resource, ArrayList<Member> members) {
        super(context, resource);

        /*try {
            long owner = ChatApplication.Companion.getUser().get_id();
            //mention bo minh ra
            for (int i = 0; i < members.size(); i++) {
                if(members.get(i).getUserId()==owner){
                    members.remove(i);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/



        this.data = new ArrayList<>(members);
        this.root = new ArrayList<>(members);

        //add @All
        Member all = new Member();
        all.setUserId("0");
        UserChat info = new UserChat();
        info.setUserId("0");
        info.setName("All");
        info.setEmail("everyone");
        all.setUserInfo(info);
        data.add(0, all);
        root.add(0, all);

        inflater = (LayoutInflater)context.getSystemService( Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        size = getContext().getResources().getDimensionPixelSize(R.dimen.avatar_size_medium);

        getFilter();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Member getItem(int arg0) {
        return data.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    public ArrayList<Member> getMembers() {
        return root;
    }

    class UserHolder {
        ImageView img1;
        TextView txt1;
        TextView txt2;
    }

    @Override
    public View getView(int arg0, View v, ViewGroup arg2) {
        UserHolder holder = null;
        if (v == null) {
            holder = new UserHolder();
            v = inflater.inflate(R.layout.mention_user_row_name_and_phone, null);
            holder.img1 = (ImageView) v.findViewById(R.id.imageView1);
            holder.txt1 = (TextView) v.findViewById(R.id.textView1);
            holder.txt2 = (TextView) v.findViewById(R.id.textView2);

            v.setTag(holder);
        } else {
            holder = (UserHolder) v.getTag();

        }

        final Member user = data.get(arg0);

        if (user != null && user.getUserInfo()!=null) {
            //set name va email
            holder.txt1.setText(user.getUserInfo().getName());
            if (!TextUtils.isEmpty(user.getUserInfo().getEmail())) {
                holder.txt2.setText(user.getUserInfo().getEmail());
                holder.txt2.setVisibility(View.VISIBLE);
            }else if (!TextUtils.isEmpty(user.getUserInfo().getPhone())) {
                holder.txt2.setText(user.getUserInfo().getPhone());
                holder.txt2.setVisibility(View.VISIBLE);
            }else{
                holder.txt2.setText("");
                holder.txt2.setVisibility(View.GONE);
            }


            String avatar = user.getUserInfo().getAvatar();
            if (!TextUtils.isEmpty(avatar)) {
                Picasso.get()
                        .load(avatar)
                        .resize(size, size)
                        .centerCrop()
                        .placeholder(R.drawable.ic_user_2)
                        .transform(new CropCircleTransformation())
                        .into(holder.img1);
            } else {
                Picasso.get()
                        .load(R.drawable.ic_user_2)
                        .resize(size, size)
                        .centerCrop()
                        .transform(new CropCircleTransformation())
                        .into(holder.img1);
            }

        }

        return v;
    }

    private ValueFilter valueFilter;

    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }

    private class ValueFilter extends Filter {


        //Invoked in a worker thread to filter the data according to the constraint.
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults results = new FilterResults();

            if (constraint != null && constraint.length() > 0 && constraint.toString().contains("@")) {

                ArrayList<Member> filters = new ArrayList<Member>();
                String value = constraint.toString().substring(1).toLowerCase();//@Phuong => Phuong

                for (int i = 0; i < root.size(); i++) {

                    Member item = root.get(i);
                    if (item != null && item.getUserInfo()!=null) {
                        String name = item.getUserInfo().getName().toLowerCase();
                        String phone = item.getUserInfo().getPhone().toLowerCase();

                        //khong dau
                        String unsigned = MyUtils.getUnsignedString(name);
                        boolean condition1 = unsigned.contains(value);

                        //co dau
                        boolean condition2 = name.contains(value);

                        //so dt
                        boolean condition3 = phone.contains(value);

                        if (condition1 || condition2 || condition3) {
                            if (!filters.contains(item)) {
                                filters.add(item);
                            }
                        }
                    }

                }

                results.count = filters.size();
                results.values = filters;

            } else {

                results.count = 0;//mentionUsers.size();
                results.values = new ArrayList<Member>();//mentionUsers;

            }

            return results;
        }


        //Invoked in the UI thread to publish the filtering results in the user interface.
        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {

            Object obj = results.values;
            if (obj != null) {
                data = (ArrayList<Member>) obj;
                notifyDataSetChanged();
            }


        }
    }
}
