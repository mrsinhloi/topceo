package com.smartapp.post_like_facebook

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_post_like_facebook_demo.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.invoke
import kotlinx.coroutines.launch


class PostLikeFacebookDemoActivity : AppCompatActivity() {
    val context: Context = this
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_like_facebook_demo)

        initSelectImage()
//        showImages()
    }

    val PICKER_REQUEST_CODE = 10
    fun initSelectImage() {
        /*GligarPicker()
                .limit(10)
                .disableCamera(false)
                .cameraDirect(false)
                .requestCode(PICKER_REQUEST_CODE)
                .withActivity(this)
                .show()*/
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PICKER_REQUEST_CODE -> {
                    /*val imageList = data?.extras?.getStringArray(GligarPicker.IMAGES_RESULT)
//                    txt.text = "You are selected ${imageList?.size}"
                    if (imageList != null) {
                        CoroutineScope(Dispatchers.Main).launch {
                            //run on UI thread
                            loadImages(imageList)
                        }

                    }*/

                }
            }
        }
    }

    fun showImages(){
        val imageList = arrayOf( /// 3
                "https://live.staticflickr.com/7893/46373528265_e862e22f2d_o_d.jpg", // v
                "https://live.staticflickr.com/3896/15110568240_9984af3ccb_z.jpg",   // smaller h
                "https://live.staticflickr.com/3896/15110568240_9984af3ccb_z.jpg",   // smaller h
                "https://live.staticflickr.com/3896/15110568240_9984af3ccb_z.jpg",   // smaller h
                "https://live.staticflickr.com/3896/15110568240_9984af3ccb_z.jpg",   // smaller h
                "https://live.staticflickr.com/3896/15110568240_9984af3ccb_z.jpg"    // smaller h
        )
        if (imageList != null) {
            CoroutineScope(Dispatchers.Main).launch {
                //run on UI thread
                loadImages(imageList)
            }

        }
    }

    suspend fun getImage(url: String) = Dispatchers.Default {
        val b = Glide.with(this@PostLikeFacebookDemoActivity).asBitmap().load(url).thumbnail(0.1F).submit().get()
        return@Default b
    }

    suspend fun loadImages(imageList: Array<String>) {
        var images: ArrayList<Bitmap> = arrayListOf()
        /*for (i in 0 until imageList.size){
            val path = imageList.get(i)
            val b = getImage(path)
            images.add(b)
        }*/

        imageList.forEachIndexed { index, path ->
            val b = getImage(path)
            images.add(b)

        }
        feed_post.images = images

    }



}
