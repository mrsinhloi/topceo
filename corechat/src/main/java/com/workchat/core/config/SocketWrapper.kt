package com.workchat.core.api

import android.os.Handler
import android.os.Looper
import com.orhanobut.logger.Logger
import com.sonhvp.utilities.json.toJSON
import io.socket.client.Ack
import io.socket.client.Socket
import org.json.JSONObject

inline fun <reified T> Array<Any>.getData(): T? {
    return runCatching {
        when {
            isEmpty() -> Logger.d("args is null")
            else -> first().toJSON().run {
                val errorCode = optInt("errorCode", -5011)
                val error = optString("error", "null")
                return@runCatching if (errorCode == 0) {
                    opt("data")
                } else {
                    Logger.d("get data from args error \nerrorCode: $errorCode \nerror: $error")
                    null
                }
            }
        }
    }.run {
        exceptionOrNull()?.printStackTrace()
        when {
            isSuccess -> getOrNull() as T?
            isFailure -> null
            else -> null
        }
    }
}

inline fun <reified T> Socket.emit(
        event: String,
        obj: JSONObject = JSONObject(),
        crossinline onSuccess: () -> Unit = {},
        crossinline onResult: (data: T) -> Unit = { _ -> },
        crossinline onFailure: (errorCode: Int, error: String) -> Unit = { _, _ -> }
) {
    this.emit(event, obj, Ack { args ->
        args.run {
            when {
                isEmpty() -> Logger.d("args is null")
                else -> first().toJSON().run {
                    val errorCode = optInt("errorCode", -5011)
                    val error = optString("error", "null")
                    if (errorCode == 0) {
                        val data: T? = opt("data") as T?
                        onSuccess()
                        if (data != null) onResult(data)
                    } else {
                        Handler(Looper.getMainLooper()).post {
                            Logger.d("$event: failure \nerrorCode: $errorCode \nerror: $error")
                        }
                        onFailure(errorCode, error)
                    }
                }
            }
        }
    })

    /*this.emit(event, obj, object : AckWithTimeOut(AckWithTimeOut.TIME_OUT) {
        override fun call(vararg args: Any?) {
            super.call(*args)
            args.run {

                if (args[0].toString().equals(AckWithTimeOut.NO_ACK, ignoreCase = true)) {
                    //timeout
                    onFailure(AckWithTimeOut.NO_ACK_ERROR_CODE, AckWithTimeOut.NO_ACK)
                }else{
                    when {
                        isEmpty() -> Logger.d("args is null")
                        else -> first()!!.toJSON().run {
                            val errorCode = optInt("errorCode", -5011)
                            val error = optString("error", "null")
                            if (errorCode == 0) {
                                val data: T? = opt("data") as T?
                                onSuccess()
                                if (data != null) onResult(data)
                            } else {
                                Handler(Looper.getMainLooper()).post {
                                    Logger.d("$event: failure \nerrorCode: $errorCode \nerror: $error")
                                }
                                onFailure(errorCode, error)
                            }
                        }
                    }
                }
            }
        }
    })*/
}