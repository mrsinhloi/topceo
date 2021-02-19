package com.topceo.views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.topceo.viewholders.MyVideoHolder;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public class AutoPlayVideoRecyclerView extends RecyclerView {

    public AutoPlayVideoRecyclerView(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public AutoPlayVideoRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public AutoPlayVideoRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private static final String TAG = "AutoPlayVideoRecyclerVi";
    private PublishSubject<Integer> subject;
    private MyVideoHolder handingVideoHolder;
    private int handingPossition = 0;
    private int newPosition = -1;
    private int heightScreen;

    public MyVideoHolder getHandingVideoHolder() {
        return handingVideoHolder;
    }

    private void initView(Context context) {
        heightScreen = getHeightScreen((Activity) context);
        subject = createSubject();
        addOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                checkPositionHandingViewHolder();
                subject.onNext(dy);
            }
        });

    }

    private void checkPositionHandingViewHolder() {
        if (handingVideoHolder == null) return;
        Observable.just(handingVideoHolder)
                .map(new Function<MyVideoHolder, Float>() {
                    @Override
                    public Float apply(@NonNull MyVideoHolder videoHolder) throws Exception {
                        return getPercentViewHolderInScreen(videoHolder);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Float>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                    }

                    @Override
                    public void onNext(@NonNull Float aFloat) {
                        if (aFloat < 50 && handingVideoHolder != null) {
                            handingVideoHolder.stopVideo();
                            handingVideoHolder = null;
                            handingPossition = -1;
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    private int getHeightScreen(Activity context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    @SuppressLint("CheckResult")
    private PublishSubject<Integer> createSubject() {
        subject = PublishSubject.create();
        subject.debounce(300, TimeUnit.MILLISECONDS)
                .filter(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer value) throws Exception {
                        return true;
                    }
                })
                .switchMap(new Function<Integer, ObservableSource<Integer>>() {
                    @Override
                    public ObservableSource<Integer> apply(Integer value) throws Exception {
                        return Observable.just(value);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer value) throws Exception {
                        playVideo(value);
                    }
                });
        return subject;
    }

    private void playVideo(float value) {
        Observable.just(value)
                .map(new Function<Float, MyVideoHolder>() {
                    @Override
                    public MyVideoHolder apply(@NonNull Float aFloat) throws Exception {
                        MyVideoHolder videoHolder = getViewHolderCenterScreen();
                        if (videoHolder == null) return null;
                        if (videoHolder.equals(handingVideoHolder) && handingPossition == newPosition)
                            return null;
                        handingPossition = newPosition;
                        return videoHolder;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MyVideoHolder>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                    }

                    @Override
                    public void onNext(@NonNull MyVideoHolder videoHolder) {
                        if (handingVideoHolder != null) handingVideoHolder.stopVideo();
                        videoHolder.playVideo();

                        handingVideoHolder = videoHolder;
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

//    private void playVideo() {
//        VideoHolder videoHolder = getViewHolderCenterScreen();
//        if (videoHolder == null) return;
//        if (videoHolder.equals(handingVideoHolder) && handingPossition == newPosition) return;
//        handingPossition = newPosition;
//
//        if (handingVideoHolder != null) handingVideoHolder.stopVideo();
//        videoHolder.playVideo();
//
//        handingVideoHolder = videoHolder;
//    }

    private MyVideoHolder getViewHolderCenterScreen() {
        int[] limitPosition = getLimitPositionInScreen();
        int min = limitPosition[0];
        int max = limitPosition[1];

        MyVideoHolder viewHolderMax = null;
        float percentMax = 0;

        for (int i = min; i <= max; i++) {
            ViewHolder viewHolder = findViewHolderForAdapterPosition(i);
            if (!(viewHolder instanceof MyVideoHolder)) continue;
            float percentViewHolder = getPercentViewHolderInScreen((MyVideoHolder) viewHolder);
            if (percentViewHolder > percentMax && percentViewHolder >= 50) {
                percentMax = percentViewHolder;
                viewHolderMax = (MyVideoHolder) viewHolder;
                newPosition = i;
            }
        }
        return viewHolderMax;
    }

    /**
     * tính toán tỷ lệ phần trăm video trên màn hình
     *
     * @param viewHolder holder cần kiểm tra
     * @return tỷ lệ phần trăm
     */
    private float getPercentViewHolderInScreen(MyVideoHolder viewHolder) {
        if (viewHolder == null) return 0;
        View view = viewHolder.getVideoLayout();

        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int viewHeight = view.getHeight();
        int viewFromY = location[1];
        int viewToY = location[1] + viewHeight;

        if (viewFromY >= 0 && viewToY <= heightScreen) return 100;
        if (viewFromY < 0 && viewToY > heightScreen) return 100;
        if (viewFromY < 0 && viewToY <= heightScreen)
            return ((float) (viewToY - (-viewFromY)) / viewHeight) * 100;
        if (viewFromY >= 0 && viewToY > heightScreen)
            return ((float) (heightScreen - viewFromY) / viewHeight) * 100;
        return 0;
    }

    private int[] getLimitPositionInScreen() {
        int findFirstVisibleItemPosition = ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
        int findFirstCompletelyVisibleItemPosition = ((LinearLayoutManager) getLayoutManager()).findFirstCompletelyVisibleItemPosition();
        int findLastVisibleItemPosition = ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
        int findLastCompletelyVisibleItemPosition = ((LinearLayoutManager) getLayoutManager()).findLastCompletelyVisibleItemPosition();

        int min = Math.min(Math.min(findFirstVisibleItemPosition, findFirstCompletelyVisibleItemPosition),
                Math.min(findLastVisibleItemPosition, findLastCompletelyVisibleItemPosition));
        int max = Math.max(Math.max(findFirstVisibleItemPosition, findFirstCompletelyVisibleItemPosition),
                Math.max(findLastVisibleItemPosition, findLastCompletelyVisibleItemPosition));

        return new int[]{min, max};
    }
}
