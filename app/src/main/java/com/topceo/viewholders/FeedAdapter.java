package com.topceo.viewholders;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.error.ANError;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.topceo.R;
import com.topceo.ads.AdConfigModel;
import com.topceo.ads.AdUtils;
import com.topceo.ads.AdsAppModel;
import com.topceo.config.MyApplication;
import com.topceo.fragments.Fragment_1_Home_Admin;
import com.topceo.fragments.Fragment_1_Home_SonTung;
import com.topceo.fragments.Fragment_1_Home_User;
import com.topceo.group.GroupHorizontalAdapter_ViewHolder;
import com.topceo.objects.image.ImageItem;
import com.topceo.objects.other.User;
import com.topceo.services.Webservices;
import com.topceo.utils.MyUtils;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSettings;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdsManager;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import bolts.Continuation;
import bolts.Task;

public class FeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public ArrayList<ImageItem> mDataset = new ArrayList<>();
    public boolean isActivityStop = false;

    public void stopPlayVideo(boolean isActivityStop) {
        this.isActivityStop = isActivityStop;
        //KHI STOP THI MOI NOTIFY LAI ADAPTER, KHI RESUME THI CHI SET LAI BIEN VE FALSE KO UPDATE ADAPTER
        if (isActivityStop) {
            //stop all
            notifyDataSetChanged();
        }

    }

    @Override
    public int getItemViewType(int position) {
        ImageItem item = mDataset.get(position);
//        return item.getTypeView();
        if (item.getTypeView() == ImageItem.TYPE_VIEW_ITEM) {
            //phan ra thanh 2 loai INSTA/FACEBOOK
            if (ImageItem.ITEM_TYPE_FACEBOOK.equals(item.getItemType())) {
                //neu chi co 1 video thi hien thi theo instagram video
                if (item.getItemContent().size() == 1 && item.isVideo()) {
                    item.setItemType(ImageItem.ITEM_TYPE_INSTAGRAM);
                    return ImageItem.TYPE_INSTAGRAM_VIDEO;
                }
                return ImageItem.TYPE_FACEBOOK;
            } else {
//                String itemType = item.getItemContentType();
                if (item.isVideo()) {
                    return ImageItem.TYPE_INSTAGRAM_VIDEO;
                } else {
                    return ImageItem.TYPE_INSTAGRAM_IMAGE;
                }
            }
        } else {
            return item.getTypeView();
        }
    }


    // Provide a suitable constructor (depends on the kind of dataset)
    private int avatarSize = 0;
    private int widthImage = 0;

    private String device_id = "";
    private boolean isHaveAds = false;
    private int roundCorner = 10;
    private RequestOptions options;

    private Context context;
    private boolean isOwnerProfile = true;
    public User owner;

    public FeedAdapter(Context context, boolean isOwnerProfile, User owner) {
        this.context = context;
        avatarSize = context.getResources().getDimensionPixelSize(R.dimen.avatar_size_small);
        this.isOwnerProfile = isOwnerProfile;
        this.owner = owner;
        roundCorner = context.getResources().getDimensionPixelOffset(R.dimen.margin_image);
        widthImage = MyUtils.getScreenWidth(context);// - roundCorner * 2;

        options = new RequestOptions()
//                .transform(new RoundedCorners(roundCorner))
                .priority(Priority.HIGH)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);

        //id_test
        /*String android_id = MyUtils.getIMEI(context);
        device_id = MyUtils.md5_Admod(android_id).toUpperCase();
        if (isHaveAds && isAdmob) {
            initAdmobAds();
        }*/
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == ImageItem.TYPE_SUGGEST) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_1_suggest, parent, false);
            // set the view's size, margins, paddings and layout parameters
            ViewHolder2 vh = new ViewHolder2(v, context);
            return vh;
        } else if (viewType == ImageItem.TYPE_GROUP) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_1_group_horizontal, parent, false);
            // set the view's size, margins, paddings and layout parameters
            GroupHorizontalAdapter_ViewHolder vh = new GroupHorizontalAdapter_ViewHolder(v, context);
            return vh;
        } else if (viewType == ImageItem.TYPE_ADD_POST) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_add_post, parent, false);
            // set the view's size, margins, paddings and layout parameters
            ViewHolder7 vh = new ViewHolder7(v, groupId);
            return vh;
        } else if (viewType == ImageItem.TYPE_ADS) {
            if (isAdmob) {
                // create a new view
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_1_ads_admob, parent, false);
                // set the view's size, margins, paddings and layout parameters
                ViewHolder3 vh = new ViewHolder3(v);

                return vh;
            } else {

                // create a new view
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_1_ads_facebook, parent, false);
                // set the view's size, margins, paddings and layout parameters
                ViewHolder4 vh = new ViewHolder4(v);
                return vh;
            }

        } else {//mac dinh tra ve default la image
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_1_row_basic, parent, false);
            if (viewType == ImageItem.TYPE_FACEBOOK) {
                ViewHolder5_Facebook vh = new ViewHolder5_Facebook(v, avatarSize);
                return vh;
            } else if (viewType == ImageItem.TYPE_INSTAGRAM_VIDEO) {
                ViewHolder6_Instagram_Video vh = new ViewHolder6_Instagram_Video(v, avatarSize, widthImage);
                return vh;
            } else {//image
                // set the view's size, margins, paddings and layout parameters
                ViewHolder6_Instagram_Image vh = new ViewHolder6_Instagram_Image(v, avatarSize, widthImage);
                return vh;
            }
        }
    }


    ViewHolder5_Facebook holderFacebook;
    ViewHolder6_Instagram_Image holderImage;
    ViewHolder6_Instagram_Video holderVideo;

    ViewHolder2 holder2;
    ViewHolder3 holder3;
    ViewHolder4 holder4;
    GroupHorizontalAdapter_ViewHolder holder5;
    ViewHolder7 holder7;

    // Replace the contents of a view (invoked by the layout manager)
    private AdView adView;
    private String native_admob_id = "";

    boolean isDebug = true;
    private void initAdmobAds() {
        if (isDebug) {
            native_admob_id = "ca-app-pub-3940256099942544/6300978111";
        }
        if (!TextUtils.isEmpty(native_admob_id)) {

            //create ads
            adView = new AdView(context);
            adView.setAdSize(AdSize.MEDIUM_RECTANGLE);
            adView.setAdUnitId(native_admob_id);

            //create ads request
            // Create an ad request.
            AdRequest.Builder adRequestBuilder = new AdRequest.Builder();

            if (isDebug) {
                // Optionally populate the ad request builder.
                adRequestBuilder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
            }

            adView.loadAd(adRequestBuilder.build());


        }

    }

    @Override
    public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);

        if (holder instanceof ViewHolder6_Instagram_Video) {
            ViewHolder6_Instagram_Video h1 = (ViewHolder6_Instagram_Video) holder;
            h1.stopVideo();
            h1.vvInfo.stop();
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holderRoot, final int position) {

        int type = getItemViewType(position);
        if (type == ImageItem.TYPE_SUGGEST) {
            holder2 = (ViewHolder2) holderRoot;
            getSuggestFollow(holder2);
        } else if (type == ImageItem.TYPE_GROUP) {
            holder5 = (GroupHorizontalAdapter_ViewHolder) holderRoot;
            holder5.bind(isRefresh, this);
            setRefresh(false);
        } else if (type == ImageItem.TYPE_ADD_POST) {
            holder7 = (ViewHolder7) holderRoot;
            holder7.bind(context, avatarSize, owner);
        } else if (type == ImageItem.TYPE_ADS) {
            if (isAdmob) {
                holder3 = (ViewHolder3) holderRoot;
                holder3.adContainer.removeAllViews();

                if (!TextUtils.isEmpty(native_admob_id)) {

                        /*//create ads
                        adView = new NativeExpressAdView(context);
                        adView.setAdSize(AdSize.MEDIUM_RECTANGLE);
                        adView.setAdUnitId(native_admob_id);

                        //create ads request
                        // Create an ad request.
                        AdRequest.Builder adRequestBuilder = new AdRequest.Builder();

                        if(BuildConfig.DEBUG){
                            // Optionally populate the ad request builder.
                            adRequestBuilder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
                        }*/

                    if (adView != null && adView.getParent() != null) {
                        ((ViewGroup) (adView.getParent())).removeView(adView);
                    }

                    if (adView != null) {
                        holder3.adContainer.addView(adView);
                    }

                        /*adView.loadAd(adRequestBuilder.build());
                        adView.setAdListener(new AdListener(){
                            @Override
                            public void onAdLoaded() {
                                super.onAdLoaded();
                                notifyItemChanged(position);
                            }
                        });*/

                }
            } else {
                //holder 4 facebook
                holder4 = (ViewHolder4) holderRoot;

                //set ads
                if (countFacebookAds > 0) {
                    holder4.linearRoot.setVisibility(View.VISIBLE);
                    NativeAd ads = manager.nextNativeAd();

                    holder4.txt1.setText(ads.getAdvertiserName());
                    holder4.txt2.setText("Sponsored");
                    holder4.txt3.setText(ads.getAdBodyText());
                    holder4.txt4.setText(ads.getAdSocialContext());

                    holder4.btn1.setText(ads.getAdCallToAction());

                    int height = ads.getAdCoverImage().getHeight();
                    holder4.mediaView1.getLayoutParams().height = height;

//                        holder4.mediaView1.setNativeAd(ads);//// Download and display the cover image.


                    // Download and display the ad icon.
//                        NativeAd.Image adIcon = ads.getAdIcon();
//                        NativeAd.downloadAndDisplayImage(adIcon, holder4.img1);

                    //ad choice
                    holder4.linear1.removeAllViews();
                    AdChoicesView adChoicesView = new AdChoicesView(context, ads, true);
                    holder4.linear1.addView(adChoicesView, 0);

                    // Register the Title and CTA button to listen for clicks.
                    List<View> clickableViews = new ArrayList<>();
                    clickableViews.add(holder4.txt1);
                    clickableViews.add(holder4.btn1);
//                        ads.registerViewForInteraction(holder4.linear1, clickableViews);
                    ads.registerViewForInteraction(holder4.linear1, holder4.mediaView1, holder4.native_ad_icon, clickableViews);
                } else {
                    holder4.linearRoot.setVisibility(View.GONE);
                }

            }

        } else {//type_image
            if (position < mDataset.size()) {
                final ImageItem item = mDataset.get(position);
                if (ImageItem.ITEM_TYPE_FACEBOOK.equals(item.getItemType())) {
                    holderFacebook = (ViewHolder5_Facebook) holderRoot;
                    holderFacebook.bindData(item, position, this);
                } else {
                    if (item.isVideo()) {
                        holderVideo = (ViewHolder6_Instagram_Video) holderRoot;
                        holderVideo.bindData(item, position, this);
                    } else {//image
                        holderImage = (ViewHolder6_Instagram_Image) holderRoot;
                        holderImage.bindData(item, position, this);
                    }

                }

            }
        }


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        int count = 0;
        if (mDataset != null && mDataset.size() > 0) {
            count = mDataset.size();
        }
        return count;
    }

    @Override
    public long getItemId(int position) {
        long id = 0;
        if (mDataset != null && mDataset.size() > 0) {
            id = mDataset.get(position).getImageItemId();
        }
        return id;
    }

    //add by mr.pham
    //clear all items
    public void clear() {
        mDataset.clear();
        notifyDataSetChanged();
    }

    public void add(int position, ImageItem item) {
        mDataset.add(position, item);

        //Man hinh chinh thi co goi y ket ban, va quang cao
        if (isOwnerProfile) {
            addItemSuggestAndAds();
        } else {//Chi hien bai dang cua user
            notifyItemInserted(position);
        }
    }


    public void remove(long itemId) {
        int position = -1;//= mDataset.indexOf(item);
        for (int i = 0; i < mDataset.size(); i++) {
            if (itemId == mDataset.get(i).getImageItemId()) {
                position = i;
                break;
            }
        }

        if (position != -1) {
            //neu o vi tri 0 thi goi load lai, vi co suggestion nam o vi tri 1 -> vi tri 0
            if (position == 0) {
                //todo check lai
                context.sendBroadcast(new Intent(Fragment_1_Home_Admin.ACTION_REFRESH));
                context.sendBroadcast(new Intent(Fragment_1_Home_SonTung.ACTION_REFRESH));
                context.sendBroadcast(new Intent(Fragment_1_Home_User.ACTION_REFRESH));

            } else {//neu o vi tri 2 tro di thi xoa binh thuong
                mDataset.remove(position);
                //Man hinh chinh thi co goi y ket ban, va quang cao
                if (isOwnerProfile) {
                    addItemSuggestAndAds();
                } else {//Chi hien bai dang cua user
                    notifyItemRemoved(position);
                }
            }
        }
    }

    //add list items
    public void addAll(ArrayList<ImageItem> list) {
        int size = mDataset.size();

        mDataset.addAll(list);
        //Man hinh chinh thi co goi y ket ban, va quang cao
        if (isOwnerProfile) {
            addItemSuggestAndAds();
        } else {//Chi hien bai dang cua user
            notifyItemRangeChanged(size, list.size());
        }

    }

    public void update(ArrayList<ImageItem> list) {
        mDataset = list;
        notifyDataSetChanged();
    }

    public void addAll(Collection<? extends ImageItem> list, int position) {
        mDataset.addAll(0, list);
        //Man hinh chinh thi co goi y ket ban, va quang cao
        if (isOwnerProfile) {
            addItemSuggestAndAds();
        } else {//Chi hien bai dang cua user
            notifyItemRangeChanged(0, list.size());
        }

    }

    public void addTop(Collection<? extends ImageItem> list) {
        int top = getTopPosition();
        mDataset.addAll(top, list);

        //Man hinh chinh thi co goi y ket ban, va quang cao
        if (isOwnerProfile) {
            addItemSuggestAndAds();
        } else {//Chi hien bai dang cua user
            notifyItemRangeChanged(0, list.size());
        }

    }

    public void replaceAll(ArrayList<ImageItem> list) {

        mDataset.clear();
        notifyDataSetChanged();

        mDataset.addAll(list);
        //Man hinh chinh thi co goi y ket ban, va quang cao
        if (isOwnerProfile) {
            addItemSuggestAndAds();
        } else {//Chi hien bai dang cua user
            notifyDataSetChanged();
        }
    }

    //CACH N PTU XUAT HIEN QUANG CAO
    private int DISTANCE_NUMBER_SHOW_AD = 10;

    private void addItemSuggestAndAds() {
        //vi tri 0 hien thi group
        if (isShowGroup()) {
            if (mDataset != null && mDataset.size() > 0) {
                //kiem tra neu chua co thi moi them vao
                ImageItem item = mDataset.get(0);
                if (item.getTypeView() != ImageItem.TYPE_GROUP) {
                    ImageItem suggest = new ImageItem();
                    suggest.setTypeView(ImageItem.TYPE_GROUP);
                    mDataset.add(0, suggest);
                }
            } else {
                ImageItem suggest = new ImageItem();
                suggest.setTypeView(ImageItem.TYPE_GROUP);
                mDataset.add(0, suggest);
            }
        }

        if (isCanPost()) {
            //co nhom thi item nay index la 1, nguoc lai la 0
            int position = isShowGroup() ? 1 : 0;
            if (mDataset != null && mDataset.size() > 0 && position < mDataset.size()) {
                //kiem tra neu chua co thi moi them vao
                ImageItem item = mDataset.get(position);
                if (item.getTypeView() != ImageItem.TYPE_ADD_POST) {
                    ImageItem suggest = new ImageItem();
                    suggest.setTypeView(ImageItem.TYPE_ADD_POST);
                    mDataset.add(position, suggest);
                }
            } else {
                ImageItem suggest = new ImageItem();
                suggest.setTypeView(ImageItem.TYPE_ADD_POST);
                mDataset.add(position, suggest);
            }
        }

        if (mDataset != null && mDataset.size() > 0) {
            // position 1 cho goi y ban be, kiem tra chua co thi moi them vao
            if (isShowSuggest()) {
                if (mDataset.size() > 1) {
                    ImageItem item = mDataset.get(1);
                    if (item.getTypeView() != ImageItem.TYPE_SUGGEST) {
                        ImageItem suggest = new ImageItem();
                        suggest.setTypeView(ImageItem.TYPE_SUGGEST);
                        mDataset.add(1, suggest);
                    }
                }

            }
        }
        notifyDataSetChanged();
    }


    public ImageItem getLastestItem() {
        ImageItem item = null;
        if (mDataset.size() > 0) {
            item = mDataset.get(mDataset.size() - 1);
        }
        return item;
    }

    public int getTopPosition() {
        int index = 0;
        for (int i = 0; i < mDataset.size(); i++) {
            ImageItem item = mDataset.get(i);
            if (item.getImageItemId() > 0) {
                index = i;
                break;
            }
        }
        return index;
    }

    public int getGroupPosition() {
        int index = -1;
        for (int i = 0; i < mDataset.size(); i++) {
            ImageItem item = mDataset.get(i);
            if (item.getTypeView() == ImageItem.TYPE_GROUP) {
                index = i;
                break;
            }
        }
        return index;
    }

    public void removeGroup() {
        int pos = getGroupPosition();
        if (pos >= 0) {
            mDataset.remove(pos);
            notifyDataSetChanged();
        }
    }

    public void addGroup() {
        int pos = getGroupPosition();
        if (pos == -1) {
            if (isOwnerProfile) {
                addItemSuggestAndAds();
            }
        }
    }

    public ImageItem getTopItem() {
        ImageItem item = null;
        if (mDataset.size() > 0) {
            item = mDataset.get(getTopPosition());
        }
        return item;
    }

    public void updateItemLikeCount(int likeCount, int position) {
        mDataset.get(position).setLikeCount(likeCount);
        notifyItemChanged(position);
    }

    public void replaceItem(ImageItem item) {
        int pos = -1;
        for (int i = 0; i < mDataset.size(); i++) {
            if (mDataset.get(i).getImageItemId() == item.getImageItemId()) {
                pos = i;
                break;
            }
        }

        if (pos >= 0) {
                /*mDataset.remove(pos);
                notifyItemRemoved(pos);
                mDataset.add(pos, item);
                notifyItemInserted(pos);

                notifyItemChanged(pos);*/
            mDataset.set(pos, item);
            notifyItemChanged(pos);
        }


    }

    public void updateItemDescription(ImageItem item) {
        int pos = -1;
        for (int i = 0; i < mDataset.size(); i++) {
            if (mDataset.get(i).getImageItemId() == item.getImageItemId()) {
                pos = i;
                break;
            }
        }

        if (pos >= 0) {
            mDataset.get(pos).setDescription(item.getDescription());
            notifyItemChanged(pos);
            notifyDataSetChanged();
        }


    }

    public void updateNumberComment(long imageItemId, int numberComment) {
        int pos = -1;
        for (int i = 0; i < mDataset.size(); i++) {
            if (mDataset.get(i).getImageItemId() == imageItemId) {
                pos = i;
                break;
            }
        }

        if (pos >= 0) {
            mDataset.get(pos).setCommentCount(numberComment);
            notifyItemChanged(pos);
            notifyDataSetChanged();
        }


    }

    ////////////////////////////////////////////////////
    private void getSuggestFollow(final ViewHolder2 holder2) {
        if (MyUtils.checkInternetConnection(context)) {
            Webservices.getSuggestFollow(false).continueWith(new Continuation<Object, Void>() {
                @Override
                public Void then(Task<Object> task) throws Exception {
                    if (task.getError() == null) {
                        if (task.getResult() != null) {
                            ArrayList<User> list = (ArrayList<User>) task.getResult();
                            if (list != null && list.size() > 0) {
                                holder2.suggestAdapter.clear();
                                holder2.suggestAdapter.addAll(list);
                            }

                            if (holder2.suggestAdapter.getItemCount() > 0) {
//                                    holder2.rvHorizontal.setVisibility(View.VISIBLE);
//                                    holder2.linearSeeMore.setVisibility(View.VISIBLE);
                                holder2.linearSuggestRoot.setVisibility(View.VISIBLE);
                            } else {
//                                    holder2.rvHorizontal.setVisibility(View.GONE);
//                                    holder2.linearSeeMore.setVisibility(View.GONE);
                                holder2.linearSuggestRoot.setVisibility(View.GONE);
                            }
                        } else {
                            //khong co danh sach thi an view suggest
//                                holder2.linearSuggestRoot.setVisibility(View.GONE);
                        }
                    } else {
                        boolean isLostCookie = MyApplication.controlException((ANError) task.getError());
                        if (isLostCookie) {
                            MyApplication.initCookie(context).continueWith(new Continuation<Object, Void>() {
                                @Override
                                public Void then(Task<Object> task) throws Exception {
                                    if (task.getResult() != null) {
                                        User kq = (User) task.getResult();
                                        if (kq != null) {
                                            getSuggestFollow(holder2);
                                        }
                                    }
                                    return null;
                                }
                            });
                        } else {
                            if (!TextUtils.isEmpty(task.getError().getMessage())) {
                                MyUtils.showToastDebug(context, task.getError().getMessage());
                            }
                        }
                    }

//                    setRefresh(false);
                    return null;
                }
            });
        } else {
            MyUtils.showThongBao(context);
        }

    }


    ///////////////////////////////////////////////////////////////////////////
    private boolean isEpapersmart = false;
    private boolean isAdmob = true;//admob or facebook
    private String native_facebook_id = "";

    private void setUpQuangCao(Context c) {

        isHaveAds = false;

        //fullscreen
        Object obj = AdUtils.loadListFromFile(c, AdUtils.OBJECT_ADS);
        if (obj != null) {
            AdsAppModel adsProviderModel = (AdsAppModel) obj;
            if (adsProviderModel != null) {

                //native
                AdConfigModel adNative = adsProviderModel.getNative();
                if (adNative != null) {
                    //admob
                    if (adNative.getAdNetworkId() == AdConfigModel.AD_NETWORK_ADMOB) {
                        String native_ad_unit_id = adNative.getExternalAd().getAdUnitId();
                        if (!TextUtils.isEmpty(native_ad_unit_id)) {
                            isHaveAds = true;
                            isAdmob = true;
                            native_admob_id = native_ad_unit_id;

                        }
                    }

                    //facebook
                    if (adNative.getAdNetworkId() == AdConfigModel.AD_NETWORK_FACEBOOK) {
                        String native_ad_unit_id = adNative.getExternalAd().getAdUnitId();
                        if (!TextUtils.isEmpty(native_ad_unit_id)) {
                            isHaveAds = true;
                            isAdmob = false;
                            native_facebook_id = native_ad_unit_id;
                            initNativeAd(native_facebook_id);
                        }
                    }


                }
            }
        }


    }

    private NativeAdsManager manager;
    private int countFacebookAds = 0;

    private void initNativeAd(String unitId) {
        if (!TextUtils.isEmpty(unitId)) {
            if (isDebug) {
                AdSettings.addTestDevice("77c85917a6e881dad0fa2bf972b8e29d");
            }
            countFacebookAds = 0;
            manager = new NativeAdsManager(context, unitId, 10);
            manager.loadAds();
            manager.setListener(new NativeAdsManager.Listener() {
                @Override
                public void onAdsLoaded() {
//                    nativeAd = manager.nextNativeAd();
                    countFacebookAds = manager.getUniqueNativeAdCount();

                }

                @Override
                public void onAdError(AdError adError) {
                    // Ad error callback
                    MyUtils.log(adError.getErrorMessage());
                }
            });
        }

    }


    ///////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////


    /////////////////////////////////////////////////////////
    public boolean isShowSuggest() {
        return isShowSuggest;
    }

    public void setShowSuggest(boolean showSuggest) {
        isShowSuggest = showSuggest;
    }

    private boolean isShowSuggest = false;
    private boolean isShowGroup = false;
    private boolean isRefresh = false;
    private boolean isCanPost = false;

    public boolean isCanPost() {
        return isCanPost;
    }

    long groupId = -1;

    public void setCanPost(boolean canPost, long groupId) {
        isCanPost = canPost;
        isOwnerProfile = true;
        this.groupId = groupId;
        notifyDataSetChanged();
    }

    public boolean isRefresh() {
        return isRefresh;
    }

    public void setRefresh(boolean refresh) {
        isRefresh = refresh;
    }

    public boolean isShowGroup() {
        return isShowGroup;
    }

    public void setShowGroup(boolean showGroup) {
        isShowGroup = showGroup;
    }

    public void updateItemShort(ArrayList<ImageItem> list) {
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                ImageItem item = list.get(i);

                int position = findPosition(item.getImageItemId());
                if (position >= 0) {
                    updateItemShort(position, item);
                }

            }
        }
    }

    public int findPosition(long id) {
        int position = -1;
        if (mDataset != null && mDataset.size() > 0) {
            for (int i = 0; i < mDataset.size(); i++) {
                if (mDataset.get(i).getImageItemId() == id) {
                    position = i;
                    break;
                }
            }
        }
        return position;
    }

    private void updateItemShort(int position, ImageItem item) {
        if (mDataset != null && position < mDataset.size()) {
            //neu khac thi moi doi
            ImageItem item2 = mDataset.get(position);
            if (!item2.getDescription().equalsIgnoreCase(item.getDescription()) ||
                    item2.getCommentCount() != item.getCommentCount() ||
                    item2.getLikeCount() != item.getLikeCount() ||
                    item2.getShareCount() != item.getShareCount()
            ) {
                mDataset.get(position).setLikeCount(item.getLikeCount());
                mDataset.get(position).setCommentCount(item.getCommentCount());
                mDataset.get(position).setShareCount(item.getShareCount());
                mDataset.get(position).setDescription(item.getDescription());
                notifyItemChanged(position);


            }

        }
    }

    public void refreshGroup() {
        setRefresh(true);
        int pos = getGroupPosition();
        if (pos >= 0) {
            notifyItemChanged(pos);
        } else {
            //ko co group thi add group
            addGroup();
        }

    }


    ////////////////////////////////////////////////////////////////////////////////////////////


}
