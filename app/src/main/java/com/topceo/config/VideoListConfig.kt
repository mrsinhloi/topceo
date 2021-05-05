package com.topceo.config

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.master.exoplayer.MasterExoPlayerHelper
import com.master.exoplayer.MuteStrategy
import com.topceo.R
import com.topceo.views.AutoPlayVideoRecyclerView

class VideoListConfig {
    companion object{
        fun configVideoAutoPlaying(
            context: Context,
            fragmentOrActivity: Any,
            rv: AutoPlayVideoRecyclerView
        ) {
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
            }else if(fragmentOrActivity is AppCompatActivity){
                helper.makeLifeCycleAware(fragmentOrActivity)
            }
            helper.attachToRecyclerView(rv)
        }
    }
}