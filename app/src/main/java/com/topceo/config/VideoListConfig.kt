package com.topceo.config

import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.ExoPlaybackException
import com.topceo.R
import com.topceo.autoplayvideo.CameraAnimation
import com.topceo.mediaplayer.masterplayer.ExoPlayerHelper
import com.topceo.mediaplayer.masterplayer.MasterExoPlayer
import com.topceo.mediaplayer.masterplayer.MasterExoPlayerHelper
import com.topceo.mediaplayer.masterplayer.MuteStrategy
import com.topceo.utils.MyUtils

class VideoListConfig {
    companion object {
        fun configVideoAutoPlaying(
            context: Context,
            fragmentOrActivity: Any,
            rv: RecyclerView?
        ): MasterExoPlayerHelper {
            val helper = MasterExoPlayerHelper(
                context,
                R.id.vvInfo,
                true,
                0.5f,
                MuteStrategy.ALL,
                true,
                false,
                0,
                Int.MAX_VALUE
            )

            if (fragmentOrActivity is Fragment) {
                if (fragmentOrActivity.activity != null && !fragmentOrActivity.requireActivity().isFinishing) {
                    helper.makeLifeCycleAware(fragmentOrActivity)
                    fragmentOrActivity.parentFragmentManager.addOnBackStackChangedListener {
                        if (fragmentOrActivity.parentFragmentManager.fragments.last() == fragmentOrActivity) {
                            //resume
                            helper.exoPlayerHelper.play()
                        } else {
                            //pause
                            helper.exoPlayerHelper.pause()
                        }
                    }
                }
            } else if (fragmentOrActivity is AppCompatActivity) {
                helper.makeLifeCycleAware(fragmentOrActivity)
            }

            if (rv != null) {
                helper.attachToRecyclerView(rv)
            }

            return helper
        }


        //////
        fun play(helper: MasterExoPlayerHelper?) {
            helper?.exoPlayerHelper?.play()
        }

        fun pause(helper: MasterExoPlayerHelper?) {
            helper?.exoPlayerHelper?.pause()
        }

        fun stop(helper: MasterExoPlayerHelper?) {
            helper?.exoPlayerHelper?.stop()
        }

        ////
        open fun setMute(vvInfo: MasterExoPlayer?, imgSound: ImageView) {
            if (vvInfo != null) {
                //set icon mute
                if (vvInfo.isMute) {
                    imgSound.setImageResource(R.drawable.ic_volume_off_white_24dp)
                } else {
                    imgSound.setImageResource(R.drawable.ic_volume_up_white_24dp)
                }
            }
        }

        fun configListener(
            vvInfo: MasterExoPlayer,
            imgSound: ImageView,
            ivCameraAnimation: CameraAnimation,
            ivInfo: ImageView
        ) {
            vvInfo.listener = object : ExoPlayerHelper.Listener {
                override fun onPlayerReady() {
                    MyUtils.log("masterplayer onPlayerReady")
                    imgSound.visibility = View.VISIBLE
                    setMute(vvInfo, imgSound)
                }

                override fun onStart() {
                    MyUtils.log("masterplayer onStart ")
                    ivCameraAnimation.visibility = View.GONE
                    ivCameraAnimation.stop()
                    ivInfo.visibility = View.GONE
                }

                override fun onStop() {
                    MyUtils.log("masterplayer onStop ")
                    ivInfo.visibility = View.VISIBLE
                    imgSound.visibility = View.GONE
                }

                override fun onProgress(l: Long) {
                    MyUtils.log("masterplayer progress $l")
                }

                override fun onError(e: ExoPlaybackException?) {
                    MyUtils.log("masterplayer onError ")
                }

                override fun onBuffering(b: Boolean) {
                    ivCameraAnimation.visibility = View.VISIBLE
                    ivCameraAnimation.start()
                    MyUtils.log("masterplayer onBuffering ")
                }

                override fun onToggleControllerVisible(b: Boolean) {
                    MyUtils.log("masterplayer onToggleControllerVisible ")
                }
            }
        }
    }
}