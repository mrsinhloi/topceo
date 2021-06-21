package com.myxteam.videocompression

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.os.SystemClock
import android.util.Log
import android.view.View
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.otaliastudios.transcoder.Transcoder
import com.otaliastudios.transcoder.TranscoderListener
import com.otaliastudios.transcoder.TranscoderOptions
import com.otaliastudios.transcoder.common.TrackStatus
import com.otaliastudios.transcoder.common.TrackType
import com.otaliastudios.transcoder.source.DataSource
import com.otaliastudios.transcoder.source.TrimDataSource
import com.otaliastudios.transcoder.source.UriDataSource
import com.otaliastudios.transcoder.strategy.*
import com.otaliastudios.transcoder.validator.DefaultValidator
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class VideoCompression(
    var context: Context,
    var videoPath: String,
    var listener: CompressionListener,
    var progressIndicator: LinearProgressIndicator? = null,
    var framerate: Int = 24,
    var height: Int = DEFAULT_HEIGHT,
    var width: Int = DEFAULT_WIDTH,
    var isMute: Boolean = false
) {
    companion object {
        const val DEFAULT_HEIGHT = 720//960
        const val DEFAULT_WIDTH = 405//540
    }

    fun showProgress() {
        progressIndicator?.run {
            progress = 0
            max = 100
            visibility = View.VISIBLE
        }
    }

    fun hideProgress() {
        progressIndicator?.run {
            progress = 0
            visibility = View.INVISIBLE
        }
    }

    lateinit var videoStrategy: DefaultVideoStrategy
    lateinit var audioStrategy: TrackStrategy

    init {
        showProgress()

        videoStrategy = DefaultVideoStrategy.atMost(width, height) //exact(height, width) //
            .frameRate(framerate)
            .build()

        if (isMute) {
            audioStrategy = RemoveTrackStrategy()
        } else {
            audioStrategy = DefaultAudioStrategy.builder()
                .channels(DefaultAudioStrategy.CHANNELS_AS_INPUT)
                .sampleRate(DefaultAudioStrategy.SAMPLE_RATE_AS_INPUT)
                .build()
        }

    }


    fun compress() {
        val start = SystemClock.elapsedRealtime()

        val uri = Uri.fromFile(File(videoPath))
        val source = UriDataSource(context, uri)

        var compressed = createVideoFile(context)
        Transcoder.into(compressed.absolutePath)
            .addDataSource(source)
            .setVideoTrackStrategy(videoStrategy) //DefaultVideoStrategies.for720x1280()
            .setAudioTrackStrategy(audioStrategy)
            .setListener(object : TranscoderListener {
                override fun onTranscodeProgress(progress: Double) {
                    val number = (progress * 100).toInt()
                    progressIndicator?.progress = number
                }

                override fun onTranscodeCompleted(successCode: Int) {
                    val howlong = (SystemClock.elapsedRealtime() - start) / 1000
                    printFileSize(compressed.absolutePath, howlong)
                    //return compressed.absolutePath
                    listener.onSuccess(compressed.absolutePath)
                }

                override fun onTranscodeCanceled() {
                    hideProgress()
                    listener.onCancel()
                }

                override fun onTranscodeFailed(exception: Throwable) {
                    hideProgress()
                    listener.onFailed(exception)
                }

            }).transcode()

    }

    fun createVideoFile(context: Context): File {
        // Create an video file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "VIDEO_COMPRESSED_$timeStamp.mp4"
        val getImage = context.externalCacheDir
        return if (getImage != null) {
            File(getImage.path, imageFileName)
        } else {
            val storageDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            File(storageDir.path, imageFileName)
        }
    }


    fun printFileSize(path: String, howlong: Long) {
        val file = File(path)
        val kb = file.length() / 1024
        val mb = kb / 1024
        val msg = if (mb > 0) {
            "$mb MB"
        } else {
            "$kb KB"
        }

        if (howlong > 0) {
            Log.d("File", "File size = $msg in $howlong seconds, $path")
        } else {
            Log.d("File", "File size = $msg, $path")
        }
    }
}