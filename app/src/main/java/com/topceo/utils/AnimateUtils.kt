package com.topceo.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.topceo.R
import com.topceo.config.MyApplication

class AnimateUtils {
    companion object {
        var animationOffline: AnimatorSet? = null
        fun animate(linearLayout: LinearLayout) {
            linearLayout.setBackgroundResource(R.color.orange_500)
            //start voi animation
            val fadeIn = ObjectAnimator.ofFloat(linearLayout, "alpha", .3f, 1f)
            fadeIn.duration = 2500
            animationOffline = AnimatorSet()
            animationOffline!!.play(fadeIn)
            animationOffline!!.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    linearLayout.setBackgroundResource(R.color.white)
                }
            })
            animationOffline!!.start()
        }
    }
}