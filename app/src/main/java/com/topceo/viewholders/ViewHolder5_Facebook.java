package com.topceo.viewholders;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.topceo.R;
import com.topceo.adapter.FeedAdapter;
import com.topceo.config.MyApplication;
import com.topceo.objects.image.ImageItem;
import com.topceo.objects.image.Item;
import com.topceo.objects.image.MyItemData;
import com.topceo.objects.image.LinkPreview;
import com.topceo.utils.MyUtils;
import com.smartapp.collage.CollageAdapterUrls;
import com.smartapp.collage.MediaLocal;
import com.smartapp.collage.OnItemClickListener;
import com.topceo.views.ShowMoreTextView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ViewHolder5_Facebook extends ViewHolderBasic {
    public ViewHolder5_Facebook(View v, int avatarSize) {
        super(v, avatarSize);
        initFacebookPost(v);
    }

    //multi image like facebook
    FrameLayout frameLayoutFacebook;
    LinearLayout linearDescription;
    ShowMoreTextView txtDescription;
    //text more
    RecyclerView rvCollage;

    //link preview
    RelativeLayout layoutPreviewLink;
    ImageView imgPreview;
    ImageView imgClosePreview;
    TextView txt1Preview;
    TextView txt2Preview;

    private void initFacebookPost(View view) {
        if (view != null) {
            View v = LayoutInflater.from(view.getContext()).inflate(R.layout.fragment_1_row_post_facebook, null);
            frameLayoutFacebook = (FrameLayout) v.findViewById(R.id.frameLayoutFacebook);
            linearDescription = (LinearLayout) v.findViewById(R.id.linearDescription);
            txtDescription = (ShowMoreTextView) v.findViewById(R.id.txtDescription);
            rvCollage = (RecyclerView) v.findViewById(R.id.rvCollage);

            //link preview
            layoutPreviewLink = (RelativeLayout) v.findViewById(R.id.layoutPreviewLink);
            imgPreview = (ImageView) v.findViewById(R.id.imgPreview);
            imgClosePreview = (ImageView) v.findViewById(R.id.imgClosePreview);
            imgClosePreview.setVisibility(View.GONE);
            txt1Preview = (TextView) v.findViewById(R.id.txt1Preview);
            txt2Preview = (TextView) v.findViewById(R.id.txt2Preview);

            //add to parent container
            linearContainer.addView(v);


        }
    }

    public void bindData(ImageItem item, int position, FeedAdapter adapter) {
        if (item != null) {

            //reset row line
            txtDescription.reset();
            txt5.reset();

            //init basic view
            initViewBasic(item, position, adapter);

            //show facebook post
            String description = item.getDescription();
            if (item.getItemContent() != null && item.getItemContent().size() > 0) {

                layoutPreviewLink.setVisibility(View.GONE);
                ArrayList<MediaLocal> list = Item.getMediaLocal(item.getItemContent(), item.isVideo());
                CollageAdapterUrls collageAdapter = new CollageAdapterUrls(context, list);
                rvCollage.setAdapter(collageAdapter);
                collageAdapter.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(@NotNull View view, int position) {
                        //open detail
//                        MyUtils.gotoDetailImage(context, item);

                        //vao gallery
                        MyUtils.openGallery(context, position, list);

                    }
                });
                rvCollage.setVisibility(View.VISIBLE);

                //neu co hinh thi text se o duoi
                //an description bottom
                txtDescription.setVisibility(View.GONE);
                linear3.setVisibility(View.VISIBLE);
                HolderUtils.setDescription(description, txt5, context);
                txt5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        if (txt5.getSelectionStart() == -1 && txt5.getSelectionEnd() == -1) {
                        if (!txt5.isClickMoreLess()) {
                            // do your code here this will only call if its not a hyperlink
                            MyUtils.gotoDetailImage(context, item);
                        }else {
                            //reset
                            txt5.setClickMoreLess(false);
                        }
                    }
                });


            } else {
                //an description bottom
                linear3.setVisibility(View.GONE);
                ivLike.setVisibility(View.GONE);
                txtDescription.setVisibility(View.VISIBLE);
                HolderUtils.setDescription(description, txtDescription, context);
                txtDescription.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

//                        if (txtDescription.getSelectionStart() == -1 && txtDescription.getSelectionEnd() == -1) {
                        if (!txtDescription.isClickMoreLess()) {
                            // do your code here this will only call if its not a hyperlink
                            MyUtils.gotoDetailImage(context, item);
                        }else{
                            //reset
                            txtDescription.setClickMoreLess(false);
                        }


                    }
                });


                rvCollage.setVisibility(View.GONE);
                //neu co link preview
                MyItemData data = item.getItemData();
                if (data != null) {
                    LinkPreview link = data.getLinkPreview();
                    if (link != null) {
                        String img = link.getImage();
                        String sitename = link.getSiteName();
                        String title = link.getTitle();
                        String url = link.getLink();

                        txt2Preview.setText(link.getCaption());
                        txt1Preview.setText(link.getTitle());

                        Glide.with(context)
                                .load(img)
                                .override(MyApplication.getInstance().screenWidth, MyApplication.getInstance().heightLinkPreview)
                                .centerCrop()
                                .into(imgPreview);

                        layoutPreviewLink.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                MyUtils.openWebPage(url, context);
                            }
                        });

                        layoutPreviewLink.setVisibility(View.VISIBLE);
                    } else {
                        layoutPreviewLink.setVisibility(View.GONE);
                    }
                } else {
                    layoutPreviewLink.setVisibility(View.GONE);
                }
            }

        }
    }


}
