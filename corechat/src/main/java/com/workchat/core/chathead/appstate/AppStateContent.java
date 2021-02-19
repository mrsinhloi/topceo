/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.workchat.core.chathead.appstate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.workchat.core.chathead.theming.HoverTheme;
import com.workchat.core.chathead.theming.HoverThemeManager;
import com.workchat.corechat.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import io.mattcarroll.hover.Content;

/**
 * {@link Content} that displays the Activity and Service state of the app.
 */
public class AppStateContent extends FrameLayout implements Content {

    private AppStateAdapter mAppStateAdapter;

    public AppStateContent(Context context) {
        super(context);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_app_state_content, this, true);

        mAppStateAdapter = new AppStateAdapter(HoverThemeManager.getInstance().getTheme());
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mAppStateAdapter);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        EventBus.getDefault().unregister(this);
        super.onDetachedFromWindow();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(@NonNull HoverTheme newTheme) {
        mAppStateAdapter.setTheme(newTheme);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(@NonNull AppStateTracker.ActivityStackChangeEvent event) {
        mAppStateAdapter.setActivityStates(event.getActivityStack());
    }

    @NonNull
    @Override
    public View getView() {
        return this;
    }

    @Override
    public boolean isFullscreen() {
        return true;
    }

    @Override
    public void onShown() {
        mAppStateAdapter.setServiceStates(AppStateTracker.getInstance().getServiceStates());
    }

    @Override
    public void onHidden() {

    }
}
