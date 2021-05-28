package com.workchat.core.config

import android.app.Application
import android.content.Intent
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import com.workchat.corechat.BuildConfig


open class App : Application(), LifecycleObserver {

    companion object{
        var eventControlListener: EventControlListener? = null
        val WHEN_APP_MOVE_TO_BACKGROUND  = "WHEN_APP_MOVE_TO_BACKGROUND"
        val WHEN_APP_MOVE_TO_FOREGROUND = "WHEN_APP_MOVE_TO_FOREGROUND"

    }


    override fun onCreate() {
        super.onCreate()
//        EventBus.builder().addIndex( MyEventBusIndex()).installDefaultEventBus()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this@App)
        initLogger()

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    open fun onMoveToForeground() {
        Logger.d("Returning to foreground…")
//        sendBroadcast(Intent(WHEN_APP_MOVE_TO_FOREGROUND))
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    open fun onMoveToBackground() {
        Logger.d("Moving to background…")
//        MyUtils.showAlertDialog(this, "background")
        sendBroadcast(Intent(WHEN_APP_MOVE_TO_BACKGROUND))
    }

    /*@OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    open fun onApplicationDestroy(){
        eventControlListener?.whenAppDestroy()
    }*/


    private fun initLogger() {
        //Logger
        val logFormat = PrettyFormatStrategy.newBuilder()
                .tag("ChatCore")
                .showThreadInfo(true)
                .methodCount(3)
                .build()
        Logger.clearLogAdapters()
        Logger.addLogAdapter(object: AndroidLogAdapter(logFormat) {
            override fun isLoggable(priority: Int, tag: String?): Boolean = BuildConfig.DEBUG
        })
    }

}