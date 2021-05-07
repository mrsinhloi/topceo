/*
 * Created on 2020-12-8 9:29:19 AM.
 * Copyright © 2020 刘振林. All rights reserved.
 */

package com.topceo.mediaplayer.pip.presenter;

import androidx.annotation.NonNull;


/**
 * @author 刘振林
 */
@SuppressWarnings("rawtypes")
public interface IPresenter<V extends IView> {
    void attachToView(@NonNull V view);
    void detachFromView(@NonNull V view);
}
