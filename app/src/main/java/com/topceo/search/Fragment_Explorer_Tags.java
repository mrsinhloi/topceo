package com.topceo.search;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.topceo.R;
import com.topceo.fragments.EndlessRecyclerViewScrollListener;
import com.topceo.fragments.GlideCircleTransform;
import com.topceo.objects.other.SearchObject;
import com.topceo.services.ReturnResult;
import com.topceo.services.Webservices;
import com.topceo.utils.MyUtils;

import java.util.ArrayList;

import bolts.Continuation;
import bolts.Task;
import butterknife.BindView;
import butterknife.ButterKnife;


public class Fragment_Explorer_Tags extends Fragment {

    public Fragment_Explorer_Tags() {}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private Context context;
    @BindView(R.id.rv)RecyclerView rv;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_explorer_top, container, false);
        ButterKnife.bind(this, view);
        context = getActivity();

        initAdapter();
        registerReceiver();

        return view;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    private void initAdapter() {
        // use a linear layout manager
        LinearLayoutManager mLayoutManager;
        mLayoutManager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(mLayoutManager);
//        tv.setItemAnimator(new DefaultItemAnimator());
        rv.addOnScrollListener(new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list

            }
        });

        // specify an adapter (see also next example)
        mAdapter = new MyAdapter();
        rv.setAdapter(mAdapter);
    }


    private ArrayList<SearchObject> mDataset=new ArrayList<>();
    private MyAdapter mAdapter;

    /**
     * https://github.com/codepath/android_guides/wiki/Using-the-RecyclerView
     * https://github.com/wasabeef/recyclerview-animators
     * <p/>
     * notifyItemChanged(int pos)	Notify that item at position has changed.
     * notifyItemInserted(int pos)	Notify that item reflected at position has been newly inserted.
     * notifyItemRemoved(int pos)	Notify that items previously located at position has been removed from the data set.
     * notifyDataSetChanged()	    Notify that the dataset has changed. Use only as last resort.
     * rvContacts.scrollToPosition(0);   // index 0 position
     */
    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {



        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public TextView txt1, txt2;
            public ImageView img1;
            public LinearLayout linear1;


            public ViewHolder(View v) {
                super(v);
                txt1 = (TextView) v.findViewById(R.id.textView1);
                txt2 = (TextView) v.findViewById(R.id.textView2);
                img1 = (ImageView) v.findViewById(R.id.imageView1);
                img1.setLayoutParams(new LinearLayout.LayoutParams(avatarSize, avatarSize));
                linear1=(LinearLayout)v.findViewById(R.id.linearLayout1);

            }
        }




        public void remove(int position) {
            if (position >= 0) {
                mDataset.remove(position);
                notifyItemRemoved(position);
                notifyDataSetChanged();
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        private int avatarSize = 0;
        private int widthScreen = 0;

        public MyAdapter() {
            widthScreen = MyUtils.getScreenWidth(context);
            avatarSize = context.getResources().getDimensionPixelSize(R.dimen.avatar_size_medium);
        }

        // Create new views (invoked by the layout manager)
        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_explorer_top_row, parent, false);
            // set the view's size, margins, paddings and layout parameters
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            final SearchObject item = mDataset.get(position);

            if(item.getTypeId()==SearchObject.TYPE_HASH_TAG){
                Glide.with(context)
                        .load(R.drawable.ic_hashtag)//"http://d2i37wz5q98nd1.cloudfront.net/wp-content/uploads/2015/08/VCCircle_Sundar_Pichai.png")
                        .placeholder(R.drawable.ic_no_avatar)
                        .override(avatarSize, avatarSize)
                        .transform(new GlideCircleTransform(context))
                        .into(holder.img1);


                holder.txt1.setText("#"+item.getTitle());
                holder.txt2.setText(item.getDescription()+" "+getText(R.string.posts));
                holder.linear1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        MyUtils.gotoProfile(item.getTitle(),context);
//                        MyUtils.showToastDebug(context, "go to Hashtag");
                        MyUtils.gotoHashtag(item.getTitle(), context);
                    }
                });
            }else{//image
                if(!TextUtils.isEmpty(item.getImage()))
                    Glide.with(context)
                            .load(item.getImage())//"http://d2i37wz5q98nd1.cloudfront.net/wp-content/uploads/2015/08/VCCircle_Sundar_Pichai.png")
                            .placeholder(R.drawable.ic_no_avatar)
                            .override(avatarSize, avatarSize)
                            .transform(new GlideCircleTransform(context))
                            .into(holder.img1);


                holder.txt1.setText(item.getTitle());
                holder.txt2.setText(item.getDescription());
                holder.linear1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MyUtils.gotoProfile(item.getTitle(),context);
                    }
                });
            }






            /////////////////////////////////////////////////////////////////////////

        }


        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.size();
        }

        //add by mr.pham
        //clear all items
        public void clear() {
            mDataset.clear();
            notifyDataSetChanged();
        }

        //add list items
        public void addAll(ArrayList<SearchObject> list) {
            int size=mDataset.size();
            mDataset.addAll(list);
            notifyItemRangeInserted(size,list.size());

        }



    }
    /////////////////////////////////////////////////////////////////////////
    public void searchKeyword(String keyword){
//        MyUtils.hideKeyboard(getActivity());
        /*final ProgressDialog progressDialog = new ProgressDialog(context, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(context.getText(R.string.searching));
        progressDialog.show();*/
//        ProgressUtils.show(context);
        Webservices.searchTop(keyword, context, "search/tags").continueWith(new Continuation<Object, Void>() {
            @Override
            public Void then(Task<Object> task) throws Exception {

                if(task.getResult()!=null){
                    ReturnResult result=(ReturnResult)task.getResult();
                    if(result.getErrorCode()==ReturnResult.SUCCESS){
                        ArrayList<SearchObject> list=(ArrayList<SearchObject>)result.getData();
                        if(list!=null && list.size()>0){
                            mDataset.clear();
                            mDataset.addAll(list);
                            mAdapter.notifyDataSetChanged();
                        }else{
//                            MyUtils.showToast(context, R.string.not_found);
                            mDataset.clear();
                            mAdapter.notifyDataSetChanged();
                        }
                    }

                }

//               ProgressUtils.hide();

                return null;
            }
        });
    }

    /////////////////////////////////////////////////////////////////////////
    public static final String SEARCH_KEY_WORD="SEARCH_KEY_WORD";
    private BroadcastReceiver receiver;
    private void registerReceiver(){
        receiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(SEARCH_KEY_WORD)){
                    Bundle b=intent.getExtras();
                    if(b!=null){
                        String keyword=b.getString(SearchObject.KEY_WORD,"");
                        if(!TextUtils.isEmpty(keyword)){
                            searchKeyword(keyword);
                        }
                    }
                }
            }
        };
        getActivity().registerReceiver(receiver, new IntentFilter(SEARCH_KEY_WORD));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(receiver!=null && getActivity()!=null)getActivity().unregisterReceiver(receiver);
    }
    /////////////////////////////////////////////////////////////////////////

    /////////////////////////////////////////////////////////////////////////

    /////////////////////////////////////////////////////////////////////////

}
