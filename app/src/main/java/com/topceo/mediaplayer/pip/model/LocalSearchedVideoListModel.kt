/*
 * Created on 2020-6-18 8:35:03 PM.
 * Copyright © 2020 刘振林. All rights reserved.
 */

package com.topceo.mediaplayer.pip.model

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import com.liuzhenlin.common.utils.Executors
import com.topceo.mediaplayer.pip.bean.Video
import com.topceo.mediaplayer.pip.dao.VideoListItemDao
import com.topceo.mediaplayer.pip.sortByElementName

/**
 * @author 刘振林
 */
class LocalSearchedVideoListModel(context: Context) : BaseModel<Nothing, MutableList<Video>?>(context) {

    override fun createAndStartLoader(): AsyncTask<*, *, *> {
        val loader = LoadVideosTask()
        loader.executeOnExecutor(Executors.THREAD_POOL_EXECUTOR)
        return loader
    }

    @SuppressLint("StaticFieldLeak")
    private inner class LoadVideosTask : Loader<Void>() {

        override fun doInBackground(vararg voids: Void): MutableList<Video>? {
            val dao = VideoListItemDao.getSingleton(mContext)

            var videos: MutableList<Video>? = null

            val videoCursor = dao.queryAllVideos() ?: return null
            while (!isCancelled && videoCursor.moveToNext()) {
                val video = dao.buildVideo(videoCursor)
                if (video != null) {
                    if (videos == null) videos = mutableListOf()
                    videos.add(video)
                }
            }
            videoCursor.close()

            videos.sortByElementName()
            return videos
        }
    }
}