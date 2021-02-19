package com.topceo.onesignal

import com.topceo.BuildConfig


class OneSignalLog {
    companion object {
        fun printLog(message: String) {
            if(BuildConfig.DEBUG){
//                println("OneSignalLog: $message")
            }
        }
    }
}