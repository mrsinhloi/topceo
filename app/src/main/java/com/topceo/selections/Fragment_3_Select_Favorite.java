package com.topceo.selections;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.topceo.R;
import com.topceo.config.MyApplication;
import com.topceo.db.TinyDB;
import com.topceo.language.LocalizationUtil;
import com.topceo.selections.hashtags.Hashtag;
import com.topceo.selections.hashtags.HashtagCategory;
import com.topceo.selections.hashtags.HashtagShort;
import com.topceo.services.ReturnResult;
import com.topceo.services.Webservices;
import com.topceo.utils.MyUtils;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_3_Select_Favorite extends Fragment {

    public static Fragment_3_Select_Favorite newInstance(HashtagCategory category) {
        Fragment_3_Select_Favorite f = new Fragment_3_Select_Favorite();
        Bundle b = new Bundle();
        b.putParcelable(HashtagCategory.HASH_TAG_CATEGORY, category);
        f.setArguments(b);
        return f;
    }

    HashtagCategory category;

    TinyDB db;
    private boolean isEnglish = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            category = getArguments().getParcelable(HashtagCategory.HASH_TAG_CATEGORY);
        }
        db = new TinyDB(requireContext());
        isEnglish = !LocalizationUtil.INSTANCE.isVietnamese(requireContext());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Nullable
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.btnNext)
    TextView btnNext;
    @BindView(R.id.btnPrevious)
    TextView btnPrevious;

    @BindView(R.id.title)
    TextView title;
