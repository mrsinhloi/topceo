/*
 * Created on 2018/09/07.
 * Copyright © 2018–2019 刘振林. All rights reserved.
 */

package com.topceo.mediaplayer.pip.presenter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.util.Preconditions
import com.google.android.material.snackbar.Snackbar
import com.liuzhenlin.common.utils.*
import com.topceo.R
import com.topceo.config.MyApplication
import com.topceo.mediaplayer.pip.*
import com.topceo.mediaplayer.pip.bean.Video
import com.topceo.mediaplayer.pip.bean.VideoDirectory
import com.topceo.mediaplayer.pip.bean.VideoListItem
import com.topceo.mediaplayer.pip.dao.VideoListItemDao
import java.io.File

/**
 * @author 刘振林
 */

private val sDeleteItemExecutor = SerialExecutor()

private fun deleteItemsInternal(items: Array<out VideoListItem>) {
    if (items.isEmpty()) return

    val dao = VideoListItemDao.getSingleton(MyApplication.getInstance())
    sDeleteItemExecutor.execute(object : Runnable {
        override fun run() {
            for (item in items)
                when (item) {
                    is Video -> deleteVideo(item)
                    is VideoDirectory -> {
                        for (video in item.videos) {
                            deleteVideo(video)
                        }
                        dao.deleteVideoDir(item.path)
                    }
                }
        }

        fun deleteVideo(video: Video) {
            // video的路径可能已在主线程中被修改（重命名视频）
            val video2 = dao.queryVideoById(video.id)
            if (video2 != null) {
                File(video2.path).delete()
            }

            dao.deleteVideo(video.id)
        }
    })
}

interface VideoListItemOpCallback<in T : VideoListItem> {
    public val isAsyncDeletingItems get() = !sDeleteItemExecutor.isIdle

    fun showDeleteItemDialog(item: T, onDeleteAction: (() -> Unit)? = null)
    fun showDeleteItemsPopupWindow(vararg items: T, onDeleteAction: (() -> Unit)? = null)
    fun showRenameItemDialog(item: T, onRenameAction: (() -> Unit)? = null)
    fun showItemDetailsDialog(item: T)

    fun deleteItems(vararg items: T) = deleteItemsInternal(items)

    fun renameItem(item: T, newName: String, view: View? = null): Boolean {
        // 如果名称没有变化
        if (newName == item.name) return false

        val context: Context = view?.context ?: MyApplication.getInstance()

        if (item is Video) {
            // 如果不存在该视频文件
            val file = File(item.path)
            if (!file.exists()) {
                if (view == null) {
                    Toast.makeText(context, R.string.renameFailedForThisVideoDoesNotExist,
                            Toast.LENGTH_SHORT).show()
                } else {
                    UiUtils.showUserCancelableSnackbar(view,
                            R.string.renameFailedForThisVideoDoesNotExist, Snackbar.LENGTH_SHORT)
                }
                return false
            }

            val newFile = File(file.parent, newName)
            // 该路径下存在相同名称的视频文件
            if (!newName.equals(item.name, ignoreCase = true) && newFile.exists()) {
                if (view == null) {
                    Toast.makeText(context,
                            R.string.renameFailedForThatDirectoryHasSomeFileWithTheSameName,
                            Toast.LENGTH_SHORT).show()
                } else {
                    UiUtils.showUserCancelableSnackbar(view,
                            R.string.renameFailedForThatDirectoryHasSomeFileWithTheSameName,
                            Snackbar.LENGTH_SHORT)
                }
                return false
            }

            // 如果重命名失败
            if (!file.renameTo(newFile)) {
                if (view == null) {
                    Toast.makeText(context, R.string.renameFailed, Toast.LENGTH_SHORT).show()
                } else {
                    UiUtils.showUserCancelableSnackbar(view,
                            R.string.renameFailed, Snackbar.LENGTH_SHORT)
                }
                return false
            }

            item.name = newName
            item.path = newFile.absolutePath
            return if (VideoListItemDao.getSingleton(context).updateVideo(item)) {
                if (view == null) {
                    Toast.makeText(context, R.string.renameSuccessful, Toast.LENGTH_SHORT).show()
                } else {
                    UiUtils.showUserCancelableSnackbar(view,
                            R.string.renameSuccessful, Snackbar.LENGTH_SHORT)
                }
                true
            } else {
                if (view == null) {
                    Toast.makeText(context, R.string.renameFailed, Toast.LENGTH_SHORT).show()
                } else {
                    UiUtils.showUserCancelableSnackbar(view,
                            R.string.renameFailed, Snackbar.LENGTH_SHORT)
                }
                false
            }
        } else if (item is VideoDirectory) {
            item.name = newName
            return if (VideoListItemDao.getSingleton(context).updateVideoDir(item)) {
                if (view == null) {
                    Toast.makeText(context, R.string.renameSuccessful, Toast.LENGTH_SHORT).show()
                } else {
                    UiUtils.showUserCancelableSnackbar(view,
                            R.string.renameSuccessful, Snackbar.LENGTH_SHORT)
                }
                true
            } else {
                if (view == null) {
                    Toast.makeText(context, R.string.renameFailed, Toast.LENGTH_SHORT).show()
                } else {
                    UiUtils.showUserCancelableSnackbar(view,
                            R.string.renameFailed, Snackbar.LENGTH_SHORT)
                }
                false
            }
        }
        return false
    }
}

