package com.workchat.core.notification.model

import android.content.Context
import android.graphics.*
import android.util.TypedValue
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import java.util.*

abstract class BaseNotification(
        var _id: String = "",
        var userId: String = "",
        var createUserId: String = "",
        var title: String = "",
        var message: String = "",
        var image: String = "",
        var type: String = "",
        var createDate: Long = 0,
        var isView: Boolean = false,
        var viewDate: String? = null) {

    fun Context.textBitmap(text: String): Bitmap {
        val imgColor by lazy {
            val rand = Random()
            Color.rgb(rand.nextInt(128), rand.nextInt(128), rand.nextInt(128))
        }
        val textRect = Rect()
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textAlign = Paint.Align.CENTER
            textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14f, resources.displayMetrics)
        }
        val imgBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        Canvas(imgBitmap).apply {
            textPaint.getTextBounds(text, 0, 1, textRect)
            drawColor(imgColor)
            drawText(text, 50f, 50f - textRect.exactCenterY() - (textPaint.descent() / 4), textPaint)
        }
        return imgBitmap
    }

    companion object {

        const val KEY = "BaseNotification_KEY"

        val IS_VIEWED = "IS_VIEWED"
        val LIST_STRING_IDS = "LIST_STRING_IDS"

        fun parseListIds(context: Context?, vararg args: Any): ArrayList<String>? {
            var list: ArrayList<String>? = null
            try {
                val json = args[0] as JSONObject
                if (json.has("errorCode")) {
                    val code = json.getInt("errorCode")
                    if (code == 0) {
                        if (json.has("data")) {
                            val data = json.getJSONObject("data")
                            val listIds = data.getString("notifyIds")
                            val type = object : TypeToken<List<String>>() {}.type
                            list = Gson().fromJson<ArrayList<String>>(listIds, type)
                        }
                    } else {
//                        val errMessage = json.getString("error")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return list
        }

    }
}