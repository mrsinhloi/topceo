/*
 * Created on 2020-6-18 8:34:50 PM.
 * Copyright © 2020 刘振林. All rights reserved.
 */

package com.topceo.mediaplayer.pip.model

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import com.liuzhenlin.common.utils.Executors
import com.topceo.mediaplayer.pip.bean.Video
import com.topceo.mediaplayer.pip.bean.VideoDirectory
import com.topceo.mediaplayer.pip.dao.VideoListItemDao
import com.topceo.mediaplayer.pip.reordered
import java.util.*

/**
 * @author 刘振林
 */
class LocalFoldedVideoListModel(private val videodir: VideoDirectory, context: Context)
    : BaseModel<Nothing, MutableList<Video>?>(context) {

    override fun createAndStartLoader(): AsyncTask<*, *, *> {
        val loader = LoadDirectoryVideosTask()
        loader.executeOnExecutor(Executors.THREAD_POOL_EXECUTOR)
        return loader
    }

    @SuppressLint("StaticFieldLeak")
    private inner class LoadDirectoryVideosTask : Loader<Void>() {

        override fun doInBackground(vararg params: Void?): MutableList<Video>? {
            val dao = VideoListItemDao.getSingleton(mContext)

            var videos: MutableList<Video>? = null

            val videoCursor = dao.queryAllVideosInDirectory(videodir.path) ?: return null
            while (!isCancelled && videoCursor.moveToNext()) {
                val video = dao.buildVideo(videoCursor) ?: continue
                if (videos == null)
                    videos = LinkedList()
                videos.add(video)
            }
            videoCursor.close()

            return videos?.reordered()
        }
    }
}