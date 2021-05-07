/*
 * Created on 2018/09/07.
 * Copyright © 2018 刘振林. All rights reserved.
 */

package com.topceo.mediaplayer.pip.model

import com.topceo.mediaplayer.pip.bean.Video


/**
 * @author 刘振林
 */
interface OnReloadVideosListener {
    fun onReloadVideos(videos: MutableList<Video>?)
}