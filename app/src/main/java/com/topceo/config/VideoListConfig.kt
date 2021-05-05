package com.topceo.config

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.master.exoplayer.MasterExoPlayerHelper
import com.master.exoplayer.MuteStrategy
import com.topceo.R

class VideoListConfig {
    companion object{
        fun configVideoAutoPlaying(
            context: Context,
            fragmentOrActivity: Any,
            rv: RecyclerView
        ):MasterExoPlayerHelper {
            val helper = MasterExoPlayerHelper(
                context,
                R.id.vvInfo,
                true,
                0.65f,
                MuteStrategy.ALL,
                true,
                false,
                0,
                Int.MAX_VALUE
            )

            if(fragmentOrActivity is Fragment) {
                helper.makeLifeCycleAware(fragmentOrActivity)
                fragmentOrActivity.parentFragmentManager.addOnBackStackChangedListener {
                    if(fragmentOrActivity.parentFragmentManager.fragments.last() == fragmentOrActivity){
                        //resume
                        helper.exoPlayerHelper.play()
                    }else{
                        //pause
                        helper.exoPlayerHelper.pause()
                    }
                }
            }else if(fragmentOrActivity is AppCompatActivity){
                helper.makeLifeCycleAware(fragmentOrActivity)
            }
            helper.attachToRecyclerView(rv)

            return helper
        }


        //////
        fun play(helper: MasterExoPlayerHelper?){
            helper?.exoPlayerHelper?.play()
        }
        fun pause(helper: MasterExoPlayerHelper?){
            helper?.exoPlayerHelper?.pause()
        }
        fun stop(helper: MasterExoPlayerHelper?){
            helper?.exoPlayerHelper?.stop()
        }
    }
}