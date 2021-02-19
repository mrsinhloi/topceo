package com.smartapp.post_like_facebook

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.smartapp.collage.MediaLocal
import gun0912.tedimagepicker.builder.TedImagePicker
import gun0912.tedimagepicker.builder.type.MediaType
import kotlinx.android.synthetic.main.activity_edit_image.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule


class EditImageActivity : AppCompatActivity() {
    companion object {
        const val LIST_ITEM: String = "LIST_ITEM"
        const val ITEM_POSITION = "ITEM_POSITION"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_image)

        setSupportActionBar(toolbar)
//        toolbar.setNavigationIcon(R.drawable.svg_16)
        toolbar.setNavigationOnClickListener { finish() }


        initRV()
    }

    var itemPosition: Int = 0
    var selectedUriList: ArrayList<Uri>? = null
    fun isHaveImages(): Boolean {
        return !selectedUriList.isNullOrEmpty()
    }

    lateinit var adapter: ImageAdapter
    fun initRV() {

        //rv
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv.layoutManager = layoutManager
        rv.itemAnimator = DefaultItemAnimator()
        adapter = ImageAdapter(this, ArrayList())
        rv.adapter = adapter
        rv.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                rv.getViewTreeObserver().removeOnGlobalLayoutListener(this)
                if (itemPosition > 0) {
                    scrollTo(itemPosition)
                }

            }
        })

        //data
        selectedUriList = intent?.extras?.getParcelableArrayList(LIST_ITEM)
        if (isHaveImages()) {
            itemPosition = intent?.extras?.getInt(ITEM_POSITION, 0)!!

            selectedUriList?.let {
                val list: ArrayList<MediaLocal> = MediaLocal.getListMediaLocal(it, applicationContext)
                adapter.update(list)
            }
        }

        btnFinish.setOnClickListener {
            val items = adapter.data
            val list: ArrayList<Uri> = MediaLocal.getListUri(items)

            val data = Intent()
            data.putParcelableArrayListExtra(LIST_ITEM, list)
            setResult(Activity.RESULT_OK, data)
            finish()
        }

        btnAddMore.setOnClickListener {
            //select and add them image vao list
            initSelectImage()
        }

    }

    fun scrollTo(position: Int){
        Timer().schedule(1200) {
            if(!isFinishing){
                runOnUiThread {
                    rv.scrollToPosition(position)
                }
            }
        }
    }

    fun initSelectImage() {
        //khi load lai thi phai theo adapter vi adapter co the xoa cac item roi
        val data = adapter.data
        selectedUriList = MediaLocal.getListUri(data)
        val oldSize = data.size

        ////
        TedImagePicker.with(this)
                .mediaType(MediaType.IMAGE)
                .dropDownAlbum()
                .errorListener { message -> Log.d("ted", "message: $message") }
                .selectedUri(selectedUriList)
                .startMultiImage { list: List<Uri> ->
                    list?.let {
                        selectedUriList = ArrayList(list)
                        val items: ArrayList<MediaLocal> = MediaLocal.getListMediaLocal(it, applicationContext)
                        adapter.update(items)

                        //scroll den bottom neu add them 1
                        val newSize = list.size
                        if(newSize>oldSize){
                            scrollTo(list.size-1)
                        }
                    }
                }
    }


}