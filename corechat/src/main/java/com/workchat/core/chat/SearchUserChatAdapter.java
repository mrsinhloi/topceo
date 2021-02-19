package com.workchat.core.chat;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.pchmn.materialchips.util.LetterTileProvider;
import com.squareup.picasso.Picasso;
import com.workchat.core.mbn.models.UserChatCore;
import com.workchat.core.utils.MyUtils;
import com.workchat.corechat.R;
import com.workchat.corechat.R2;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * Created by MrPhuong on 2016-10-31.
 */

public class SearchUserChatAdapter extends RecyclerView.Adapter<SearchUserChatAdapter.MyViewHolder> implements Filterable {

    private List<UserChatCore> listItems = new ArrayList<>();
    private List<UserChatCore> listRoot = new ArrayList<>();


    public class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R2.id.img1)
        CircleImageView img1;

        @BindView(R2.id.txt1)
        TextView txt1;
        @BindView(R2.id.txt2)
        TextView txt2;
        @BindView(R2.id.txt3)
        TextView txt3;
        @BindView(R2.id.linearRoot)
        LinearLayout linearRoot;
        @BindView(R2.id.linear2)
        LinearLayout linear2;


        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, width);
            img1.setLayoutParams(params);
            img1.setScaleType(ImageView.ScaleType.CENTER_CROP);

        }
    }

    public void setListItems(ArrayList<UserChatCore> list) {
        this.listItems = list;
    }

    public void addListItems(ArrayList<UserChatCore> list) {
        int sum = listItems.size();
        this.listItems.addAll(list);
        notifyItemRangeChanged(sum, list.size());
    }

    public void addItem(UserChatCore item, int position) {
        try {
            listItems.set(position, item);
            notifyItemChanged(position);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void addItem(UserChatCore item) {
        listItems.add(item);
        notifyDataSetChanged();

    }

    private int width = 0;
    private Context context;
    private UserChatCore user;
    // letter tile provider
    private LetterTileProvider mLetterTileProvider;

    public SearchUserChatAdapter(List<UserChatCore> list, Context context, UserChatCore user) {
        if (list == null) list = new ArrayList<>();
//        list = sortList(list);

        this.listItems = new ArrayList<>(list);
        this.listRoot = new ArrayList<>(list);
        this.context = context;
        width = context.getResources().getDimensionPixelOffset(R.dimen.avatar_size_medium);

        this.user = user;
        // letter tile provider
        mLetterTileProvider = new LetterTileProvider(context);

    }

    private ArrayList<UserChatCore> sortList(List<UserChatCore> userLists) {
        ArrayList<UserChatCore> list = new ArrayList<>(userLists);
        //sort theo thu tu alphabet, khong phan biet hoa thuong, khong phan biet co dau va khong dau
        MyUtils.sortListUserMBN(list);
        return list;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_search_user_chat_row, parent, false);

        return new SearchUserChatAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final SearchUserChatAdapter.MyViewHolder holder, final int position) {
        final UserChatCore item = listItems.get(position);

        //an thanh thich, tra loi
//        holder.linear4.setVisibility(ChatView.GONE);
        //set image
        if (!TextUtils.isEmpty(item.getAvatar())) {
            Picasso.get()
                    .load(item.getAvatar())
                    .centerCrop()
                    .resize(width, width)
                    .transform(new CropCircleTransformation())
                    .placeholder(R.drawable.icon_no_image)
                    .into(holder.img1);
        } else {
            String label = item.getFirstNameCharacter();
            if (label != null) {
                holder.img1.setImageBitmap(mLetterTileProvider.getLetterTile(label));
            } else holder.img1.setImageResource(R.drawable.icon_no_image);
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

        if(!TextUtils.isEmpty(phoneAndEmail)) {
            holder.txt2.setVisibility(View.VISIBLE);
            holder.txt2.setText(phoneAndEmail);
        }else{
            holder.txt2.setVisibility(View.GONE);
        }

        ////
        if (!TextUtils.isEmpty(item.getNameMBN())) {
            holder.txt3.setVisibility(View.VISIBLE);
            holder.txt3.setText(item.getNameMBN());
        } else {
            holder.txt3.setVisibility(View.GONE);
        }


        holder.linearRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(SearchUserChat2Activity.ADD_CHIP);
                intent.putExtra(UserChatCore.USER_MODEL, item);
                context.sendBroadcast(intent);

                //goi xong thi reset
//                getFilter().filter("");

            }
        });
        holder.txt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.linearRoot.performClick();
            }
        });
        holder.txt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.linearRoot.performClick();
            }
        });
        holder.linear2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.linearRoot.performClick();
            }
        });


    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                List<UserChatCore> filteredList = new ArrayList<>();

                if (TextUtils.isEmpty(charSequence)) {
                    filteredList.clear();
                    filteredList.addAll(listRoot);
                } else {

                    String query = charSequence.toString().toLowerCase();
                    query = MyUtils.getUnsignedString(query);

                    for (UserChatCore arr : listRoot) {
                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        String name = (arr.getName() == null) ? "" : MyUtils.getUnsignedString(arr.getName().toLowerCase());
                        String nameMBN = (arr.getNameMBN() == null) ? "" : MyUtils.getUnsignedString(arr.getNameMBN().toLowerCase());
                        String phone = (arr.getPhone() == null) ? "" : arr.getPhone().toLowerCase();
                        String email = (arr.getEmail() == null) ? "" : arr.getEmail().toLowerCase();

                        if (name.contains(query) ||
                                nameMBN.contains(query) ||
                                phone.contains(query) ||
                                email.contains(query)
                        ) {
                            filteredList.add(arr);
                        }


                    }

                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                ArrayList<UserChatCore> list = (ArrayList<UserChatCore>) filterResults.values;
                if (list != null && list.size() > 0) {
                    list = sortList(list);
                }

                listItems = new ArrayList<>(list);
                notifyDataSetChanged();
            }
        };
    }


    /////////////////////////////////////////////////////////////////////////////////
    SortedSet<Character> characters;

    public void initHeadersLetters() {
        //sap theo thu tu tieng viet
//        Collator collator = Collator.getInstance(new Locale("vi"));
        characters = new TreeSet<>();
        if (null != listItems) {
            for (UserChatCore item : listItems) {
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
    public List<UserChatCore> getItems() {
        return listItems;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    public int getPosition(Character letter) {
        int position = -1;
        if (listItems != null && listItems.size() > 0) {
            for (int i = 0; i < listItems.size(); i++) {
                if (listItems.get(i).getName().toUpperCase().charAt(0) == letter) {
                    position = i;
                    break;
                }
            }
        }
        return position;
    }

}
