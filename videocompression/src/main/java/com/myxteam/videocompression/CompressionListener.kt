package com.myxteam.videocompression

interface CompressionListener {
    fun onSuccess(path:String)
    fun onCancel()
    fun onFailed(exception:Throwable)
}