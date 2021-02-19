package com.smartapp.collage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import android.media.ExifInterface
import android.os.Build
import android.util.Patterns
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.LayoutRes
import java.io.FileNotFoundException
import java.io.IOException
import java.net.URLConnection

fun Bitmap.isSquare(): Boolean = (height == width)
                || (height > width && height <= width * 1.15)
                || (height < width && height * 1.15 >= width)

fun Bitmap.isVertical():    Boolean = height > width
fun Bitmap.isHorizontal():  Boolean = height < width

fun ViewGroup.inflate(@LayoutRes layoutId: Int): View = LayoutInflater.from(context).inflate(layoutId, this, false)

fun <T> List<T>.second(): T {
    return when {
        size < 2 -> throw NoSuchElementException("List not contains two images")
        else -> this[1]
    }
}

fun String.isValidUrl(): Boolean = Patterns.WEB_URL.matcher(this).matches()

fun getScreenWidth(context: Context): Int {
    var widthScreen = 0
    val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val display = wm.defaultDisplay
    widthScreen = if (Build.VERSION.SDK_INT > 12) {
        val size = Point()
        display.getSize(size)
        size.x
    } else {
        display.width // Deprecated
    }
    return widthScreen
}

fun getScreenHeight(context: Context): Int {
    var height = 0
    val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val display = wm.defaultDisplay
    height = if (Build.VERSION.SDK_INT > 12) {
        val size = Point()
        display.getSize(size)
        size.y
    } else {
        display.height // Deprecated
    }
    return height
}


fun decodeFile(path: String?): Point? {
    try {
        //decode image size
        val o = BitmapFactory.Options()
        o.inJustDecodeBounds = true
        BitmapFactory.decodeFile(path, o)
        //Find the correct scale value. It should be the power of 2.
        val width_tmp = o.outWidth
        val height_tmp = o.outHeight
        return Point(width_tmp, height_tmp)
    } catch (e: FileNotFoundException) {
    }
    return null
}

fun isImageFile(path: String?): Boolean {
    val mimeType: String = URLConnection.guessContentTypeFromName(path)
    return mimeType != null && mimeType.startsWith("image")
}

fun isVideoFile(path: String?): Boolean {
    val mimeType = URLConnection.guessContentTypeFromName(path)
    return mimeType != null && mimeType.startsWith("video")
}

fun getImageOrientation(imageLocalPath: String): Int {
    return try {
        val exifInterface = ExifInterface(imageLocalPath)
        exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
    } catch (e: IOException) {
        e.printStackTrace()
        ExifInterface.ORIENTATION_NORMAL
    }
}

fun getImageSize(imageLocalPath: String): Size {
    val options: BitmapFactory.Options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    /*val bitmap: Bitmap =*/ BitmapFactory.decodeFile(imageLocalPath, options)
    val width: Int = options.outWidth
    val height: Int = options.outHeight

    //kiem tra them orientation vi samsung co khi nhan dang nguoc lai opposite width and height
    val orientation = getImageOrientation(imageLocalPath)
    return when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90, ExifInterface.ORIENTATION_ROTATE_270 -> {
            Size(height, width)
        }
        else -> {
            Size(width, height)
        }
    }
}