//    @BindView(R.id.subTitle)
//    TextView subTitle;

    @BindView(R.id.recyclerView)
    RecyclerView rv;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_7_select_topics, container, false);
        ButterKnife.bind(this, v);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initRecyclerView();
        setTitles();
        getHashtags();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            getHashtags();
        }
    }

    private int minSelect = 0;

    private void setTitles() {
        if (category != null) {
            title.setText(isEnglish ? category.getTitleEN() : category.getTitle());

            /*int min = category.getMinPick();
            int max = category.getMaxPick();
            String thongBao = getString(R.string.select_one_or_more_topic);
            if (min == 0 && max == 0) {//ko gioi han va co the chon hoac ko chon, co the bo qua
                //thông báo chọn 1 hoặc nhiều phần tử
                setStateButtonNext(true);
                minSelect = 0;
            } else {
                //min>0 hoac max>0 hoac min,max>0
                if (min > 0 && max == 0) {
                    //thông báo chọn ít nhất min phần tử
                    thongBao = getString(R.string.select_min_x_topic, min);
                    setStateButtonNext(false);
                    minSelect = min;
                } else if (min == 0 && max > 0) {
                    //thông báo chọn tối đa max phần tử
                    thongBao = getString(R.string.select_max_x_topic, max);
                    setStateButtonNext(true);
                    minSelect = 0;
                } else {//if (min > 0 && max > 0) { hoac min==max

                    //trường hợp người dùng set sai: min=2,max=1 thì cho max bằng min, xem như chọn tối đa max
                    //neu set max<min thi lay theo min
                    if (max < min) {
                        max = min;
                    }
                    if (min == max) {
                        //thông báo chọn tối đa max phần tử
                        thongBao = getString(R.string.select_max_x_topic, max);
                        setStateButtonNext(false);//phai chon min ptu
                        minSelect = min;
                    } else {//max > min
                        //thông báo chọn từ min đến max phần tử
                        thongBao = getString(R.string.select_from_x_to_y_topic, min, max);
                        setStateButtonNext(false);
                        minSelect = min;
                    }
                }
            }*/
            /*if(TextUtils.isEmpty(category.getSubtitle())){
                subTitle.setVisibility(View.GONE);

                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) title.getLayoutParams();
                params.addRule(RelativeLayout.CENTER_VERTICAL);
                title.setLayoutParams(params);
            }else{
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) title.getLayoutParams();
                params.removeRule(RelativeLayout.CENTER_VERTICAL);
                title.setLayoutParams(params);

                subTitle.setVisibility(View.VISIBLE);
                subTitle.setText(isEnglish?category.getSubtitleEN():category.getSubtitle());
            }*/
            btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    whenClickNext();
//                    callNext();
                }
            });
            btnPrevious.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callPrevious();
                }
            });

            //TRANG DAU TIEN KO HIEN NUT BACK
            if (category.getOrder() == 1) {
                btnPrevious.setVisibility(View.GONE);
            } else {
                btnPrevious.setVisibility(View.VISIBLE);
            }


            boolean isCanSkip = category.isCanSkip();//chỉ hiển thị text, và click vào thì cho qua
            if (isCanSkip) {
                btnNext.setText(R.string.skip);
                setStateButtonNext(true);
            } else {
                btnNext.setText(R.string.next);
                //btnNext hiển thị enable theo ben tren
            }

        }
    }


    private void setStateButtonNext(boolean enable) {
        if (enable) {
//            ViewCompat.setBackgroundTintList(btnNext, ContextCompat.getColorStateList(getContext(), R.color.btnNextColor));
            btnNext.setBackgroundResource(R.drawable.bg_sky_radian_rounded);
            btnNext.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        } else {
//            ViewCompat.setBackgroundTintList(btnNext, ContextCompat.getColorStateList(getContext(), R.color.grey_300));
            btnNext.setBackgroundResource(R.drawable.bg_sky_disable_rounded);
            btnNext.setTextColor(ContextCompat.getColor(getContext(), R.color.grey_700));
        }
        btnNext.setEnabled(enable);
    }


    private int spanCount = 3;
    private int spacing = 20;

    private void initRecyclerView() {
        GridLayoutManager mLayoutManager = new GridLayoutManager(getContext(), spanCount);
        rv.setLayoutManager(mLayoutManager);
        rv.setItemAnimator(new DefaultItemAnimator());

        //apply spacing
        spacing = getResources().getDimensionPixelOffset(R.dimen.grid_spacing);
        GridSpacingItemDecoration itemDecoration = new GridSpacingItemDecoration(spanCount, spacing, true);
        rv.addItemDecoration(itemDecoration);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private Fragment_3_Select_Favorite_Adapter adapter;

    private void getHashtags() {
        if (MyUtils.checkInternetConnection(getContext())) {
            if (category != null) {
                MyApplication.apiManager.getHashtags(
                        category.getCategoryId(),
                        new Callback<JsonObject>() {
                            @Override
                            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                JsonObject data = response.body();
                                if (data != null) {
                                    Type collectionType = new TypeToken<ArrayList<Hashtag>>() {
                                    }.getType();
                                    ReturnResult result = Webservices.parseJson(data.toString(), collectionType, true);

                                    if (result != null) {
                                        if (result.getErrorCode() == ReturnResult.SUCCESS) {//da ton tai thi vao
                                            ArrayList<Hashtag> list = (ArrayList<Hashtag>) result.getData();
                                            if (list != null && list.size() > 0) {
                                                adapter = new Fragment_3_Select_Favorite_Adapter(
                                                        getContext(),
                                                        list,
                                                        spanCount,
                                                        spacing,
                                                        category.getMinPick(),
                                                        category.getMaxPick(),
                                                        isEnglish,
                                                        new ItemSelectDeselectListener() {
                                                            @Override
                                                            public void onSelectionChange(int numberItemSelected) {

                                                                //ko ko cho skip thì hiển thị theo min, phai chon đủ min mới đc đi tiếp
                                                                if (!category.isCanSkip()) {
                                                                    //tính toán hiển thị button next
                                                                    int min = category.getMinPick();
                                                                    if (numberItemSelected >= min) {//ko yc chon
                                                                        setStateButtonNext(true);
                                                                    } else {
                                                                        setStateButtonNext(false);
                                                                    }
                                                                } else {
                                                                    //co the skip, neu chon thi next, ko chon la skip
                                                                    if (numberItemSelected > 0) {
                                                                        btnNext.setText(R.string.next);
                                                                    } else {
                                                                        btnNext.setText(R.string.skip);
                                                                    }
                                                                }
                                                            }
                                                        });
                                                rv.setAdapter(adapter);

                                                //neu da chon va thoa dieu kien thi cho enable button de next qua
                                                ArrayList<Hashtag> tags = adapter.getItemSelected();
                                                if (tags.size() > 0) {
                                                    if (tags.size() >= minSelect) {
                                                        btnNext.setText(R.string.next);
                                                        setStateButtonNext(true);
                                                    }
                                                }

                                            }
                                        }
                                    }


                                }

                                //neu ko co du lieu thi cho qua
                                if (adapter == null || (adapter != null && adapter.getItemCount() == 0)) {
                                    setStateButtonNext(true);
                                }
                            }

                            @Override
                            public void onFailure(Call<JsonObject> call, Throwable t) {
                                MyUtils.log("error");
                            }
                        });
            }
        } else {
            MyUtils.showThongBao(getContext());
        }

    }


    private void whenClickNext() {

        if (MyUtils.checkInternetConnection(getContext())) {
            //nếu nhấn skip thì bỏ qua, nếu nhấn next thì mới update
            if (btnNext.getText().equals(getString(R.string.next))) {
                if (adapter != null) {

                    //update tag selected roi den tag unselected
                    //update tag selected
                    ArrayList<Hashtag> tags = adapter.getItemSelected();
                    if (tags.size() > 0) {
                        ArrayList<HashtagShort> data = Hashtag.convert(tags);
//                        String json = new Gson().toJson(data);
                        try {
                            /*JsonArray array = (JsonArray) new Gson().toJsonTree(data,
                                    new TypeToken<List<HashtagShort>>() {}.getType());*/
                            setHashtags(data);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        callNext();
                    }

                } else {
                    callNext();
                }
            } else {//skip thi bo qua man hinh nay
                callNext();
            }
        } else {
            MyUtils.showThongBao(getContext());
        }


    }

    private void callNext() {
        if (getContext() != null) {
            getContext().sendBroadcast(new Intent(SelectFavoritesActivity.ACTION_NEXT));
        }
    }

    private void callPrevious() {
        if (getContext() != null) {
            getContext().sendBroadcast(new Intent(SelectFavoritesActivity.ACTION_PREVIOUS));
        }
    }


    private int numberError = 0;

    private void setHashtags(ArrayList<HashtagShort> hashtags) {
        if (MyUtils.checkInternetConnection(getContext())) {
            if (category != null) {

                MyApplication.apiManager.setHashtags(
                        category.getCategoryId(),
                        hashtags,
                        new Callback<JsonObject>() {
                            @Override
                            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                JsonObject data = response.body();
                                if (data != null) {
                                    Type collectionType = new TypeToken<ArrayList<Hashtag>>() {
                                    }.getType();
                                    ReturnResult result = Webservices.parseJson(data.toString(), collectionType, true);

                                    if (result != null) {
                                        if (result.getErrorCode() == ReturnResult.SUCCESS) {//da ton tai thi vao
                                            callNext();
                                        } else {

                                            //nếu lỗi 3 lần thì cho qua
                                            numberError++;
                                            if (numberError == TinyDB.NUMBER_ERROR_MAX) {
                                                callNext();
                                            } else {
                                                String message = result.getErrorMessage();
                                                if (!TextUtils.isEmpty(message)) {
                                                    MyUtils.showAlertDialog(getContext(), message);
                                                }
                                            }

                                        }
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<JsonObject> call, Throwable t) {
                                MyUtils.log("error");
                            }
                        });
            }
        } else {
            MyUtils.showThongBao(getContext());
        }

    }

}