fun Fragment.shareVideo(video: Video) {
    (activity ?: requireContext()).shareVideo(video)
}

fun Context?.shareVideo(video: Video) {
    val app = MyApplication.getInstance()
    val context = this ?: app
    if (URLUtils.isNetworkUrl(video.path)) {
        ShareUtils.shareText(context,
                FileUtils.getFileTitleFromFileName(video.name) + "：" + video.path,
                "text/plain")
    } else {
        ShareUtils.shareFile(context, Files.PROVIDER_AUTHORITY, File(video.path), "video/*")
    }
}

@JvmOverloads
fun Context.playVideo(uriString: String, videoTitle: String? = null) {
    startActivity(
            Intent(this, VideoActivityPipDetail::class.java)
                    .setData(Uri.parse(uriString))
                    .putExtra(KEY_VIDEO_TITLE, videoTitle))
}

@JvmOverloads
fun Context.playVideo(uri: Uri, videoTitle: String? = null) {
    startActivity(
            Intent(this, VideoActivityPip::class.java)
                    .setData(uri)
                    .putExtra(KEY_VIDEO_TITLE, videoTitle))
}

@JvmOverloads
fun Context.playVideos(uriStrings: Array<String>, videoTitles: Array<String?>? = null, selection: Int) {
    Preconditions.checkArgument(uriStrings.size == videoTitles?.size ?: true,
            "Array 'videoTitles' can only be null or its size equals the size of Array 'uriStrings'")

    if (uriStrings.isEmpty()) return

    startActivity(
            Intent(this, VideoActivityPip::class.java)
                    .putExtra(KEY_VIDEO_URIS, uriStrings.map { Uri.parse(it) }.toTypedArray())
                    .putExtra(KEY_VIDEO_TITLES, videoTitles)
                    .putExtra(KEY_SELECTION, selection))
}

@JvmOverloads
fun Context.playVideos(uris: Array<Uri>, videoTitles: Array<String?>? = null, selection: Int) {
    Preconditions.checkArgument(uris.size == videoTitles?.size ?: true,
            "Array 'videoTitles' can only be null or its size equals the size of Array 'uris'")

    if (uris.isEmpty()) return

    startActivity(
            Intent(this, VideoActivityPip::class.java)
                    .putExtra(KEY_VIDEO_URIS, uris)
                    .putExtra(KEY_VIDEO_TITLES, videoTitles)
                    .putExtra(KEY_SELECTION, selection))
}

fun Fragment.playVideo(video: Video) {
    startActivityForResult(
            Intent(requireContext(), VideoActivityPip::class.java)
                    .putExtra(KEY_VIDEO, video),
            REQUEST_CODE_PLAY_VIDEO)
}

fun Activity.playVideo(video: Video) {
    startActivityForResult(
            Intent(this, VideoActivityPip::class.java)
                    .putExtra(KEY_VIDEO, video),
            REQUEST_CODE_PLAY_VIDEO)
}

fun Fragment.playVideos(vararg videos: Video, selection: Int) {
    if (videos.isEmpty()) return

    startActivityForResult(
            Intent(requireContext(), VideoActivityPip::class.java)
                    .putExtra(KEY_VIDEOS, videos).putExtra(KEY_SELECTION, selection),
            REQUEST_CODE_PLAY_VIDEOS)
}

fun Activity.playVideos(vararg videos: Video, selection: Int) {
    if (videos.isEmpty()) return

    startActivityForResult(
            Intent(this, VideoActivityPip::class.java)
                    .putExtra(KEY_VIDEOS, videos).putExtra(KEY_SELECTION, selection),
            REQUEST_CODE_PLAY_VIDEOS)
}