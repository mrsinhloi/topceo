/*
 * Created on 2019/12/4 5:27 PM.
 * Copyright © 2019 刘振林. All rights reserved.
 */

package com.topceo.mediaplayer.pip.dao;

import android.database.Cursor;

import androidx.annotation.Nullable;

import com.topceo.mediaplayer.pip.bean.VideoDirectory;


/**
 * @author 刘振林
 */
public interface IVideoDirectoryDao {
    boolean insertVideoDir(@Nullable VideoDirectory videodir);

    boolean deleteVideoDir(@Nullable String directory /* directory path */);

    boolean updateVideoDir(@Nullable VideoDirectory videodir);

    @Nullable
    VideoDirectory queryVideoDirByPath(@Nullable String path);

    @Nullable
    Cursor queryAllVideoDirs();
}
