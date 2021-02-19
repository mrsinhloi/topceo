package com.topceo.post

import android.graphics.Bitmap
import com.bumptech.glide.Glide
import com.topceo.config.MyApplication
import com.topceo.utils.MyUtils
import kotlinx.coroutines.*

suspend fun getImage(url: String, imgWidth: Int) = Dispatchers.Default {
    val b = Glide.with(MyApplication.context)
            .asBitmap()
            .load(url)
//                .thumbnail(0.1F)
            .override(imgWidth, imgWidth)
            .submit().get()
    return@Default b
}

/*fun getListBitmaps(activity: Fragment, imageList: Array<String>?): ArrayList<Bitmap> {
    var imgs: ArrayList<Bitmap> = ArrayList()
    activity.lifecycleScope.launch {
        withContext(Dispatchers.Default){
            imgs = loadImages(imageList)
        }
    }
    return imgs
}*/

suspend fun loadImages(imageList: Array<String>?): ArrayList<Bitmap> {
    var images: ArrayList<Bitmap> = arrayListOf()
    if (imageList != null) {
        val screenWith = MyUtils.getScreenWidth(MyApplication.context)
        val imgWidth = if (imageList.size > 1) screenWith / 2 else screenWith

        imageList.forEachIndexed { index, path ->
            val b = getImage(path, imgWidth)
            images.add(b)

        }
    }
    return images

}